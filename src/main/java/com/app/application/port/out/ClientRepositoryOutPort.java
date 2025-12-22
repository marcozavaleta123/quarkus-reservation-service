package com.app.application.port.out;

import java.util.List;

import com.app.domain.model.Client;

import io.smallrye.mutiny.Uni;

public interface ClientRepositoryOutPort {
	
	Uni<Client> save(Client client);
	
	Uni<List<Client>> findAll();
	
	Uni<Client> findByDni(String dni);
	
	Uni<String> updateStatus(String dni, boolean status);

}
