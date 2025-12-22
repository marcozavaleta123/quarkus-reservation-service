package com.app.application.port.out;

import java.util.List;

import com.app.domain.model.Professional;

import io.smallrye.mutiny.Uni;

public interface ProfessionalRepositoryOutPort {
	
    Uni<Professional> save(Professional client);
	
	Uni<List<Professional>> findAll();
	
	Uni<Professional> findByDni(String dni);
	
	Uni<String> updateStatus(String dni, boolean status);

}
