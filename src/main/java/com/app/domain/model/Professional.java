package com.app.domain.model;

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
public class Professional {
	
	private Long id;
	private String firstName;
	private String lastName;
	private String dni;
	private String speciality;
	private boolean statusActive;

}
