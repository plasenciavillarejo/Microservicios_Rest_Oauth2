package com.formacionbdi.springboot.app.gateway.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;


// Habilitamos la seguridad WebFlux.
@EnableWebFluxSecurity
public class SpringSecurityConfig {
	
	@Autowired
	private JwtAuthenticationFilter authenticationFiler;
	
	/*
	@Bean
    public RequestMatcher customRequestMatcher() {
        return new AntPathRequestMatcher("/api/security/oauth/token");
    }
	*/
	
	// ##########################
	// INI: Configuración ANGULAR
	// ##########################
	// Método encargado de dar acceso al front a nuestro back-end
	 @Bean
	  CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration corsConfig = new CorsConfiguration();
	    corsConfig.setAllowedOriginPatterns(Arrays.asList("*"));
	    corsConfig.setAllowedMethods(Arrays.asList("POST","GET","PUT","DELETE","OPTIONS"));
	    corsConfig.setAllowCredentials(true);
	    corsConfig.setAllowedHeaders(Arrays.asList("Authorization","Content-Type"));
	    
	    // Pasamos el corsConfig a nuestras rutas urls    
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    // se aplique a todas nuestras rutas
	    source.registerCorsConfiguration("/**", corsConfig);
	    
	    return source;
	  }
	
	// ##########################
	// FIN: Configuración ANGULAR
	// ##########################
	
	@Bean
	WebSecurityCustomizer webSecurityCustomizar() {
		return (web) -> web.ignoring().antMatchers("/images/**");
	}
		
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
    }
	
	@Bean
	SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        http.authorizeExchange(path ->
                path.pathMatchers("/api/security/oauth/**").permitAll()
                .pathMatchers(HttpMethod.GET,
                		"/api/productos/listar",
                        "/api/listar/page/",
                        "/api/items/listar",
                        "/api/usuarios/usuarios",
                        "/api/items/ver/{id}/cantidad/{cantidad}",
                        "/api/productos/ver/{id}",
                        "/api/productos/listar/page/{pagina}",
                        "/api/productos/verImagen/{nombreFoto:.+}",
                        "/api/productos/images/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/usuarios/usuarios/{id}").hasAnyRole("ADMIN", "USER")
                .pathMatchers("/api/productos/**", "/api/items/**", "/api/usuarios/usuarios/**").hasRole("ADMIN")
                .anyExchange().authenticated())
        // Registramos nuestro Filtro
        .addFilterBefore(authenticationFiler, SecurityWebFiltersOrder.AUTHENTICATION)
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource()));
        
		return http.build();
	}
			
}
