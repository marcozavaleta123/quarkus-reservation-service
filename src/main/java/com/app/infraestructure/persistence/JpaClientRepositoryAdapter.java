 package com.app.infraestructure.persistence;

import java.util.List;

import org.modelmapper.ModelMapper;

import com.app.application.port.out.ClientRepositoryOutPort;
import com.app.domain.model.Client;
import com.app.infraestructure.exception.BusinessErrorType;
import com.app.infraestructure.exception.BusinessException;
import com.app.infraestructure.persistence.entity.ClientEntity;

import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class JpaClientRepositoryAdapter implements ClientRepositoryOutPort {
	
	private final ModelMapper modelMapper;
	
	private Uni<Boolean> existsByEmail(String email) {
	    return ClientEntity.count("email", email)
	            .map(count -> count > 0);
	}

	private Uni<Boolean> existsByDni(String dni) {
	    return ClientEntity.count("dni", dni)
	            .map(count -> count > 0);
	}

	@Override
    public Uni<Client> save(Client client) {
		
		Uni<Boolean> emailExists = existsByEmail(client.getEmail());
	    Uni<Boolean> dniExists   = existsByDni(client.getDni());
	    
	    return Uni.combine().all().unis(emailExists, dniExists).asTuple()
	            .flatMap(result -> {
	                boolean emailExist = result.getItem1();
	                boolean dniExist   = result.getItem2();

	                if (emailExist) {
	                    return Uni.createFrom().failure(
	                        new BusinessException(
	                            BusinessErrorType.VALIDATION_ERROR,
	                            "El email ya está registrado"
	                        )
	                    );
	                }

	                if (dniExist) {
	                    return Uni.createFrom().failure(
	                        new BusinessException(
	                            BusinessErrorType.VALIDATION_ERROR,
	                            "El DNI ya está registrado"
	                        )
	                    );
	                }

	                ClientEntity entity = modelMapper.map(client, ClientEntity.class);
            		entity.setStatusActive(true);
            		return ClientEntity.persist(entity)
                            .replaceWith(() -> modelMapper.map(entity, Client.class));
	            });
    }

	@Override
	public Uni<Client> findByDni(String dni) {
		return ClientEntity.find("dni = ?1", dni)
	            .firstResult()
	            .onItem().ifNull().failWith(
		                new BusinessException(BusinessErrorType.VALIDATION_ERROR, "El DNI no se encuentra registrado")
		            )
	            .map(entity -> {
	                ClientEntity clientEntity = (ClientEntity) entity;
	                return modelMapper.map(clientEntity, Client.class);
	            });
	}

	@Override
	public Uni<List<Client>> findAll() {
	    return ClientEntity.listAll()
	            .map(entities ->
	                    entities.stream()
	                            .map(entity -> modelMapper.map((ClientEntity) entity, Client.class))
	                            .toList()
	            );
	}
	
	@Override
	public Uni<String> updateStatus(String dni, boolean status) {
		return existsByDni(dni)
	            .flatMap(exists -> {
	                return ClientEntity.update("statusActive = :status where dni = :dni",
	                	    Parameters.with("status", status).and("dni", dni))
	                		 .flatMap(rows -> {
	                		        if (rows > 0) {
	                		        	if(status)
	                		        	  return Uni.createFrom().item("El estado del cliente se activó correctamente");
	                		        	else
	                		        	  return Uni.createFrom().item("El estado del cliente se desactivó correctamente");
	                		        }
	                		        return Uni.createFrom().item("El DNI ingresado no se encuentra registrado");
	                		    });
	            });
	}

}
