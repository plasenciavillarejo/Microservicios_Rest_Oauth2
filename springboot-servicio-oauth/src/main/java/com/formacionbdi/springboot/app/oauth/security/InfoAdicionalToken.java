package com.formacionbdi.springboot.app.oauth.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import com.formacionbdi.springboot.app.oauth.services.IUsuarioService;
import com.formacionbdi.springboot.app.usuarios.commons.models.entity.Usuario;

@Component
public class InfoAdicionalToken implements TokenEnhancer{

	@Autowired
	private IUsuarioService usuarioService;
	
	// Se utiliza para agregar nueva información al token también llamados 'CLAIMS'
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		Map<String, Object> info = new HashMap<>();
		
		Usuario usuarioAutenticado = usuarioService.findByUsername(authentication.getName());
		
		info.put("nombre", usuarioAutenticado.getNombre());
		info.put("apellido", usuarioAutenticado.getApellido());
		info.put("email", usuarioAutenticado.getEmail());
		
		// Necesitamos una interfaz mas concreta para poder asingar está información, casteamos de -> DefaultOAuth2AccessToken
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
		
		// Retornamos el accessToken
		return accessToken;
	}

}
