package com.app.infraestructure.persistence;

import static java.util.Objects.isNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;

import com.app.application.port.out.BookingRepositoryOutPort;
import com.app.domain.model.Booking;
import com.app.infraestructure.controller.response.BookingResponse;
import com.app.infraestructure.enums.StatusBooking;
import com.app.infraestructure.exception.BusinessErrorType;
import com.app.infraestructure.exception.BusinessException;
import com.app.infraestructure.persistence.entity.BookingEntity;
import com.app.infraestructure.persistence.entity.ClientEntity;
import com.app.infraestructure.persistence.entity.ProfessionalEntity;
import com.app.infraestructure.persistence.entity.ScheduleEntity;

import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class JpaBookingRepositoryAdapter implements BookingRepositoryOutPort {

	private final ModelMapper modelMapper;

	@Override
	public Uni<String> save(Booking booking) {
		return ProfessionalEntity.find("dni = ?1 and statusActive = ?2", booking.getProfessionalDni(), true)
				.firstResult().flatMap(professional -> {
					if (isNull(professional)) {
						return Uni.createFrom().failure(new BusinessException(BusinessErrorType.VALIDATION_ERROR,
								"el DNI del profesional ingresado no se encuentra registrado y/o inactivo"));
					}

					return ClientEntity.find("dni = ?1 and statusActive = ?2", booking.getClientDni(), true)
							.firstResult().flatMap(client -> {
								if (isNull(client)) {
									return Uni.createFrom().failure(new BusinessException(
											BusinessErrorType.VALIDATION_ERROR,
											"el DNI del cliente ingresado no se encuentra registrado y/o inactivo"));
								}

								ProfessionalEntity professionalEntity = (ProfessionalEntity) professional;
								ClientEntity clientEntity = (ClientEntity) client;

								return ScheduleEntity.<ScheduleEntity>find("professionalEntity.id = ?1 and date = ?2 and status = ?3", professionalEntity.getId(), booking.getDate(), true).list()
										.flatMap(list -> {
											for (ScheduleEntity sc : list) {
												if ((booking.getStartTime().compareTo(sc.getStartTime()) >= 0 && booking.getStartTime().compareTo(sc.getEndTime()) <= 0)
													|| (booking.getEndTime().compareTo(sc.getStartTime()) > 0 && booking.getEndTime().compareTo(sc.getEndTime()) < 0)) {
													return Uni.createFrom().failure(new BusinessException(
															BusinessErrorType.VALIDATION_ERROR,
															"el profesional seleccionado no cuenta con disponibilidad en el horario elegido"));
												}
											}

											return BookingEntity.<BookingEntity>find("professionalEntity.id = ?1 and date = ?2 and status = ?3", professionalEntity.getId(), booking.getDate(), StatusBooking.CREADA).list()
													.flatMap(blist -> {
														for (BookingEntity bo : blist) {
															if ((booking.getStartTime().compareTo(bo.getStartTime()) >= 0 && booking.getStartTime().compareTo(bo.getEndTime()) <= 0)
																	|| (booking.getEndTime().compareTo(bo.getStartTime()) > 0 && booking.getEndTime().compareTo(bo.getEndTime()) <= 0)
																	|| (booking.getStartTime().compareTo(bo.getStartTime()) < 0 && booking.getEndTime().compareTo(bo.getEndTime()) > 0)) {
																return Uni.createFrom().failure(new BusinessException(
																		BusinessErrorType.VALIDATION_ERROR,
																		"no se pudo registrar la reserva porque el horario elegido con el profesional seleccionado ya se encuentra ocupado"));
															}
														}

														BookingEntity entity = modelMapper.map(booking, BookingEntity.class);
														entity.setProfessionalEntity(professionalEntity);
														entity.setClientEntity(clientEntity);
														entity.setStatus(StatusBooking.CREADA);

														return BookingEntity.persist(entity).replaceWith("Reserva creada correctamente");
													});

										});
							});
				});
	}

	@Override
	public Uni<String> cancelBooking(Long id) {
		return BookingEntity.count("id", id)
	            .map(count -> count > 0)
	            .flatMap(exists -> {
	                return BookingEntity.update("status = :status where id = :id",
	                	    Parameters.with("status", StatusBooking.CANCELADA).and("id", id))
	                		 .flatMap(rows -> {
	                		        if (rows > 0) {
	                		        	return Uni.createFrom().item("La reserva cuyo id es : " + id + " se canceló correctamente");
	                		        }
	                		        return Uni.createFrom().item("El id de la reserva ingresada no se encuentra registrado");
	                		    });
	            });
	}

	@Override
	public Uni<List<BookingResponse>> getBooking() {
		return BookingEntity.<BookingEntity>find(
		        "select b from BookingEntity b " +
		        "join fetch b.clientEntity " +
		        "join fetch b.professionalEntity"
		    ).list()
		    .map(list -> {
		    	List<BookingResponse> bookingResponseList = new ArrayList<>();
		    	List<LocalDate> dateList = list.stream().map(l -> l.getDate()).distinct().sorted().toList();
		    	for (LocalDate date : dateList) {
		    		List<String> detailList = new ArrayList<>();
		    		List<BookingEntity> bookingDateList = list.stream().filter(l -> l.getDate().compareTo(date) == 0).toList();
		    		for(int i = 0; i < bookingDateList.size(); i ++) {
		    			String detail = "Reserva " + (i + 1)
		    					+ " , Estado : " + bookingDateList.get(i).getStatus() 
		    					+ " (Hora de inicio : " + bookingDateList.get(i).getStartTime() + " - Hora final : " + bookingDateList.get(i).getEndTime()
		    					+ " , Cliente : " + bookingDateList.get(i).getClientEntity().getFirstName() + " " + bookingDateList.get(i).getClientEntity().getLastName() 
		    					+ " , Profesional : " + bookingDateList.get(i).getProfessionalEntity().getFirstName() + " " + bookingDateList.get(i).getProfessionalEntity().getLastName() + ")";
		    			detailList.add(detail);
		    		}
		    		BookingResponse br = BookingResponse.builder().date(date) .details(detailList) .build();
		    		bookingResponseList.add(br);
		    	}
		    	
		    	return bookingResponseList;
		    });
	}

}
