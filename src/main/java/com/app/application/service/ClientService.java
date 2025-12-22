package com.app.application.service;

import java.util.List;

import com.app.application.port.in.ClientUseCase;
import com.app.application.port.out.ClientRepositoryOutPort;
import com.app.domain.model.Client;
import com.app.infraestructure.exception.BusinessErrorType;
import com.app.infraestructure.exception.BusinessException;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class ClientService implements ClientUseCase {
	
	private final ClientRepositoryOutPort clientRepositoryOutPort;
	
	@Override
	public Uni<Client> createClient(Client client) {
		Uni<Boolean> emailExists = clientRepositoryOutPort.existsByEmail(client.getEmail());
	    Uni<Boolean> dniExists   = clientRepositoryOutPort.existsByDni(client.getDni());
	    
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

            		return clientRepositoryOutPort.save(client);
	            });
	}

	@Override
	public Uni<Client> getClientByDni(String dni) {
		return clientRepositoryOutPort.findByDni(dni)
				.onItem().ifNull().failWith(
		                new BusinessException(
		                    BusinessErrorType.VALIDATION_ERROR,
		                    "El DNI no se encuentra registrado"
		                )
		            );
	}

	@Override
	public Uni<List<Client>> getClients() {
		return clientRepositoryOutPort.findAll();
	}

	@Override
	public Uni<String> updateClientStatus(String dni, boolean status) {
		return clientRepositoryOutPort.updateStatus(dni, status)
				.flatMap(b -> {
    		        if (b) {
    		        	if(status)
    		        	  return Uni.createFrom().item("El estado del cliente se activó correctamente");
    		        	else
    		        	  return Uni.createFrom().item("El estado del cliente se desactivó correctamente");
    		        }
    		        return Uni.createFrom().item("El DNI ingresado no se encuentra registrado");
    		    });
	}

}
