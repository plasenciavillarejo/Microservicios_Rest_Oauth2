package com.formacionbdi.springboot.app.productos.controllers;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.formacionbdi.springboot.app.productos.models.service.IProductoService;
import com.formacionbdi.springboot.app.shared.library.models.entity.Producto;

import brave.Tracer;


@RestController
public class ProuctoController {

	public static final Logger LOGGER = LoggerFactory.getLogger(ProuctoController.class); 
	
	// Vamos a obtener el puerto almacenado en el application.propeties
	@Autowired
	private Environment env;
	
	// Otra forma de recupear el puerto desde el application.properties
	@Value("${server.port}")
	private Integer puerto;
	
	// Cuando tenemos la aplicación con puerto automático se puede obtener dicho puerto con el siguiente Servlet 
	@Autowired
	private ServletWebServerApplicationContext webServerAppCtxt;
	
	
	@Autowired
	private IProductoService productoService;
	
	@Autowired
	private Tracer trace;
	
	
	/*
	@GetMapping({"/listar","/"})
	public List<Producto> listarProducto() {
		// podemos modificar el flujo con la función map de stream()
		return productoService.findAll().stream().map(producto ->{
			//producto.setPuerto(Integer.parseInt(env.getProperty("local.server.port")));
			//producto.setPuerto(puerto);
			producto.setPuerto(webServerAppCtxt.getWebServer().getPort());
			return producto;
		}).collect(Collectors.toList());
	}
	
	*/
	
	@GetMapping({"/listar","/"})
	public ResponseEntity<List<Producto>> listarProducto() {
		// podemos modificar el flujo con la función map de stream()
		// return new ResponseEntity<List<Address>>(addressService.findAddresByProfileAndUserId(userId, profileId), HttpStatus.OK);
		List<Producto> producto = productoService.findAll();
		for(Producto p: producto) {
			p.setPuerto(webServerAppCtxt.getWebServer().getPort());
		}		
		
		trace.currentSpan().tag("Ejemplo.Etiqueta", "PLASENCIA EL ETIQUETAS");
		return new ResponseEntity<List<Producto>>(producto,HttpStatus.OK);
	}
	
	
	
	@GetMapping("/ver/{id}")
	public Producto detalle(@PathVariable Long id) {
		Producto producto = productoService.fingById(id); 
		
		// Simulamos errores para trabajar con Resilicience
		if(id.equals(10L)) {
			LOGGER.info("Forzando el fallo con el id {} para verificar que funciona el metodoAlternativo configurado en caso de falla con Resilience", id);
			throw new IllegalStateException("Producto no encontrado");
		} else if(id.equals(7L)) {
			try {
				LOGGER.info("Parando el servicio durante 5 segundos, se procede a dar un timeout e ir por el metodoAlternativo configurado con Resilience");
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//producto.setPuerto(Integer.parseInt(env.getProperty("local.server.port")));
		//producto.setPuerto(puerto);
		
		// Vamos a generar un pequeño error para que lo capture Hystrix en el proyecto de ITEMS.
		/*boolean ok = false;
		if(!ok) {
			throw new RuntimeException("Simulando un error para validar Hystrix en el proyecto Items, por si falla, generar otro circuito de este modo"
					+ "no se verá caído el microservicio. Esto se deberá de implemenar en el controlador de el proyecto ITEMS");
		}
		*/
		
		producto.setPuerto(webServerAppCtxt.getWebServer().getPort());
		return producto;
	}

	/*
	 @RequestBody -> De forma automática toman los datos que viene en el cuerpo de la petición del request, hace un binding y lo convierte a un objeto Producto
	 siempre y cuando los atributos de este JSON corresponda a los mismo que hay en la clase Producto.java
	 */
	@PostMapping(value = "/crear")
	@ResponseStatus(value = HttpStatus.CREATED)
	public Producto crear(@RequestBody Producto producto) {
		return productoService.guardarProducto(producto);
	}
	
	
	@PutMapping(value = "/editar/{id}")
	@ResponseStatus(value = HttpStatus.CREATED)
	public Producto editar(@RequestBody Producto producto, @PathVariable("id") Long id) {
		Producto buscarProducto = productoService.fingById(id);
		
		if(buscarProducto != null) {
			buscarProducto.setNombre(producto.getNombre());
			buscarProducto.setPrecio(producto.getPrecio());
		}
		return productoService.guardarProducto(buscarProducto);
	}
	
	@DeleteMapping(value = "/eliminar/{id}")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void eliminar(@PathVariable("id") Long id) {
		productoService.deleteById(id);
	}
	
}
