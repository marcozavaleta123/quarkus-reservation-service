package com.app.infraestructure.exception;

import java.util.List;

import com.app.infraestructure.util.Result;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Provider
public class ConstraintViolationMapper implements ExceptionMapper<ConstraintViolationException> {
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        log.info("A constraint validation exception was thrown");
        List<String> errors = exception.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        Result<?> result = Result.builder()
                .httpStatus(Response.Status.BAD_REQUEST)
                .errors(errors)
                .build();
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(result)
                .build();
    }
}
