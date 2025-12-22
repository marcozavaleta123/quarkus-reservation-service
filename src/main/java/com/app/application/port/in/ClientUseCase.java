package com.app.application.port.in;

import java.util.List;

import com.app.domain.model.Client;

import io.smallrye.mutiny.Uni;

public interface ClientUseCase {
	
	Uni<Client> createClient(Client client);
	
	Uni<List<Client>> getClients();
	
	Uni<Client> getClientByDni(String dni);
	
	Uni<String> updateClientStatus(String dni, boolean status);

}
