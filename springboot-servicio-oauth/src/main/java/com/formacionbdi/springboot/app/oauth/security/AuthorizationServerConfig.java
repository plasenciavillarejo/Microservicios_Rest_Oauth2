package com.formacionbdi.springboot.app.oauth.security;

import java.util.Arrays;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;


//Por si cambiamos algun valor dentro de el properties para que lo coja en caliente
@RefreshScope
// Habilitamos la clase como un servidor de autorizacion
@EnableAuthorizationServer
@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	// Como ya hemos inyectado los bena de BCryptPasswordEncoder y AuthenticationManager, podemos ya inectarlos con @Autowired.
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private InfoAdicionalToken infoAdicionalToken;
	
	@Autowired
	private Environment env;
	
	
	@Value("${config.security.oauth.client.id}")
	private String usernameAuthorization;
	
	@Value("${config.security.oauth.client.password}")
	private String passwordAuthorization;
	
	@Value("${config.security.ouath.jwt.key}")
	private String keySecretAuthorization;
	
	
	/*
		Permisos que van a tener nuestros endpoints del servidor de authorizacion 'oauth2' para genera y validar el token.
	*/
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		// 1.- La idea es que sea público para que cualquier persona pueda acceder a esta ruta para generar el token /oauth/token
		security.tokenKeyAccess("permitAll")
		// 2.- Se encarga de validar el token mediante Authenticacion
		.checkTokenAccess("isAuthenticated()");
	}

	// Encargado de proteger nuestro cliente FRONT 'Angular, React, etc...' cualquier peticion que consuma nuestros recursos debemos registralos con su cliente id, y con su password
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		/* 1.- Registramos nuestros clientes de el front. Utilizamos inMemory() pero podemos usar JDBC
			 	1.1.- En postman se debe indicar -> http://localhost:8090/api/security/oauth/token:
			 		Authorization:
			 			Username: frontendapp,
			 			Password: 12345
			 	Esta parte es para el cliente FRONT que se quiere comunicar con nuestro recursos.
		 	2.- Luego debemos ir a 'Body':
		 		x-www-form-urlencoded:
		 			username: jose
		 			password: 12345
		 			grant_type: password
		 		Esta configuración es la que contiene nuestro Back-End almacenada en Base de datos.
		 	Si todo ha ido correcto nos devolverá un token, refres_token, etc...
		*/
		String username= "frontendapp";
		if(!usernameAuthorization.isEmpty()) {
			username = usernameAuthorization;
		}
		String password = "12345";
		if(!passwordAuthorization.isEmpty()) {
			password = passwordAuthorization;
		}
		
		clients.inMemory()
			.withClient(username)
			.secret(passwordEncoder.encode(passwordAuthorization))
			// 2.- Configuramos el alcance de la aplicación, de lectura y escritura (read, write)
			.scopes("read","write")
			/* 3.- Tipo de Authorizacion que va a tener nuestra aplicación, como va a obtener el token. En nuestro caso con "password" cuando es con credenciales
				También vamos a tener el 'refresh_token' -> Nos permite un nuevo token justo antes de que caduque el que tenemos en vigor
			*/
			.authorizedGrantTypes("password", "refresh_token")
			// 4.- Tiempo de validez de el token -> 3600 sec '1 hora'
			.accessTokenValiditySeconds(3600)
			// 5.- Tiempo de el refresh token
			.refreshTokenValiditySeconds(3600)
			.and()
			// 6.- Un segundo cliente llamado androidapp
			.withClient("androidapp")
			.secret(passwordEncoder.encode("12345")) 
			.scopes("read","write")
			.authorizedGrantTypes("password", "refresh_token")
			.accessTokenValiditySeconds(3600)
			.refreshTokenValiditySeconds(3600);
	}

	
	// (Relacionado con la configuracion de el servidor OAUTH2 encargada de generar el token) En esta clase se configura todo referente al authenticatioManager,jwtt, claims, etc ....
	// Registramos el authenticationManager en el autorizationServer
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		
		// Para agregar la información adicioanl al token que hemos creado en la clase InfoAdicionalToken.java debemos añadir la siguiente configuración para unir los datos de el token:
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(infoAdicionalToken, accessTokenConverter()));
		
		// 1.- Registramos el AuthenticationManager()
		endpoints.authenticationManager(authenticationManager)
		// 2.- Configuramos el componente que se encargar de crear el token y almacenarlo con los datos de accessTokenConverter()
		.tokenStore(tokenStore())
		// 3.- Configuramos el acceso para que sea de tipo JWTT
		.accessTokenConverter(accessTokenConverter())
		// 4.- Añadimos la cadena de tokens creada adicionalmente desde la clase InfoAdicionalToken
		.tokenEnhancer(tokenEnhancerChain);
		
	}

	@Bean
	public JwtTokenStore tokenStore() {
		// Recibe como argumento el accessTokenConverter()
		return new JwtTokenStore(accessTokenConverter());
	}
	
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		
		String keySecret = "algun_codigo_secreto_123456";
		if(!keySecretAuthorization.isEmpty()) {
			keySecret = keySecretAuthorization;
		}
		
		JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
		// Creamos un código secreto para firmar el token, despues se utilizar en el servidor de recursos para validar el token si es correcto para dar acceso a los recursos protegidos.
		tokenConverter.setSigningKey(Base64.getEncoder().encodeToString(keySecret.getBytes()));
		return tokenConverter;
	}
	
	
	
	
}
