package com.app.infraestructure.enums;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum StatusBooking {
	
	CREADA("creada"),
    CANCELADA("cancelada"),
    COMPLETADA("completada");
	
	private final String value;
	
    public String getValue() {
		return value;
	}

	

}
