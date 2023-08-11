package com.formacionbdi.springboot.app.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {

	@Autowired
	private ReactiveAuthenticationManager authenticationManager;
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		return Mono.justOrEmpty(exchange.getRequest()
				// Obtenemos la cabecera el token y lo almacenamos en un flujo reactivo (Mono<>)
				.getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
				// Filtramos para preguntar si la cabecera contiene la palabra Be
				.filter(authHeader -> authHeader.startsWith("Bearer "))
				// Si no viene el bearer no hacemos nada, continuamos con un token vacío y continue con los demás filtros. ( En resumen no hacemos nada)
				.switchIfEmpty(chain.filter(exchange).then(Mono.empty()))
				// Eliminamos el Bearer de la cadena de está forma solo tenemos el token en la cadena
				.map(token -> token.replace("Bearer ", ""))
				// Creamos el objeto authenticate(), primer argumento null el segundo el token
				// flatMap Es similar al map pero devuelve otro flujo, ya que estamos utilizando authenticationManager y devuelve otro flujo
				.flatMap(token -> authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(null, token)))
				// Devolvemos un authentication y registramos en el contexto de spring utilizando ReactiveSecurityContextHolder.withAuthentication(authentication) 
				.flatMap(authentication -> chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication)));
	}

}
