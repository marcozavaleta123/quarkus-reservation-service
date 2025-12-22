package com.app.infraestructure.controller;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.modelmapper.ModelMapper;

import com.app.application.port.in.BookingUseCase;
import com.app.domain.model.Booking;
import com.app.infraestructure.controller.request.BookingRequest;
import com.app.infraestructure.util.Result;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Path("/bookings")
public class BookingController {
	
	private final BookingUseCase bookingUseCase;
	private final ModelMapper modelMapper;
	
	@Operation(summary = "Registra las reservas solo si el horario elegido se encuentra disponible x el profesional seleccionado", description = "")
	@APIResponses({
			@APIResponse(responseCode = "200", description = "Creación exitosa", content = @Content(schema = @Schema(implementation = Uni.class))) })
	@WithTransaction
	@POST
	public Uni<Response> createBooking(@Valid BookingRequest bookingRequest) {
		Booking booking = modelMapper.map(bookingRequest, Booking.class);
		return bookingUseCase.createBooking(booking).map(msg -> Result.builder().data(msg).build())
				.map(result -> Response.ok(result).build());
	}
	
	@WithTransaction
	@PUT
	@Path("/{id}/cancel")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<Response> cancelBooking(@PathParam("id") Long id) {
		return bookingUseCase.cancelBooking(id)
				.map(msg -> Result.builder().data(msg).build())
				.map(result -> Response.ok(result).build());
	}
	
	@WithSession
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<Response> getBooking() {
		return bookingUseCase.getBooking()
				.map(list -> Result.builder().data(list).build())
				.map(result -> Response.ok(result).build());
	}

}
