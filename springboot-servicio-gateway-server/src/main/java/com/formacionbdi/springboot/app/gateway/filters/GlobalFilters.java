package com.formacionbdi.springboot.app.gateway.filters;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;


/* Este filtro captara cualqueir peticion a excepcion de 'servicio-productos' que se le ha indicado que para dicho endpoint
se ejecutará el filtro EjemploGatewayFilterFactory.java */

@Component
public class GlobalFilters implements GlobalFilter, Ordered {

	private final Logger LOGGER = LoggerFactory.getLogger(GlobalFilter.class);
	
	/*
		Este filtro trabaja por debajo de webflux, programacion reactiva (Observables, flujos reactivos 'Existen dos tipos, monos(un solo elemento en el flujo) y flux(trata de una lista en el flujo)')
		ServerWebExchange -> Se puede acceder al Request y al Response y poder modificarlos. Realizar validaciones, cabecera, parámetros del request para validar si se cumple unos determinados
			flujos poder permitir o rechar el acceso a nuestra API Rest. Por ejemplo implementando Spring Security
	*/
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		// Esto es el PRE filter
		
		LOGGER.info("Ejecutando el PRE filtro");
		// Generamos una cabecera con un token al request
		exchange.getRequest().mutate().headers(h -> h.add("token", "123456"));
		
		String tokenAuthorization = getTokenFromRequest(exchange);
		
		// Verificar si la ruta coincide con "/images/**"
	    /*
		if (exchange.getRequest().getURI().getPath().startsWith("/images/")) {
	        LOGGER.info("La ruta coincide con /images/**, permitiendo el flujo.");
	        return chain.filter(exchange);
	    }
		*/
		/* Esto es el POST filter 
		  Utilizamos programación reactiva con webflux invocamos el operador 'then', esto se ejecuta depues cuando se haya terminado toda la ejecución de todos los filtros y haya finalizado el
		proceso y obtenamos la respuesta.
		THEN -> Manejamos la respuesta (POST) que recibe una expresión lambda
			Mono.fromRunnable -> Nos permite crear un objeto reactivo, un mono boy, creamos una tarea  o una implementación.
		*/
		return chain.filter(exchange).then(Mono.fromRunnable(()->{
			LOGGER.info("Ejecutando el PRO filtro");
			
			LOGGER.info("Procedemos a obtener la cabecera enviada en el request utiliznado Optional de JAVA 8");
			// Validamos si está presente utilizando el Optional de java 8.
			// Of -> Convierte un valor en Optional
			// Nullable -> Si este valor no contiene nada va a guardar un optional vacío 'empty'
			Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("token")).ifPresent(valorToken ->{
				exchange.getResponse().getHeaders().add("token", valorToken);
				LOGGER.info("Pasamos el token de la cabecera del Request a la cabecera del Response {}", valorToken);
			});
			
			// Devolvemos un build ya que devuelve dicho objeto.
			exchange.getResponse().getCookies().add("color", ResponseCookie.from("color", "rojo").build());
			exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
		}));
	}

	
	/* Con este getOrder() le damos un orde de ejecución si tenemos muchos filotros, heredamos de Ordered. De está forma si el orden es valor negativo, se va a ejecutar la lógica de
	 	el PRE antes que los demás filtros.
	 	
	 	Nota: cuando se le da un orden 'negativo', la respuesta 'reponse' es solo de lectura y no de escritura por tanto nos daría error la línea
	 		'exchange.getResponse().getHeaders().add("token", valorToken);'
	 	Lo cambiamos a un valor positivo	
	 */
	@Override
	public int getOrder() {
		//return -1;
		return 10;
	}

	
	public String getTokenFromRequest(ServerWebExchange request) {
		// Obtenemos de la cabecera la authorization
        String token = request.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		
		// Si se cumple viene el token
		if(StringUtils.hasText(token) && token.startsWith("Bearer ")) {
			// Retornamos desde el caracter 7 hasta el final que contiene el token eliminadno el -> "Bearer "
			return token.substring(7);
		}
		return null;
	}
	
}
