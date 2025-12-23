package com.app.application.finder;

import com.app.domain.model.Professional;

import io.smallrye.mutiny.Uni;

public interface ProfessionalFinder {
	
	Uni<Professional> findByDni(String dni);
	
}
