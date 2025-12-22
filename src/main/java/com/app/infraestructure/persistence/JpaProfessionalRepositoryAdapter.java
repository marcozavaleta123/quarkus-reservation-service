package com.app.infraestructure.persistence;

import java.util.List;

import org.modelmapper.ModelMapper;

import com.app.application.port.out.ProfessionalRepositoryOutPort;
import com.app.domain.model.Professional;
import com.app.infraestructure.persistence.entity.ProfessionalEntity;

import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class JpaProfessionalRepositoryAdapter implements ProfessionalRepositoryOutPort {
	
	private final ModelMapper modelMapper;
	
	@Override
	public Uni<Professional> save(Professional professional) {
		ProfessionalEntity entity = modelMapper.map(professional, ProfessionalEntity.class);
		entity.setStatusActive(true);
		return ProfessionalEntity.persist(entity)
                .replaceWith(() -> modelMapper.map(entity, Professional.class));
	}

	@Override
	public Uni<List<Professional>> findAll() {
		return ProfessionalEntity.listAll()
	            .map(entities ->
	                    entities.stream()
	                            .map(entity -> modelMapper.map((ProfessionalEntity) entity, Professional.class))
	                            .toList()
	            );
	}

	@Override
	public Uni<Professional> findByDni(String dni) {
		return ProfessionalEntity.find("dni = ?1", dni)
	            .firstResult()
	            .map(entity -> entity != null ? modelMapper.map((ProfessionalEntity) entity, Professional.class) : null);
	}

	@Override
	public Uni<Boolean> updateStatus(String dni, boolean status) {
		return ProfessionalEntity.update("statusActive = :status where dni = :dni",
        	    Parameters.with("status", status).and("dni", dni))
        		 .map(rows -> rows > 0);
	}

}
