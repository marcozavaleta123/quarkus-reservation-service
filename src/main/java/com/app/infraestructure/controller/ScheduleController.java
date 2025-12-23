package com.app.infraestructure.controller;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.modelmapper.ModelMapper;

import com.app.application.port.in.ScheduleUseCase;
import com.app.domain.model.Schedule;
import com.app.infraestructure.controller.request.ScheduleRequest;
import com.app.infraestructure.util.Result;

import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Path("/schedules")
public class ScheduleController {

	private final ScheduleUseCase scheduleUseCase;
	private final ModelMapper modelMapper;
	
	@Operation(summary = "Registra los horarios de los profesionales asignados según su disponibilidad", description = "")
	@APIResponses({
			@APIResponse(responseCode = "200", description = "Creación exitosa", content = @Content(schema = @Schema(implementation = Uni.class))) })
	@POST
	public Uni<Response> createSchedule(@Valid ScheduleRequest scheduleRequest) {
		Schedule schedule = modelMapper.map(scheduleRequest, Schedule.class);
		return scheduleUseCase.createSchedule(schedule).map(msg -> Result.builder().data(msg).build())
				.map(result -> Response.ok(result).build());
	}
	
}
