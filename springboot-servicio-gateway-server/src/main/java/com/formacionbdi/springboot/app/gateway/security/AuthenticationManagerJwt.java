package com.formacionbdi.springboot.app.gateway.security;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationManagerJwt implements ReactiveAuthenticationManager {

	@Value("${config.security.ouath.jwt.key}")
	private String llaveJwt;
	
	// Este Authentication va a contener el Token que se pasa por el filtro
	@SuppressWarnings("unchecked")
	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		// Just -> Convierte un objeto comun en un objeto reactivo. Combierte el token en un flujo.
		// El getCredentials() es el token.
		return Mono.just(authentication.getCredentials().toString())
				// Procesamos el token para convertirlo y devolver los claims (Contiene la información de el user, roles,etc..) de el token.
				.map(token ->{
					// Lo convertimos en Base64 para que sea mucho más robusto
					SecretKey llaveSecreta = Keys.hmacShaKeyFor(Base64.getEncoder().encode(llaveJwt.getBytes(Charset.forName("UTF-8"))));
					
					// Se valida el token y devolver al flujo reactivo los claims
					// Con getBody() obtenemos las credenciales
					return Jwts.parserBuilder().setSigningKey(llaveSecreta).build().parseClaimsJws(token).getBody();
				})
				// Ahora tenemos que convertir el Mono<Object> a Mono<Authentication> con un map
				.map(claims -> {
					// Obtenemos el username que se obtiene dentro de el atributo que contiene el token -> user_name y el tipo de datos String.class
					String username = claims.get("user_name", String.class);
					// Los roles se captura desde authorities
					List<String> roles = claims.get("authorities", List.class);
					/* Otra forma de hacer el rol -> roles.stream().map(SimpleGrantedAuthority::new) , utilizando referencias de métodos
					 	Forma básica: roles.stream().map(role -> new SimpleGrantedAuthority(role))
					 */
					Collection<GrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
					return new UsernamePasswordAuthenticationToken(username, null,authorities);
				});
	}

}
