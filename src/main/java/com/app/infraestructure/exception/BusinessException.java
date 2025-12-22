package com.app.infraestructure.exception;

import jakarta.persistence.PersistenceException;
import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

@Getter
public class BusinessException extends RuntimeException {
    private final UUID id;
    private final BusinessErrorType type;
    private final String description;

    @Builder
    public BusinessException(
            BusinessErrorType businessErrorType,
            String description
    ) {
        super(description);
        this.id = UUID.randomUUID();
        this.description = businessErrorType.getDescription().formatted(description);
        this.type = businessErrorType;
    }

    public static BusinessException createFromPersistenceException(PersistenceException persistenceException) {
        return BusinessException.builder()
                .businessErrorType(BusinessErrorType.PERSISTENCE_ERROR)
                .description(BusinessErrorType.PERSISTENCE_ERROR.getDescription()
                        .formatted(persistenceException.getMessage())
                )
                .build();
    }
}
