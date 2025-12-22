package com.app.infraestructure.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.ws.rs.core.Response;
import lombok.*;
import java.util.List;
import java.util.function.Function;

import static java.util.Objects.isNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Result<T> {
    private T data;
    private String message;
    private List<String> errors;
    @Builder.Default
    private Response.Status httpStatus = Response.Status.OK;

    public <R> Result<R> map(Function<T, R> mapper) {
        R newData = null;
        if(!isNull(data)) {
            newData = mapper.apply(data);
        }
        return new Result<>(newData, message, errors, httpStatus);
    }
}
