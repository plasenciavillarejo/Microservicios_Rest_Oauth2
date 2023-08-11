package com.formacionbdi.springboot.app.oauth.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.formacionbdi.springboot.app.usuarios.commons.models.entity.Usuario;

// Anotamos la interfaz con @FeignClient y anotamos el microservicio con el queremos comunicarnos. En nuestro caso con el proyecto de servicio-usuarios
@FeignClient(name="servicio-usuarios")
public interface IUsuarioFeignClient {

	/*
	Como estamos utilizando por defecto en servicio-usuarios el @RepositoryRestResource, si queremos buscar un usuario debemos indicarlos de la siguiente forma.
		/usuarios/search/buscar-username -> /search por defecto nos indica la anotación @RepositoryRestResource que se debe buscar incluyendo ese sub-dominio. 
	*/
	@GetMapping(value = "/usuarios/search/buscar-username")
	public Usuario findByUsername(@RequestParam("username") String username);
	
	// Metodo que cuenta los fallos de acceso
	@PutMapping("/usuarios/{id}")
	public Usuario update(@RequestBody Usuario usuario, @PathVariable(value = "id") Long id);
}
