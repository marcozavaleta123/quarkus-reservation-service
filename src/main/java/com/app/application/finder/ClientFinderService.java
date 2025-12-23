package com.app.application.finder;

import com.app.application.port.out.ClientRepositoryOutPort;
import com.app.domain.model.Client;
import com.app.infraestructure.exception.BusinessErrorType;
import com.app.infraestructure.exception.BusinessException;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class ClientFinderService implements ClientFinder {
	
	private final ClientRepositoryOutPort clientRepositoryOutPort;
	
	@Override
	public Uni<Client> findByDni(String dni) {
		return clientRepositoryOutPort.findByDni(dni)
				.onItem().ifNull().failWith(
		                new BusinessException(
		                    BusinessErrorType.VALIDATION_ERROR,
		                    "El DNI del cliente ingresado no se encuentra registrado"
		                )
		            );
	}

}
