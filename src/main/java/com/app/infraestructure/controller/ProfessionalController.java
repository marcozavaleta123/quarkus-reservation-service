package com.app.infraestructure.controller;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.modelmapper.ModelMapper;

import com.app.application.port.in.ProfessionalUseCase;
import com.app.domain.model.Professional;
import com.app.infraestructure.controller.request.ProfessionalRequest;
import com.app.infraestructure.controller.response.ProfessionalResponse;
import com.app.infraestructure.util.Result;

import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Path("/professionals")
public class ProfessionalController {

	private final ProfessionalUseCase professionalUseCase;
	private final ModelMapper modelMapper;
	
	@Operation(summary = "Registra los profesionales", description = "")
	@APIResponses({
			@APIResponse(responseCode = "200", description = "Creación exitosa", content = @Content(schema = @Schema(implementation = Uni.class))) })
	@POST
	public Uni<Response> createClient(@Valid ProfessionalRequest professionalRequest) {
		Professional professional = modelMapper.map(professionalRequest, Professional.class);
		return professionalUseCase.createProfessional(professional).map(
				createdProfessional -> Result.builder().data(modelMapper.map(createdProfessional, ProfessionalResponse.class)).build())
				.map(result -> Response.ok(result).build());
	}
	
	@PUT
	@Path("/{dni}/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<Response> updateClientStatus(@PathParam("dni") String dni, @QueryParam("status") boolean status) {
		return professionalUseCase.updateProfessionalStatus(dni, status)
				.map(msg -> Result.builder().data(msg).build())
				.map(result -> Response.ok(result).build());
	}
	 
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<Response> getClient() {
		return professionalUseCase.getProfessionals()
				.map(entities -> entities.stream().map(e -> modelMapper.map((Professional) e, ProfessionalResponse.class)).toList())
				.map(professionalResponseList -> Result.builder().data(professionalResponseList).build())
				.map(result -> Response.ok(result).build());
	}

	@GET
	@Path("/{dni}")
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<Response> getClientByDni(@PathParam("dni") String dni) {
		return professionalUseCase.getProfessionalByDni(dni)
				.map(professional -> Result.builder().data(modelMapper.map(professional, ProfessionalResponse.class)).build())
				.map(result -> Response.ok(result).build());
	}

}
