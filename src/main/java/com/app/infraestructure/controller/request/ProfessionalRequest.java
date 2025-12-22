package com.app.infraestructure.controller.request;

import jakarta.validation.constraints.NotBlank;

public record ProfessionalRequest(
		@NotBlank(message = "El campo firstName no puede ser blanco, vacio o nulo")
		String firstName,
		@NotBlank(message = "El campo lastName no puede ser blanco, vacio o nulo")
        String lastName,
        @NotBlank(message = "El campo dni no puede ser blanco, vacio o nulo")
        String dni,
        @NotBlank(message = "El campo especialidad no puede ser blanco, vacio o nulo")
        String speciality) {

}
