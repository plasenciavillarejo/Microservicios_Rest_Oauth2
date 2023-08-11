package com.formacionbdi.springboot.app.shared.usuarios.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import com.formacionbdi.springboot.app.usuarios.commons.models.entity.Role;
import com.formacionbdi.springboot.app.usuarios.commons.models.entity.Usuario;

/*
 	1.- Para mostrar los id en las rutas de postman debemos de configurarlo o cualquier configuracion que contenga dentro de @RepositoryRestResource
 		1.1.- Sobreescribimos el siguiente método -> configureRepositoryRestConfiguration
 */

@Configuration
public class RepositoriyConfig implements RepositoryRestConfigurer{

	@Override
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
		// Con esto habilitamos que el campo "id" se muestre en el json, pero esto es opcional.
		config.exposeIdsFor(Usuario.class, Role.class);
	}
	
}
