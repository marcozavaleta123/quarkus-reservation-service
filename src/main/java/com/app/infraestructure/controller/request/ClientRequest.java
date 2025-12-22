package com.app.infraestructure.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClientRequest(
		@NotBlank(message = "El campo firstName no puede ser blanco, vacio o nulo")
		String firstName,
		@NotBlank(message = "El campo lastName no puede ser blanco, vacio o nulo")
        String lastName,
        @NotBlank(message = "El campo dni no puede ser blanco, vacio o nulo")
        String dni,
        @NotBlank(message = "El campo email no puede ser blanco, vacio o nulo")
		@Email(message = "El campo email no tiene un formato válido")
        String email,
        @NotBlank(message = "El campo phone no puede ser blanco, vacio o nulo")
        String phone) {

}
