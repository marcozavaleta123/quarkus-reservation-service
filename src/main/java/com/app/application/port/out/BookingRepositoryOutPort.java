package com.app.application.port.out;

import java.util.List;

import com.app.domain.model.Booking;

import io.smallrye.mutiny.Uni;

public interface BookingRepositoryOutPort {
	
	Uni<Long> save(Booking booking, Long professionalDni, Long clientDni);
	
	Uni<Boolean> cancelBooking(Long id);
	
	Uni<List<Booking>> getBooking();
	
	Uni<List<Booking>> getBookingByProfessionalId(Long professionalId);

}
