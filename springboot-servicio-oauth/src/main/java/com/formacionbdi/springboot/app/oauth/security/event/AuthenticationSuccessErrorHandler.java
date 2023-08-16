package com.formacionbdi.springboot.app.oauth.security.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import com.formacionbdi.springboot.app.oauth.services.IUsuarioService;
import com.formacionbdi.springboot.app.usuarios.commons.models.entity.Usuario;

import brave.Tracer;
import feign.FeignException;

@Component
public class AuthenticationSuccessErrorHandler implements AuthenticationEventPublisher {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationSuccessErrorHandler.class);
		
	@Autowired
	private IUsuarioService usuarioService;
	
	// Procedemos añadir trazas al span de zipkin -> brave.Tracer 
	@Autowired
	private Tracer tracer;
	
	@Override
	public void publishAuthenticationSuccess(Authentication authentication) {
		
		// Para evitar que entre dos veces por el cliente de el front y de el be, (Solo queremos validar el BE) vamos a implementar la siguiente lógica
		if(authentication.getDetails() instanceof WebAuthenticationDetails) {
			LOGGER.info("Es el usuario de el Front: {}, no se valida dicho usuario ya que nos interesa solo el usuario registrado en el BE",authentication.getName());
			return;
		}
		
		Usuario usuario = usuarioService.findByUsername(authentication.getName());
		
		if(usuario.getIntentos() != null && usuario.getIntentos() > 0) {
			usuario.setIntentos(0);
			usuarioService.update(usuario, usuario.getId());
		}
		
		// 1.- Si implementamos el UserDetailService tendremos que devolver el siguiente objeto:
		UserDetails user = (UserDetails) authentication.getPrincipal();
		
		// Si implemento el AuthenticationProvider() tengo que cambiar la forma en la que devuelvo el objeto
		//UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials());
		LOGGER.info("Success Login: {}", user.getUsername());
	}

	@Override
	public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
		String mensajeError = "Error Login: " + exception.getMessage();
		LOGGER.error(mensajeError);
		
		try {
			StringBuilder errors = new StringBuilder();
			errors.append(mensajeError);
			
			Usuario usuario = usuarioService.findByUsername(authentication.getName());
			if(usuario.getIntentos() == null) {
				usuario.setIntentos(0);
			}
			LOGGER.info("Intento actual es de: {}", usuario.getIntentos());
			usuario.setIntentos(usuario.getIntentos()+1);
			LOGGER.info("Intento después es de: {}", usuario.getIntentos());
			
			errors.append(" - Intento después de login: " + usuario.getIntentos());
			
			if(usuario.getIntentos() >= 3) {
				String errorMaxIntentos = String.format("El usuario %s deshabilitado por máximo intentos", usuario.getUsername());
				LOGGER.error(errorMaxIntentos);
				errors.append(" - " + errorMaxIntentos);
				usuario.setEnabled(false);
			}
			usuarioService.update(usuario, usuario.getId());
			
			// Traza span para zipkin
			tracer.currentSpan().tag("error.mensaje", errors.toString());
			
		}catch (FeignException e) {
			LOGGER.error(String.format("El usuario %s no existe en el sistema", authentication.getName()));
		}
	}

}
