package com.app.infraestructure.exception;

import com.app.infraestructure.util.Result;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Provider
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {
    @Override
    public Response toResponse(BusinessException exception) {
        log.error("Error de negocio : " + exception.getMessage());
        var result = Result.builder()
                .data(exception.getDescription())
                .httpStatus(exception.getType().getStatus())
                .build();
        return Response
                .status(exception.getType().getStatus())
                .entity(result)
                .build();
    }
}
