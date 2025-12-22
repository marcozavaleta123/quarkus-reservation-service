 package com.app.infraestructure.persistence;

import java.util.List;

import org.modelmapper.ModelMapper;

import com.app.application.port.out.ClientRepositoryOutPort;
import com.app.domain.model.Client;
import com.app.infraestructure.persistence.entity.ClientEntity;

import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class JpaClientRepositoryAdapter implements ClientRepositoryOutPort {
	
	private final ModelMapper modelMapper;
	
	@Override
    public Uni<Boolean> existsByEmail(String email) {
	    return ClientEntity.count("email", email)
	            .map(count -> count > 0);
	}

	@Override
    public Uni<Boolean> existsByDni(String dni) {
	    return ClientEntity.count("dni", dni)
	            .map(count -> count > 0);
	}

	@Override
    public Uni<Client> save(Client client) {
		ClientEntity entity = modelMapper.map(client, ClientEntity.class);
		entity.setStatusActive(true);
		return ClientEntity.persist(entity)
                .replaceWith(() -> modelMapper.map(entity, Client.class));
    }

	@Override
	public Uni<Client> findByDni(String dni) {
		return ClientEntity.find("dni = ?1", dni)
	            .firstResult()
	            .map(entity -> {
	            	if(entity != null) {
	            		ClientEntity clientEntity = (ClientEntity) entity;
		                return modelMapper.map(clientEntity, Client.class);
	                }
	            	return null;
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
	public Uni<Boolean> updateStatus(String dni, boolean status) {
		return ClientEntity.update("statusActive = :status where dni = :dni",
        	    Parameters.with("status", status).and("dni", dni))
				.map(rows -> rows > 0);
	}

}
