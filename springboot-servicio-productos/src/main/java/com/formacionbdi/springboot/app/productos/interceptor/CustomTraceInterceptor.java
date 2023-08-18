package com.formacionbdi.springboot.app.productos.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import brave.Tracer;

@Configuration
public class CustomTraceInterceptor implements HandlerInterceptor {

	@Autowired
	private Tracer trace;

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomTraceInterceptor.class);

	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		LOGGER.info("Ejecutando flujo de Interceptor de entrada");

		return true;

	}

}
