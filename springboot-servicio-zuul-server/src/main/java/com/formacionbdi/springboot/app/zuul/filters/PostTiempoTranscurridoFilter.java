package com.formacionbdi.springboot.app.zuul.filters;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Component
public class PostTiempoTranscurridoFilter extends ZuulFilter {

	public static final Logger log = LoggerFactory.getLogger(PostTiempoTranscurridoFilter.class);
	
	
	/* 4.- Este es el ecargado de validar, si vamos o no ejecutar el filtro
		Si retorna 'true' pasa a ejecutar el método -> public Object run();
		En el se puede indicar lógica de negocio o algún parámetro en la ruta para seguir ejecutandose o en su defecto
		no proseguir con el filtro.
	 	*/
	@Override
	public boolean shouldFilter() {		
		// Por defecto vamos a retornar true en nuestro ejemplo
		return true;
	}

	/* 3.- Se resuelve la lógica de nuestro métdo */
	@Override
	public Object run() throws ZuulException {
		
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		
		log.info("Entrando en el PostTiempoTranscurridoFilter");
		
		Long tiempoInicio = (Long) request.getAttribute("tiempoInicio");
		Long tiempoFinal = System.currentTimeMillis();
		
		Long tiempoTranscurrido = tiempoFinal - tiempoInicio;
		
		log.info("Tiempo transcurrido en segundos {}", tiempoTranscurrido.doubleValue()/1000.00);
		log.info("Tiempo transcurrido en milisegundos {}", tiempoTranscurrido);
		return null;
	}

	/* 1.- Primero definimos el tipo de filtro que vamos a utilizar:
	   	"pre" -> Antes de que se ejecute esa ruta
	   	"post" -> Depues de que se ejecute
	   	"route" -> Cuando se va a enrutar 
	 */
	@Override
	public String filterType() {
		return "post";
	}

	/* 2.- Debemos de coloar el orden, será el primero por lo que se indica '1' */
	
	@Override
	public int filterOrder() {
		return 1;
	}

}
