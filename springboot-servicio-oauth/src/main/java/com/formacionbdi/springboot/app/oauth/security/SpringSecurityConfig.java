package com.formacionbdi.springboot.app.oauth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.formacionbdi.springboot.app.oauth.servicesimpl.CustomAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {
	
	/* Le indico que antes de que ejecute el FilterChain de Spring security me ejecute mi propio filtro personalizado con el @Order(1), en este caso no funciona
	@Bean
	@Order(1)
	public CustomFilter filtroPersonalizado() {
		return new CustomFilter();
	}
	*/
	
	// Inyectamos nuestra clase creada para el AuthenticationSuccesErrorHandler.java pero indicamos la interfaz. -> AuthenticationEventPublisher() 
	@Autowired
	private AuthenticationEventPublisher eventPublisher;
	
	/* Inyectamos la Intefaz genérica que nos proporciona spring security 'UserDetailsService' ya que nuestra clase UsuarioService.java implementa dicha interfaz entonces spring va a buscar
	un componente anotado con @Service e inyectará nuestra clase
	*/
	@Autowired
	private UserDetailsService usuarioService;

	@Autowired
	private CustomAuthenticationProvider customAuthenticatinProvider;
	
	@Bean
	static BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	// Registramos el usuario en el authentication Manager y encriptamos la contraseña cuando el usuario inice sesión de forma automática va a encriptar ese password y lo comparará con el que hay en BBDD.
	// Cuando entramos al login -> http://localhost:8090/api/security/oauth/token -> El enviar automáticamente llega a la clase UsuarioService que implementa UserDetailService y realiza la autenticación
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// Implemento mi propia validacíon - Funciona correctamente
		//auth.authenticationProvider(customAuthenticatinProvider)
		// Validación propia de spring security
		auth.userDetailsService(this.usuarioService).passwordEncoder(passwordEncoder())
		.and()
		// Registramos nuestro evento para AuthenticationSuccesErrorHandler.java
		.authenticationEventPublisher(eventPublisher);
	}
	
	// Implementamos el AuthenticationManager para registrarlo como componente de spring para poder inyectarlo en el servidor de autorizacion de oauth2
	// Nota: Si la contraseña no se almacena en Base de Datos se debe implementar una implementación 
	/*@Bean
	public AuthenticationProvider authenticationProvider() {
	    return new CustomAuthenticationProvider(usuarioService);
	}
	*/
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); 
    }

}
