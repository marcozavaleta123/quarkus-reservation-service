package com.app.application.service;

import java.util.List;

import com.app.application.port.in.ProfessionalUseCase;
import com.app.application.port.out.ProfessionalRepositoryOutPort;
import com.app.domain.model.Professional;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class ProfessionalService implements ProfessionalUseCase {
	
	private final ProfessionalRepositoryOutPort professionalRepositoryOutPort;

	@Override
	public Uni<Professional> createProfessional(Professional professional) {
		return professionalRepositoryOutPort.save(professional);
	}

	@Override
	public Uni<List<Professional>> getProfessionals() {
		return professionalRepositoryOutPort.findAll();
	}

	@Override
	public Uni<Professional> getProfessionalByDni(String dni) {
		return professionalRepositoryOutPort.findByDni(dni);
	}

	@Override
	public Uni<String> updateProfessionalStatus(String dni, boolean status) {
		return professionalRepositoryOutPort.updateStatus(dni, status);
	}

}
