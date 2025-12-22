package com.app.infraestructure.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProfessionalResponse {
	
	private Long id;
	private String firstName;
    private String lastName;
    private String dni;
    private String speciality;
    private boolean statusActive;

}
