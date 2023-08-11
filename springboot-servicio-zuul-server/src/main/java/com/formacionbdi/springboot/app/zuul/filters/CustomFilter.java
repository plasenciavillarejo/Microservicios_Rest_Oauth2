package com.formacionbdi.springboot.app.zuul.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.GenericFilter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

public class CustomFilter extends GenericFilter {

	private static final long serialVersionUID = 6190584931427481608L;
	
	Logger LOGGER = LoggerFactory.getLogger(CustomFilter.class);	
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// Aquí puedes realizar cualquier lógica de filtrado que necesites antes de continuar con el FilterChain de Spring Security
	    // Por ejemplo, puedes hacer log de la solicitud o verificar ciertas condiciones

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		
		String url = httpRequest.getRequestURI();
		
		if(!"/api/security/oauth/token".equals(url)) {
		    
			//String username = (String) request.getAttribute("username");
		   	//userDetailsService.loadUserByUsername(username);
		   	
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
		}else {
			// Si no es la URL que queremos intervenir debe proseguir el filtro.
			chain.doFilter(request, response);
		}
	}

}
