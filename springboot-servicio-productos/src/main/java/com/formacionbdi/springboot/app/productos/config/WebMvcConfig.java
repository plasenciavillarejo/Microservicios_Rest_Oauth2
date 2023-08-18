package com.formacionbdi.springboot.app.productos.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.formacionbdi.springboot.app.productos.interceptor.CustomTraceInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Autowired
	private CustomTraceInterceptor customTraceInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(customTraceInterceptor).addPathPatterns("/listar");
	}
	
	
	

}
