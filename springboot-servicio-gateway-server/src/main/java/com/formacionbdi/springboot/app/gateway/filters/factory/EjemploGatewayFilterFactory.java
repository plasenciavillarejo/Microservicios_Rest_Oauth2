package com.formacionbdi.springboot.app.gateway.filters.factory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

/* Algo muy importante es que la clase debe llevar de sufijo '(NombreClase)GatewayFilterFactory' para que detecte el nombre de forma automática

	Al extender de 'AbstractGatewayFilterFactory' debemos crear una clase, pero le indicaremos que la queremos dentro de está clase utiliznado 'EjemploGatewayFilterFactory.Configuracion'

*/
@Component
public class EjemploGatewayFilterFactory extends AbstractGatewayFilterFactory<EjemploGatewayFilterFactory.Configuracion> {

	private final Logger LOGGER = LoggerFactory.getLogger(EjemploGatewayFilterFactory.class);
	
	public EjemploGatewayFilterFactory() {
		super(Configuracion.class);
	}

	/*
		GatewayFilter solo contiene un método (Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain)), por tanto lo implementamos al vuelvo con una expresión lambda.
	*/
	
	@Override
	public GatewayFilter apply(Configuracion config) {
		/* 
		 1.- Retorna los métodos que contiene dentro el filter (Si hacemos click en GatewayFilter veremos el método) -> ServerWebExchange exchange, GatewayFilterChain chain 
		 2.-Para dar un orden lo indicamos con un new OrderedGatewayFiltered((expr,2));
		 	- 1º Expresión -> la función lambda
		 	- 2º Expresión -> el orden, en este cado el 2.
		*/
		return new OrderedGatewayFilter((exchange,chain) ->{
			// Todo lo que hay aquí es el PRE
			LOGGER.info("Ejecutando el PRE Gateway Filter Factory: {} ", config.mensaje);
			LOGGER.info("Ejecutando el PRE Gateway Filter Factory: {} ", config.cookieNombre);
			LOGGER.info("Ejecutando el PRE Gateway Filter Factory: {} ", config.cookieValor);

			return chain.filter(exchange).then(Mono.fromRunnable(()->{
				// Todo lo que viene depues de el return es el POST
				LOGGER.info("Ejecutando el PRO Gateway Filter Factory");
				
				Optional.ofNullable(config.cookieValor).ifPresent(cookie -> {
					exchange.getResponse().addCookie(ResponseCookie.from(config.cookieNombre, cookie).build());
				});
			}));
		},2);
	}

	// Para dar un orden a lo que se configura en el Applicatin.properties debemos implementar el siguiente método de la interfaz ShortcutConfigurable
	
	@Override
	public List<String> shortcutFieldOrder() {
		// Se le indica el orden que se recibe los parámetros
		return Arrays.asList("mensaje","cookieNombre","cookieValor");
	}

	// Si queremos cambiar el nombre de el Gateway debemos implementar de la interfaz GatewayFilterFactory el objeto 'name()'
	
	@Override
	public String name() {
		return "EjemploNombreCambiado";
	}

	public static class Configuracion {
		private String mensaje;
		private String cookieValor;
		private String cookieNombre;

		public String getMensaje() {
			return mensaje;
		}

		public void setMensaje(String mensaje) {
			this.mensaje = mensaje;
		}

		public String getCookieValor() {
			return cookieValor;
		}

		public void setCookieValor(String cookieValor) {
			this.cookieValor = cookieValor;
		}

		public String getCookieNombre() {
			return cookieNombre;
		}

		public void setCookieNombre(String cookieNombre) {
			this.cookieNombre = cookieNombre;
		}
	}
	


	
}
