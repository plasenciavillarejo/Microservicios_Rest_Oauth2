package com.formacionbdi.springboot.app.productos.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import brave.Tracer;

@Component
public class FilterTrace extends OncePerRequestFilter  {

	@Autowired
	private Tracer trace;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		Map<String, Object> traceName = new HashMap<>();
		traceName.put("trId", trace.currentSpan().context().traceId());
		filterChain.doFilter(request, response);
	}

}
