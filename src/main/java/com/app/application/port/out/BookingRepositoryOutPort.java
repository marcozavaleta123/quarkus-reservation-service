package com.app.application.port.out;

import java.util.List;

import com.app.domain.model.Booking;
import com.app.infraestructure.controller.response.BookingResponse;

import io.smallrye.mutiny.Uni;

public interface BookingRepositoryOutPort {
	
	Uni<String> save(Booking booking);
	
	Uni<String> cancelBooking(Long id);
	
	Uni<List<BookingResponse>> getBooking();

}
