package com.app.infraestructure.persistence;

import static java.util.Objects.nonNull;

import java.util.List;

import org.modelmapper.ModelMapper;

import com.app.application.port.out.ProfessionalRepositoryOutPort;
import com.app.domain.model.Professional;
import com.app.infraestructure.exception.BusinessErrorType;
import com.app.infraestructure.exception.BusinessException;
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
		return ProfessionalEntity.find("dni = ?1", professional.getDni()).firstResult()
                .flatMap(existingProfessional-> {
                    if (nonNull(existingProfessional)) {
                        return Uni.createFrom().failure(
                                new BusinessException(BusinessErrorType.VALIDATION_ERROR, "el DNI del profesional ingresado ya se encuentra registrado")
                        );
                    }
                    ProfessionalEntity entity = modelMapper.map(professional, ProfessionalEntity.class);
            		entity.setStatusActive(true);
            		return ProfessionalEntity.persist(entity)
                            .replaceWith(() -> modelMapper.map(entity, Professional.class));
                });
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
	            .onItem().ifNull().failWith(
	                new BusinessException(BusinessErrorType.VALIDATION_ERROR, "El DNI no se encuentra registrado")
	            )
	            .map(entity ->
	                modelMapper.map((ProfessionalEntity) entity, Professional.class)
	            );
	}

	@Override
	public Uni<String> updateStatus(String dni, boolean status) {
		return ProfessionalEntity.update("statusActive = :status where dni = :dni",
        	    Parameters.with("status", status).and("dni", dni))
        		 .flatMap(rows -> {
        		        if (rows > 0) {
        		        	if(status)
        		        	  return Uni.createFrom().item("El estado del profesional se activó correctamente");
        		        	else
        		        	  return Uni.createFrom().item("El estado del profesional se desactivó correctamente");
        		        }
        		        return Uni.createFrom().item("El DNI ingresado no se encuentra registrado");
        		    });
	}

}
