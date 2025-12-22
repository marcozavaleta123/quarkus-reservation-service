package com.app.application.service;

import java.util.List;

import com.app.application.port.in.BookingUseCase;
import com.app.application.port.out.BookingRepositoryOutPort;
import com.app.domain.model.Booking;
import com.app.infraestructure.controller.response.BookingResponse;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class BookingService implements BookingUseCase {
	
	private final BookingRepositoryOutPort bookingRepositoryOutPort;

	@Override
	public Uni<String> createBooking(Booking booking) {
		return bookingRepositoryOutPort.save(booking);
	}

	@Override
	public Uni<String> cancelBooking(Long id) {
		return bookingRepositoryOutPort.cancelBooking(id);
	}

	@Override
	public Uni<List<BookingResponse>> getBooking() {
		return bookingRepositoryOutPort.getBooking();
	}

}
