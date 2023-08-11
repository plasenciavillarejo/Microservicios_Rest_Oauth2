package com.formacionbdi.springboot.app.oauth.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.GenericFilter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/*
 
 Este filtro funciona cuando se realiza la peticion de login inicial, pero como tengo todo configurado en el servicio-zuul-server comento este filtro desde este proyecto. Dejo activo el de zuul
 
@Component
public class CustomFilter extends GenericFilter {

	Logger LOGGER = LoggerFactory.getLogger(CustomFilter.class);
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// Aquí puedes realizar cualquier lógica de filtrado que necesites antes de continuar con el FilterChain de Spring Security
	    // Por ejemplo, puedes hacer log de la solicitud o verificar ciertas condiciones

	    boolean condicionesCumplidas = true; // Realiza aquí tu lógica para verificar si las condiciones se cumplen

	    if (condicionesCumplidas) {
	        // Luego, continúa con el FilterChain de Spring Security
	        chain.doFilter(request, response);
	    } else {
	        // Si las condiciones no se cumplen, puedes decidir qué hacer a continuación.
	        // Por ejemplo, puedes enviar una respuesta de error o redireccionar a otra página.
	        HttpServletResponse httpResponse = (HttpServletResponse) response;
	        httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN); // Código de respuesta 403 Forbidden
	        // O puedes redireccionar a una página de error
	        // httpResponse.sendRedirect("/error-page");
	    }	
	}
}
*/