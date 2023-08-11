package com.formacionbdi.springboot.app.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

// Habilitamos la seguridad WebFlux.
@EnableWebFluxSecurity
public class SpringSecurityConfig {
	
	@Autowired
	private JwtAuthenticationFilter authenticationFiler;
	
	@Bean
    public RequestMatcher customRequestMatcher() {
        return new AntPathRequestMatcher("/api/security/oauth/token");
    }
	
	@Bean
	public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
		http.authorizeExchange()
			.pathMatchers("/api/security/oauth/**").permitAll()
			.pathMatchers(HttpMethod.GET, 
					"/api/productos/listar", 
					"/api/items/listar",
					"/api/usuarios/usuarios",
					"/api/items/ver/{id}/cantidad/{cantidad}",
					"/api/productos/ver/{id}").permitAll()								
			.pathMatchers(HttpMethod.GET, "/api/usuarios/usuarios/{id}").hasAnyRole("ROLE_ADMIN","ROLE_USER")
			.pathMatchers("/api/productos/**", "/api/items/**", "/api/usuarios/usuarios/**").hasRole("ADMIN")
			.anyExchange().authenticated()
			.and()
			// Registramos nuestro Filtro
			.addFilterBefore(authenticationFiler, SecurityWebFiltersOrder.AUTHENTICATION)
			.csrf().disable();		
		
		return http.build();
	}
	
}
