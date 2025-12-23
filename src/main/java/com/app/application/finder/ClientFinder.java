package com.app.application.finder;

import com.app.domain.model.Client;

import io.smallrye.mutiny.Uni;

public interface ClientFinder {
	
	Uni<Client> findByDni(String dni);

}
