package com.app.infraestructure.controller;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.modelmapper.ModelMapper;

import com.app.application.port.in.ClientUseCase;
import com.app.domain.model.Client;
import com.app.infraestructure.controller.request.ClientRequest;
import com.app.infraestructure.controller.response.ClientResponse;
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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Path("/clients")
public class ClientController {
	
	private final ClientUseCase clientUseCase;
	private final ModelMapper modelMapper;
	
	@Operation(summary = "Registra los clientes", description = "")
	@APIResponses({
			@APIResponse(responseCode = "200", description = "Creación exitosa", content = @Content(schema = @Schema(implementation = Uni.class))) })
	@POST
	public Uni<Response> createClient(@Valid ClientRequest clientRequest) {
		Client client = modelMapper.map(clientRequest, Client.class);
		return clientUseCase.createClient(client).map(
				createdClient -> Result.builder().data(modelMapper.map(createdClient, ClientResponse.class)).build())
				.map(result -> Response.ok(result).build());
	}
	
	@PUT
	@Path("/{dni}/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<Response> updateClientStatus(@PathParam("dni") String dni, @QueryParam("status") boolean status) {
		return clientUseCase.updateClientStatus(dni, status)
				.map(msg -> Result.builder().data(msg).build())
				.map(result -> Response.ok(result).build());
	}
	 
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<Response> getClient() {
		return clientUseCase.getClients()
				.map(entities -> entities.stream().map(e -> modelMapper.map((Client) e, ClientResponse.class)).toList())
				.map(clientResponseList -> Result.builder().data(clientResponseList).build())
				.map(result -> Response.ok(result).build());
	}

	@GET
	@Path("/{dni}")
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<Response> getClientByDni(@PathParam("dni") String dni) {
		return clientUseCase.getClientByDni(dni)
				.map(client -> Result.builder().data(modelMapper.map(client, ClientResponse.class)).build())
				.map(result -> Response.ok(result).build());
	}

}
