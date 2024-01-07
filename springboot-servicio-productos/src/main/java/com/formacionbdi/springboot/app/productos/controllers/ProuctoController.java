package com.formacionbdi.springboot.app.productos.controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.formacionbdi.springboot.app.productos.models.service.IFacturaService;
import com.formacionbdi.springboot.app.productos.models.service.IProductoService;
import com.formacionbdi.springboot.app.productos.models.service.IRegionService;
import com.formacionbdi.springboot.app.shared.library.models.entity.Factura;
import com.formacionbdi.springboot.app.shared.library.models.entity.Producto;
import com.formacionbdi.springboot.app.shared.library.models.entity.Region;

import brave.Tracer;

// CrossOrigin para conectar con el front de angular
//@CrossOrigin(origins = {"http://localhost:4200"}, allowedHeaders = "*")
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
	
	@Autowired
	private IRegionService regionService;
	
	@Autowired
	private IFacturaService facturaService;
	
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
	public ResponseEntity<?> detalle(@PathVariable Long id) {
		Producto producto = null;
		Map<String, Object> objetoRespuesta = new HashMap<>();
		
		try {
			producto = productoService.fingById(id); 
		}catch (DataAccessException e) {
			objetoRespuesta.put("mensaje", "Error al realizar la consulta a BBDD.");
			objetoRespuesta.put("error", e.getMessage().concat(" : ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(objetoRespuesta, HttpStatus.NOT_FOUND);
		}
		
		if(producto == null) {
			objetoRespuesta.put("mensaje", "El cliente ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(objetoRespuesta, HttpStatus.NOT_FOUND);
		}
		
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
		return new ResponseEntity<Producto>(producto, HttpStatus.OK);
	}

	/*
	 @RequestBody -> De forma automática toman los datos que viene en el cuerpo de la petición del request, hace un binding y lo convierte a un objeto Producto
	 siempre y cuando los atributos de este JSON corresponda a los mismo que hay en la clase Producto.java
	 */
	@PostMapping(value = "/crear")
	//@ResponseStatus(value = HttpStatus.CREATED)
	public ResponseEntity<?> crear(@Valid @RequestBody Producto producto, BindingResult bindigResult) {
		Map<String, Object> objetoRespuesta = new HashMap<>();
		
		if(bindigResult.hasErrors()) {
			
			// 1º Forma -> Convertimos los errores en una lista de la forma tradicional.
			/*
			List<String> listaErrores = new ArrayList<>();
			for(FieldError err: bindigResult.getFieldErrors()) {
				listaErrores.add("El campo ".concat(err.getField().concat(" ").concat(err.getDefaultMessage())));
			}
			*/
			// 2º Forma -> Método con Api 8
			
			List<String> listaErrores = bindigResult.getFieldErrors()
					.stream()
					.map(err -> {
						return "El campo ".concat(err.getField().concat(" ").concat(err.getDefaultMessage()));
					})
					.collect(Collectors.toList());
			
			objetoRespuesta.put("errors", listaErrores);
					
			return new ResponseEntity<Map<String, Object>>(objetoRespuesta,HttpStatus.BAD_REQUEST);
		}else if(producto.getNombre() == null) {
			objetoRespuesta.put("mensaje", "Error al crear el usuario en la base de datos.");
			return new ResponseEntity<Map<String, Object>>(objetoRespuesta,HttpStatus.INTERNAL_SERVER_ERROR);
		} else if(producto.getCreateAt() == null) {
			producto.setCreateAt(new Date());
		}
		productoService.guardarProducto(producto);
		return new ResponseEntity<Producto>(producto, HttpStatus.CREATED);
				
	}
	
	
	@PutMapping(value = "/editar/{id}")
	//@ResponseStatus(value = HttpStatus.CREATED)
	public ResponseEntity<?> editar(@RequestBody Producto producto, @PathVariable("id") Long id) {
		Producto buscarProducto = productoService.fingById(id);
		Map<String, Object> objetoRespuesta = new HashMap<>();
		if(buscarProducto != null) {
			buscarProducto.setNombre(producto.getNombre());
			buscarProducto.setPrecio(producto.getPrecio());
			buscarProducto.setPuerto(webServerAppCtxt.getWebServer().getPort());
			buscarProducto.setCreateAt(producto.getCreateAt());
			buscarProducto.setRegion(producto.getRegion());
		} else if(producto.getNombre() == null) {
			objetoRespuesta.put("mensaje", "Error al editar el usuario en la base de datos.");
			return new ResponseEntity<Map<String, Object>>(objetoRespuesta,HttpStatus.INTERNAL_SERVER_ERROR);
		} 
		productoService.guardarProducto(buscarProducto);			
		return new ResponseEntity<Producto>(buscarProducto, HttpStatus.CREATED);
	}
	
	@DeleteMapping(value = "/eliminar/{id}")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public ResponseEntity<?> eliminar(@PathVariable("id") Long id) {
		Map<String, Object> objetoRespuesta = new HashMap<>();
		try {
			borrarFotoProducto(id);
			productoService.deleteById(id);
		}catch (DataAccessException e) {
			objetoRespuesta.put("mensaeje", "Error al eliminar el producto de la base de datos");
			objetoRespuesta.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(objetoRespuesta, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		objetoRespuesta.put("mensaje", "El cliente ha sido eliminado con éxito.");
		return new ResponseEntity<Map<String, Object>>(objetoRespuesta, HttpStatus.OK);
		
	}

	@GetMapping(value = "/listar/page/{pagina}")
	public Page<Producto> listaPaginada(@PathVariable("pagina") int pagina) {
		// 4 -> 4 Registor por página se va a mostrar.
		Pageable pageable = PageRequest.of(pagina, 4);
		return productoService.paginador(pageable);
	}
	
	@PostMapping(value = "/upload")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("id") Long id) {
		Map<String, Object> objetoRespuesta = new HashMap<>();
		Producto producto = productoService.fingById(id);

		if (!file.isEmpty()) {
			String nombreArchivo = UUID.randomUUID().toString() + "_" + file.getOriginalFilename().replace(" ", "");
			Path rutaArchivo = Paths.get("C://Users//maplvijo//Documents//upload").resolve(nombreArchivo)
					.toAbsolutePath();
			try {
				Files.copy(file.getInputStream(), rutaArchivo);
			} catch (IOException e) {
				LOGGER.error("Error al subir la imágen {}", e.getMessage(), e);
				objetoRespuesta.put("mensaje", "Error al subir la imágen".concat(e.getCause().getMessage()));
				return new ResponseEntity<>(objetoRespuesta, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			if (producto.getFoto() != null) {
				borrarFotoProducto(id);
			}

			producto.setFoto(nombreArchivo);

			// Como el puerto no puede ser nulo y se agrega de forma automática lo que tengo
			// que hacer es volver asignarlo
			if (producto.getPuerto() == null) {
				producto.setPuerto(webServerAppCtxt.getWebServer().getPort());
			}
			productoService.guardarProducto(producto);

			objetoRespuesta.put("producto", producto);
			objetoRespuesta.put("mensaje", "Se ha subido correctamente la imágen: ".concat(nombreArchivo));
		}
		return new ResponseEntity<Map<String, Object>>(objetoRespuesta, HttpStatus.CREATED);
	}
	

	public void borrarFotoProducto(Long id) {
		Producto producto = productoService.fingById(id);
		String nombreAnteriorFoto = producto.getFoto();
		
		if(nombreAnteriorFoto!= null) {
			Path rutaFotoAnterior = Paths.get("C://Users//maplvijo//Documents//upload").resolve(nombreAnteriorFoto)
					.toAbsolutePath();
			File archivoFotoAnterior = rutaFotoAnterior.toFile();
			if (archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()) {
				archivoFotoAnterior.delete();
			}
		}
	}
	
	// Como se le va a pasar una imagen con extensión se le agrega una expresión regular -> :.+ (Indica que contiene un punto y la extensión)
	@GetMapping(value= "/verImagen/{nombreFoto:.+}")
	public ResponseEntity<Object> verImagen(@PathVariable(value = "nombreFoto") String nombreImagen) throws IOException {
		
		Path rutaArchivo = Paths.get("C://Users//maplvijo//Documents//upload").resolve(nombreImagen).toAbsolutePath();
		Resource recurso = null;
		try {
			recurso = new UrlResource(rutaArchivo.toUri());
		} catch (MalformedURLException e) {
			LOGGER.error(e.getLocalizedMessage());
		}
		
		if(!recurso.exists() && !recurso.isReadable()) {
			// Cuando no se puede cargar la imagen, por defecto cargamos la que hay por defecto
			rutaArchivo = Paths.get("src/main/resources/static/images").resolve("user.svg").toAbsolutePath();
			try {
				Producto busquedaProducto = productoService.findByFoto(nombreImagen);
				if(busquedaProducto != null) {
					busquedaProducto.setFoto("");
					busquedaProducto.setPuerto(webServerAppCtxt.getWebServer().getPort());
					productoService.guardarProducto(busquedaProducto);
				}
				recurso = new UrlResource(rutaArchivo.toUri());
			} catch (MalformedURLException e) {
				LOGGER.error("Error no se pudo cargar la imágen :" + nombreImagen);
				LOGGER.error(e.getLocalizedMessage());
			}
			LOGGER.error("Se ha cargado correctamente la imágen :" + nombreImagen);
		}		
		// Ahora vamos a pasar la cabecera HttpHeaders para forzar que se pueda descargar el attachment.
		HttpHeaders cabecera = new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+ recurso.getFilename()+ "\"");
			
		return new ResponseEntity<>(recurso.getURL(), cabecera, HttpStatus.OK);
	}
	
	@GetMapping(value = "/listar/regiones")
	public ResponseEntity<List<Region>> listarRegiones() {
		return new ResponseEntity<>(regionService.buscarListaRegiones(),HttpStatus.OK);
	}

	@GetMapping(value = "/listar/factura/{id}")
	public ResponseEntity<List<Factura>> listarFacturaPorId(@PathVariable(value = "id") Long id) {
	  List<Factura> listaFacturas = facturaService.findById(id).stream().collect(Collectors.toList());
	  return new ResponseEntity<>(listaFacturas, HttpStatus.OK);
	}
	

}
