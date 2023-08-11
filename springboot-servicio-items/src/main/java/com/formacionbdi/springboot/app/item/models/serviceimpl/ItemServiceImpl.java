package com.formacionbdi.springboot.app.item.models.serviceimpl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.formacionbdi.springboot.app.item.models.Item;
import com.formacionbdi.springboot.app.shared.library.models.entity.Producto;
import com.formacionbdi.springboot.app.item.models.service.IItemService;

@Service("serviceRestTemplate")
public class ItemServiceImpl implements IItemService {

	@Autowired
	private RestTemplate clienteRest;
	
	@Override
	public List<Item> findAll() {
		/* Nota:
		 Cuando queremos recuperar una lista de producto, por defecto nos devuelve una Array de Producto 'Producto []', entonces
		 debemos de indicar un arreglo de la clase Producto 'Producto[].class' y luego castearlo con 'Arrays.asList()', con esto ya 
		 tendríamos la lista de Producto -> List<Producto>
		*/
		
		/* Se comena ya que al utilizar el balanceador de carga cambia la forma en la que se llama al servicio
			
			List<Producto> productos = Arrays.asList(clienteRest.getForObject("http://localhost:8001/listar", Producto[].class));
			
		   Se indica el nombre que contiene el properties de el proyecto Productos -> servicio-productos
		*/
		List<Producto> productos = Arrays.asList(clienteRest.getForObject("http://servicio-productos/listar", Producto[].class));
		
		// Vamos a convertir la lista de productos en una lista de items, utilizando stream() para convertirlo en un flujo.
		// Utilizamos el map para convertir un objeto de producto en un objeto item
		return productos.stream().map(p -> new Item(p,1)).collect(Collectors.toList());
	}

	@Override
	public Item findById(Long id, Integer cantidad) {
		Map<String, String> pathVariables = new HashMap<>();
		pathVariables.put("id", id.toString());
		
		/* Se comenta para utilizar la forma de implementación con el baleanceador de carga
			Producto producto = clienteRest.getForObject("http://localhost:8001/ver/{id}", Producto.class, pathVariables);
		*/
		
		Producto producto = clienteRest.getForObject("http://servicio-productos/ver/{id}", Producto.class, pathVariables);
		
		return new Item(producto, cantidad);
	}

	@Override
	public Producto guardarItem(Producto producto) {
		/*
		  Exchange -> Sirve para intercambiar cuando estamos consumienso Serviciso REST en la cual enviamos un endpoint mediante una URL
		  	en el cuerpo de la respuesta.
		  		1.- El endpoint a donde se envía -> http://servicio-productos/crear
		  		2.- Tipo de servicio que se va a consumir -> HttpMethos.POST
		  		3.- Enviamos el Request(body) que contiene el producto -> Lo llamamos body por ejemplo
		  		4.- Tipo de dato como queremos recibir el objeto Json que retorna -> El tipo Producto.class  
		  */
		// Esto hace que se escriba en el cuerpo de el request de la petición
		HttpEntity<Producto> body = new HttpEntity<Producto>(producto);
		ResponseEntity<Producto> respuesta = clienteRest.exchange("http://servicio-productos/crear", HttpMethod.POST, body,Producto.class);
		Producto productoResponse = respuesta.getBody();
		return productoResponse;
	}

	@Override
	public Producto actualizarItem(Producto producto, Long id) {
		
		Map<String, String> pathVariables = new HashMap<>();
		pathVariables.put("id", id.toString());
		
		// Esto hace que se escriba en el cuerpo de el request de la petición
		HttpEntity<Producto> body = new HttpEntity<Producto>(producto);
		ResponseEntity<Producto> respuesta = clienteRest.exchange("http://servicio-productos/editar/{id}", HttpMethod.PUT, 
				body,Producto.class,pathVariables);
		Producto productoResponse = respuesta.getBody();
		return productoResponse;
	}

	@Override
	public void eliminarItem(Long id) {
		clienteRest.delete("http://servicio-productos/eliminar/{id}", id);
	}



}
