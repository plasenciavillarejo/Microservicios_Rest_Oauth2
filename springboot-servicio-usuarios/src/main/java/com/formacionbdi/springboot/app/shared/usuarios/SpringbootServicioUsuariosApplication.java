package com.formacionbdi.springboot.app.shared.usuarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

// Este Proyecto no se tiene que levantar ya que solo nos sirve para acceder a el y obtener la Entidades desde cualquier proyecto

@EntityScan({"com.formacionbdi.springboot.app.usuarios.commons.models.entity"})
@SpringBootApplication
public class SpringbootServicioUsuariosApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootServicioUsuariosApplication.class, args);
	}

}
