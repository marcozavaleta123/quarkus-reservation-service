package com.app.application.port.in;

import java.util.List;

import com.app.domain.model.Professional;

import io.smallrye.mutiny.Uni;

public interface ProfessionalUseCase {
	
	Uni<Professional> createProfessional(Professional client);
	
    Uni<List<Professional>> getProfessionals();
	
	Uni<Professional> getProfessionalByDni(String dni);
	
	Uni<String> updateProfessionalStatus(String dni, boolean status);

}
