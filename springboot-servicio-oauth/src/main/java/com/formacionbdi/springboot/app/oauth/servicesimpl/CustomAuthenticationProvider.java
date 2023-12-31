package com.formacionbdi.springboot.app.oauth.servicesimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.formacionbdi.springboot.app.oauth.clients.IUsuarioFeignClient;
import com.formacionbdi.springboot.app.usuarios.commons.models.entity.Usuario;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationProvider.class);
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private IUsuarioFeignClient clienteFeign;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		LOGGER.info("Entrando por nuestro CustumAuthenticationProvider.");
		
		String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        Usuario buscarNombrePersonaAuthenticad = usuarioService.findByUsername(name);
        
        List<GrantedAuthority> authorities = buscarNombrePersonaAuthenticad.getRoles()
				.stream()
				.map(role -> new SimpleGrantedAuthority(role.getNombre()))
				// Por cada rol vamos a mostrar el nombre de el usuario, como ya lo hemos pasado a Authority(), podremos utilizarlo. 
				.peek(authority -> LOGGER.info("Rol identificado: {}", authority.getAuthority()))
				// Convertimos a un tipo list
				.collect(Collectors.toList());
        
        return new UsernamePasswordAuthenticationToken(name, password, authorities);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
