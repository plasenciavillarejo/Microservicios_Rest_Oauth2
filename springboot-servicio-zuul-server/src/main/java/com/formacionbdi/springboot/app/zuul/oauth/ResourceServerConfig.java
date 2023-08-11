package com.formacionbdi.springboot.app.zuul.oauth;

import java.util.Arrays;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


// Por si cambiamos algun valor dentro de el properties para que lo coja en caliente
@RefreshScope
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter{
	
	/*	##############################
	 	## INI: Diferentes Filtros. ##
	 	##############################
	*/
	/* 1.- Le indico que antes de que ejecute el FilterChain de Spring security me ejecute mi propio filtro personalizado con el @Order(1).
		Está configuracion se debe indicar cuando utilizamos el .addFilter(new CustomFilter())
	
	@Bean
	@Order(1)
	public CustomFilter filtroPersonalizado() {
		return new CustomFilter();
	}
	*/
		
    /* 2.- Cuando vamos a ejecutar el un filtro que extiende de AbstractAuthenticationProcessingFilter debemos anotar el RequestMatcher para indicarle que ruta unicamente debe interceptar
		Este filtro puede actuar para meter el contexto de spring al usuario pero en nuestro caso podemos obviarlo ya que tenemos la authenticacion con el AuthenticationProvider()
		public class CustomAuthenticationProvider implements AuthenticationProvider
	*/
	@Bean
    public RequestMatcher customRequestMatcher() {
        //return new AntPathRequestMatcher("/api/usuarios/usuarios");
		// Intercepta la petición antes de que se logue el usuario.
		return new AntPathRequestMatcher("/api/security/oauth/token");
    }
	
	
	/* Forma de registrar un Filtro utilizando CompositeFilter 'Listado de filtros' 
	@Bean	
    public CompositeFilter compositeFilter() {
        CompositeFilter compositeFilter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();
        filters.add(new CustomFilterSpringSecurity(customRequestMatcher()));
        compositeFilter.setFilters(filters);
        return compositeFilter;
	}
	
	@Bean
	public FilterRegistrationBean<CompositeFilter> customCompositeFilterRegistration() {
		FilterRegistrationBean<CompositeFilter> registrationBean = new FilterRegistrationBean<>(compositeFilter());
		registrationBean.setOrder(1);
		return registrationBean;
	}
	
	*/
	/*	##############################
	 	## FIN: Diferentes Filtros. ##
	 	##############################
	 */
	
	
	@Value("${config.security.ouath.jwt.key}")
	private String keySecretAuthorization;
	
	/*
	1.- Debemo de implementar dos métodos:
		- Uno para proteger nuestras rutas, endpoints
		- Otro para configurar el token con la misma estructura que el servidor de Autorización
	 */
	
	
	// Se configura el token -> JwtTokenStore() y JwtAccessTokenConverter()
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		// Con esto se configura
		resources.tokenStore(tokenStore());
	}

	// Proteger las rutas
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
			// 1.- Forma de implementar un CompositeFiler()
			//.addFilterBefore(compositeFilter(),UsernamePasswordAuthenticationFilter.class)
			// 2.- Si quiero ejecutar antes el filtro que el propio de Spring Security tengo que utilizar -> addFilterBefore
			//.addFilterBefore(new CustomFilterSpringSecurity(customRequestMatcher()),  UsernamePasswordAuthenticationFilter.class) // BasicAuthenticationFilter.class
			/* Con este tipo de filtro capturo la petición antes de que el usuario se almacene en el contexto de spring security -> AbstractPreAuthenticatedProcessingFilter.class
			  Esto quiere decir que entra antes de que entre a mi implmentación -> implements UserDetailsService
			 */
			//.addFilterBefore(new CustomFilterSpringSecurity(customRequestMatcher()),  AbstractPreAuthenticatedProcessingFilter.class)
		    // 3.- Filtro de la API SERVLET 
			//.addFilterBefore(filtroPersonalizado(),  UsernamePasswordAuthenticationFilter.class) // BasicAuthenticationFilter.class // 
			.authorizeRequests()
			.antMatchers("/api/security/oauth/token").permitAll()
			.antMatchers(HttpMethod.GET, "/api/productos/listar", "/api/items/listar").permitAll()
			.antMatchers(HttpMethod.GET, "/api/productos/ver/{id}", "/api/productos/ver/{id}/cantidad/{cantidad}", "/api/usuarios/usuarios/{id}","/api/usuarios/usuarios").hasAnyRole("ADMIN","USER")
			.antMatchers("/api/productos/**", "/api/items/**", "/api/usuarios/**").hasRole("ADMIN")
			.anyRequest()
			.authenticated()
			// Configuramos el acceso de el front-end 'Angular' a nuestras aplicación REST
			.and().cors().configurationSource(corsConfigurationSource());
			/* De forma más detallada 
			.antMatchers(HttpMethod.POST, "/api/productos/crear", "/api/items/crear", "/api/usuarios/usuarios").hasRole("ADMIN")
			.antMatchers(HttpMethod.PUT, "/api/productos/editar/{id}", "/api/items/crear/{id}", "/api/usuarios/usuarios/{id}").hasRole("ADMIN")
			.antMatchers(HttpMethod.DELETE, "/api/productos/eliminar/{id}", "/api/items/eliminar/{id}", "/api/usuarios/usuarios/{id}").hasRole("ADMIN");
			*/
	}
	
	// ##########################
	// INI: Configuración ANGULAR
	// ##########################
	// Método encargado de dar acceso al front a nuestro back-end
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfig = new CorsConfiguration();
		// Nombre dominio con el puerto
		corsConfig.setAllowedOrigins(Arrays.asList("*"));
		corsConfig.setAllowedMethods(Arrays.asList("POST","GET","PUT","DELETE","OPTIONS"));
		corsConfig.setAllowCredentials(true);
		corsConfig.setAllowedHeaders(Arrays.asList("Authorization","Content-Type"));
		
		// Pasamos el corsConfig a nuestras rutas urls
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		// se aplique a todas nuestras rutas
		source.registerCorsConfiguration("/**", corsConfig);
		
		return source;
	}

	// Se configura tambien un filtro para que quede configurado a nivel global, para que no este en spring security si no en toda la aplicación.
	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter() {
		FilterRegistrationBean<CorsFilter> filtro = new FilterRegistrationBean<CorsFilter>(new CorsFilter(corsConfigurationSource()));
		filtro.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return filtro;
	}
	
	// ##########################
	// FIN: Configuración ANGULAR
	// ##########################
	
	@Bean
	public JwtTokenStore tokenStore() {
		// Recibe como argumento el accessTokenConverter()
		return new JwtTokenStore(accessTokenConverter());
	}
	
	// Debe mantener la misma configuración que en el proyecto ouath ya que estamos validando la palabra secreta y debe ser la la misma.
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		
		String keySecret = "algun_codigo_secreto_123456";
		if(!keySecretAuthorization.isEmpty()) {
			keySecret = keySecretAuthorization;
		}
		
		JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
		tokenConverter.setSigningKey(Base64.getEncoder().encodeToString(keySecret.getBytes()));
		return tokenConverter;
	}
	



	
	
	
}
