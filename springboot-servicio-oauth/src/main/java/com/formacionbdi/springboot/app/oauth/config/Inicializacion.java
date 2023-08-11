package com.formacionbdi.springboot.app.oauth.config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Inicializacion {

	Logger LOGGER = LoggerFactory.getLogger(Inicializacion.class);
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@PostConstruct
	public void inicializacion() {
		String password = "12345";
		
		for(int i=0; i<4; i++ ) {
			String passwordEncriptado = passwordEncoder.encode(password);
			LOGGER.info("Imprimiendo contraseñas: {}", passwordEncriptado);
		}
	}
	
}
