package com.formacionbdi.springboot.app.oauth.servicesimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.formacionbdi.springboot.app.oauth.clients.IUsuarioFeignClient;
import com.formacionbdi.springboot.app.oauth.services.IUsuarioService;
import com.formacionbdi.springboot.app.usuarios.commons.models.entity.Usuario;

import brave.Tracer;
import feign.FeignException;

/* Implementa la interfaz propia de spring security que contiene un método para loguear al usuario desde el username.
	Nota: este método funciona independientemente si utilizamos JPA, JDCB o consumiendo microservicios utilizando una API REST.
*/
@Service
public class UsuarioService implements UserDetailsService, IUsuarioService{

	private static final Logger LOGGER = LoggerFactory.getLogger(UsuarioService.class);
	
	@Autowired
	private IUsuarioFeignClient clienteFeign;
	
	// Procedemos añadir trazas al span de zipkin -> brave.Tracer 
	@Autowired
	private Tracer tracer;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		try {
		
		// Obtenemos el cliente FEIGN
		Usuario usuario = clienteFeign.findByUsername(username);
				
		// Los roles son de el tipo genérico de la interfaz GrantedAuthority, tenemos que convertir los roles en GrantedAuthority utilizando la api de java 8 'stream()'
		List<GrantedAuthority> authorities = usuario.getRoles()
				.stream()
				.map(role -> new SimpleGrantedAuthority(role.getNombre()))
				// Por cada rol vamos a mostrar el nombre de el usuario, como ya lo hemos pasado a Authority(), podremos utilizarlo. 
				.peek(authority -> LOGGER.info("Rol identificado: {}", authority.getAuthority()))
				// Convertimos a un tipo list
				.collect(Collectors.toList());
		
		LOGGER.info("Usuario autenticado en la aplicación: {}", username);
		
		return new User(usuario.getUsername(), usuario.getPassword(), usuario.getEnabled(), true, true, true, authorities);
		
		}catch (FeignException e) {
			String loginError = "Error en el login, no existe el usario '"+username+"' en el sistema";
			LOGGER.error(loginError);
			
			// Traza span para zipkin
			tracer.currentSpan().tag("error.mensaje", loginError + ": "+ e.getMessage());
			
			throw new UsernameNotFoundException(loginError);
		}		
	}

	@Override
	public Usuario findByUsername(String username) {
		Usuario usuario = clienteFeign.findByUsername(username);
		return usuario;
	}

	@Override
	public Usuario update(Usuario usuario, Long id) {
		return clienteFeign.update(usuario, id);
	}

}
