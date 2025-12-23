package com.app.application.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.app.application.finder.ClientFinder;
import com.app.application.finder.ProfessionalFinder;
import com.app.application.finder.ScheduleFinder;
import com.app.application.port.in.BookingUseCase;
import com.app.application.port.out.BookingRepositoryOutPort;
import com.app.domain.model.Booking;
import com.app.infraestructure.controller.response.BookingResponse;
import com.app.infraestructure.exception.BusinessErrorType;
import com.app.infraestructure.exception.BusinessException;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class BookingService implements BookingUseCase {
	
	private final ProfessionalFinder professionalFinder;
	private final ClientFinder clientFinder;
	private final ScheduleFinder scheduleFinder;
	private final BookingRepositoryOutPort bookingRepositoryOutPort;

	@WithTransaction
	@Override
	public Uni<String> createBooking(Booking booking, String professionalDni, String clientDni) {
		return professionalFinder.findByDni(professionalDni)
				.flatMap(professional -> {
					return clientFinder.findByDni(clientDni)
							    .flatMap(client -> {
							    	return scheduleFinder.findByProfessionalIdAndDate(professional.getId(), booking.getDate())
							    			.invoke(scheduleList -> professionalFinder.validateSchedule(scheduleList, booking.getStartTime(), booking.getEndTime()))
							    			.replaceWith(bookingRepositoryOutPort.getBookingByProfessionalId(professional.getId()))
							    			.invoke(bookingList -> validateBooking(bookingList, booking))
							    			.flatMap(bookingList -> bookingRepositoryOutPort.save(booking, professional.getId(), client.getId()))
							    			.map(id -> "Reserva creada correctamente");
							    });
				});
	}

	@WithTransaction
	@Override
	public Uni<String> cancelBooking(Long id) {
		return bookingRepositoryOutPort.cancelBooking(id).flatMap(b -> {
			if (b) {
				return Uni.createFrom().item("La reserva cuyo id es : " + id + " se canceló correctamente");
			}
			return Uni.createFrom().item("El id de la reserva ingresada no se encuentra registrado");
		});
	}

	@WithTransaction
	@Override
	public Uni<List<BookingResponse>> getBooking() {
		return bookingRepositoryOutPort.getBooking()
		    .map(list -> {
		    	List<BookingResponse> bookingResponseList = new ArrayList<>();
		    	List<LocalDate> dateList = getDateList(list);
		    	
		    	for (LocalDate date : dateList) {
		    		List<String> detailList = getDetailBooking(date, list);
		    		
		    		BookingResponse br = BookingResponse.builder()
		    				                            .date(date)
		    				                            .details(detailList) 
		    				                            .build();
		    		
		    		bookingResponseList.add(br);
		    	}
		    	
		    	return bookingResponseList;
		    });
	}
	
	private List<LocalDate> getDateList(List<Booking> bookingList) {
		return bookingList.stream().map(l -> l.getDate())
				                   .distinct().sorted()
				                   .toList();
	}

	private List<String> getDetailBooking(LocalDate date, List<Booking> bookingList) {
		List<Booking> bookingDateList = bookingList.stream()
				                                   .filter(l -> l.getDate().compareTo(date) == 0)
				                                   .toList();
		
		List<String> detailList = new ArrayList<>();
		
		for(int i = 0; i < bookingDateList.size(); i ++) {
			String detail = "Reserva " + (i + 1)
					+ " , Estado : " + bookingDateList.get(i).getStatus() 
					+ " (Hora de inicio : " + bookingDateList.get(i).getStartTime() + " - Hora final : " + bookingDateList.get(i).getEndTime()
					+ " , Cliente : " + bookingDateList.get(i).getClient().getFirstName() + " " + bookingDateList.get(i).getClient().getLastName() 
					+ " , Profesional : " + bookingDateList.get(i).getProfessional().getFirstName() + " " + bookingDateList.get(i).getProfessional().getLastName() + ")";
			detailList.add(detail);
		}
		
		return detailList;
	}
	
	private void validateBooking(List<Booking> bookingList, Booking booking) {
		for (Booking sc : bookingList) {
			if ((booking.getStartTime().compareTo(sc.getStartTime()) > 0 && booking.getStartTime().compareTo(sc.getEndTime()) < 0)
				|| (booking.getEndTime().compareTo(sc.getStartTime()) > 0 && booking.getEndTime().compareTo(sc.getEndTime()) < 0)
				|| (booking.getStartTime().compareTo(sc.getStartTime()) < 0 && booking.getEndTime().compareTo(sc.getEndTime()) >= 0)
				|| (booking.getStartTime().compareTo(sc.getStartTime()) == 0 && booking.getEndTime().compareTo(sc.getEndTime()) == 0)) {
				throw new BusinessException(
						BusinessErrorType.VALIDATION_ERROR,
						"el profesional seleccionado ya cuenta con otra reserva en el horario elegido");
			}
		}
	}
}
