package com.app.infraestructure.persistence;

import java.util.List;

import org.modelmapper.ModelMapper;

import com.app.application.port.out.BookingRepositoryOutPort;
import com.app.domain.model.Booking;
import com.app.infraestructure.enums.StatusBooking;
import com.app.infraestructure.persistence.entity.BookingEntity;
import com.app.infraestructure.persistence.entity.ClientEntity;
import com.app.infraestructure.persistence.entity.ProfessionalEntity;
import com.app.infraestructure.persistence.mapper.BookingMapper;

import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class JpaBookingRepositoryAdapter implements BookingRepositoryOutPort {

	private final BookingMapper bookingMapper;
	private final ModelMapper modelMapper;

	@Override
	public Uni<Long> save(Booking booking, Long professionalId, Long clientId) {
		Uni<ProfessionalEntity> pEntity = ProfessionalEntity.findById(professionalId);
	    Uni<ClientEntity> cEntity = ClientEntity.findById(clientId);
	    
	    return Uni.combine().all().unis(pEntity, cEntity).asTuple()
	            .flatMap(result -> {
	            	ProfessionalEntity professionalEntity = result.getItem1();
	            	ClientEntity clientEntity   = result.getItem2();
	            	
	            	BookingEntity entity = modelMapper.map(booking, BookingEntity.class);
					entity.setProfessionalEntity(professionalEntity);
					entity.setClientEntity(clientEntity);
					entity.setStatus(StatusBooking.CREADA);
					
					return BookingEntity.persist(entity).map(s -> entity.getId());
	            });
	}

	@Override
	public Uni<Boolean> cancelBooking(Long id) {
		return BookingEntity.update("status = :status where id = :id",
				Parameters.with("status", StatusBooking.CANCELADA).and("id", id))
				.map(r -> r > 0);
				          
	}

	@Override
	public Uni<List<Booking>> getBooking() {
		return BookingEntity.<BookingEntity>find(
		        "select b from BookingEntity b " +
		        "join fetch b.clientEntity " +
		        "join fetch b.professionalEntity"
		    ).list()
				.map(bookingMapper::toDomainList);
	}

	@Override
	public Uni<List<Booking>> getBookingByProfessionalId(Long professionalId) {
		return BookingEntity.<BookingEntity>find(
		        "select b from BookingEntity b " +
		        "join fetch b.clientEntity " +
		        "join fetch b.professionalEntity " +
		        " where b.professionalEntity.id = ?1 and b.status = ?2", professionalId , StatusBooking.CREADA
		    ).list()
				.map(bookingMapper::toDomainList);
	}

}
