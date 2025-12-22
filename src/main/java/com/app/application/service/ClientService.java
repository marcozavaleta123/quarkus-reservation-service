package com.app.application.service;

import java.util.List;

import com.app.application.port.in.ClientUseCase;
import com.app.application.port.out.ClientRepositoryOutPort;
import com.app.domain.model.Client;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class ClientService implements ClientUseCase {
	
	private final ClientRepositoryOutPort clientRepositoryOutPort;

	@Override
	public Uni<Client> createClient(Client client) {
		return clientRepositoryOutPort.save(client);
	}

	@Override
	public Uni<Client> getClientByDni(String dni) {
		return clientRepositoryOutPort.findByDni(dni);
	}

	@Override
	public Uni<List<Client>> getClients() {
		return clientRepositoryOutPort.findAll();
	}

	@Override
	public Uni<String> updateClientStatus(String dni, boolean status) {
		return clientRepositoryOutPort.updateStatus(dni, status);
	}

}
