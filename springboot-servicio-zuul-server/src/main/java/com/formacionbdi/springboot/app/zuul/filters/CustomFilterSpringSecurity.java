package com.formacionbdi.springboot.app.zuul.filters;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

/* Este método attemptAuthentication se encarga de intentar autenticar al usuario. Si no estás realizando una autenticación adicional aquí y solo deseas acceder a la información del usuario autenticado,
  podrías considerar no sobrescribir este método en absoluto. En cambio, podrías acceder a la información del usuario autenticado en otros componentes de tu aplicación donde sea necesario.
*/
public class CustomFilterSpringSecurity extends AbstractAuthenticationProcessingFilter {
	
	Logger LOGGER = LoggerFactory.getLogger(CustomFilterSpringSecurity.class);
	
	public CustomFilterSpringSecurity(RequestMatcher requiresAuthenticationRequestMatcher) {
		super(requiresAuthenticationRequestMatcher);
	}

	@Autowired
	private UserDetailsService userDetailsService;
		
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {		
		
		LOGGER.info("Usuario logueado: {} ",request.getParameter("username"));
		LOGGER.info("Password logueado: {} ",request.getParameter("password"));
		
		String nombre = request.getParameter("username");		
		User usuario = (User) userDetailsService.loadUserByUsername(nombre);		
		UsernamePasswordAuthenticationToken authenticationToken = null;
		
		if(usuario != null) {
			authenticationToken = new UsernamePasswordAuthenticationToken(
					usuario.getUsername(), usuario.getPassword());
		}		
		return getAuthenticationManager().authenticate(authenticationToken);
	}
	
    private String extractTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

}
