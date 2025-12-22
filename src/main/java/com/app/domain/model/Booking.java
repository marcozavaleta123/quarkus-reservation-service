package com.app.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;

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
public class Booking {
	
	private Long id;
	private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String professionalDni;
    private String clientDni;

}
