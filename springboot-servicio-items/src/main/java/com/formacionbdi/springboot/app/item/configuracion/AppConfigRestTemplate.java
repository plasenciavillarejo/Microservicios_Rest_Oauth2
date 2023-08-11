package com.formacionbdi.springboot.app.item.configuracion;

import java.time.Duration;

import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

// Clase encargada de realizar la configuración rest template para la comunicación entre microservicios

@Configuration
public class AppConfigRestTemplate {
	
	/* Le indicamos donde va a estar mapeado -> 
		ItemServiceImpl.java / private RestTemplate clienteRest;
	*/
	@Bean
	// Cofiguramos el Balanceador con la anotación RestTemplate, de forma automática utiliza Ribbon
	@LoadBalanced
	public RestTemplate registrarRestTemplate() {
		return new RestTemplate();
	}
	
	
	/* Configuración parámetros Resilicience sin anotaciones
	@Bean
	public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomicer() {
		// Devolvemos una expresion lambda
		return factory -> factory.configureDefault(identificador -> {
			return new Resilience4JConfigBuilder(identificador)
					.circuitBreakerConfig(CircuitBreakerConfig.custom()
							// Solo queremos 10 Requeste en la ventana deslizante inicial
							.slidingWindowSize(10)
							// Umbral de fallo, le dejamos el 50%
							.failureRateThreshold(50)
							// Tiempo de espera despues de superar el umbral -> 10 segundos
							.waitDurationInOpenState(Duration.ofSeconds(10L))
							// Cambiamos el numero de llamadas en el circuito semi abierto, por defecto son 10 -> Cambiamos a 5
							.permittedNumberOfCallsInHalfOpenState(5)
							// Configuración de Llamdas lentas -> Configuramos el umbral de llamdas lenta en un 50%
							.slowCallRateThreshold(50)
							/* Tiempo máximo que se puede demorar una llamada, si supera dicho tiempo se considera una llamda lenta -> Por defecto 2 segundos.
							 Nota -> Tiene prioridad el TimeOut por lo que debemos de aumentar dicho time out para que funciona bien -> timeLimiterConfig()
							 Si no supera el timeout(configurado en 7 segundos) la llamda se registra como llamada lenta. Cuando supera el umbral entrara en cortocircuito
							 y ejecutara el metodoAlternativo(). Mientras tanto las 5/10 primeras llamadas se registraran como lentas y no entrara por el metodoAlternativo()
							 pero al ejecutar la 6 llamda ya entrara por el métodoAlternativo/() ya que ha superado el umbra máximo (5/10).
							 Algo que hay que destacar que no se registra en llamada con umbral de fallo si no umbral de timeout que es distinto
							 */
		/* DESCOMENTAR 
							.slowCallDurationThreshold(Duration.ofSeconds(2))
							.build())
					// Tiempo límite por defecto si la llamda se demora más de 1 segundo va a emitir un TimeOut.
					.timeLimiterConfig(TimeLimiterConfig.custom()
							// Tiempo límite de duración entre la llamadas. 
							.timeoutDuration(Duration.ofSeconds(7L))
							.build())
					.build();
		});
	}
		 DESCOMENTAR */
}
