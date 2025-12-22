package com.app.infraestructure.exception;

import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum BusinessErrorType {
    VALIDATION_ERROR("V-01", "Error en la validación de datos: %s", Response.Status.BAD_REQUEST),
    GENERIC_ERROR("G-02", "Error interno del sistema. Reintentar más tarde: %s", Response.Status.INTERNAL_SERVER_ERROR),
    PERSISTENCE_ERROR("DB-03", "Error de persistencia en db: %s", Response.Status.CONFLICT);

    private final String code;
    private final String description;
    private final Response.Status status;
}
