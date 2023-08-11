package com.formacionbdi.springboot.app.item.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.formacionbdi.springboot.app.item.models.Item;
import com.formacionbdi.springboot.app.shared.library.models.entity.Producto;
import com.formacionbdi.springboot.app.item.models.service.IItemService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;


/* 
  Anotamos el controlador con @RefreshScope que nos permite actualizar los componentes (Controlador, Service, Component, etc ...) que inyectamos
  configuraciones anotadas con @Value, Enviroment.
  Actuliza, refresca el contexto y  vuelve a inyectar e inicilizar los componentes con los cambios reflejados en tiempo real sin tener que reinicar
  la aplicación.
  Todo esto se realizar mediante un EndPoint anotado con @Actuator (Debemos añadir la dependencia en el pom.xml)
  
  ** Ejemplo: **
  	Si tenemos el fichero de configuración almacenado en Git/Svn y cambiamos en el servicio-item-dev.properties algun campo, al subirlo al repositorio
  	de forma automática tomara esos valores y se actualizara automáticamente
*/
@RefreshScope
@RestController
public class ItemController {

	// Reemplazo de el HystrixCommand -> Utiliamos Resilience sin anotaciones
	@Autowired
	private CircuitBreakerFactory circuitBreaker;
	
	@Autowired
	private Environment env;
	
	@Value("${configuracion.texto}")
	private String texto;
	
	@Value("${spring.profiles.active}")
	private String entornoEjecucion;
	
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ItemController.class);
	
	@Autowired
	// Le indico que interfaz quiero inyectar
	@Qualifier("serviceFeign")
	//@Qualifier("serviceRestTemplate")
	//private ItemServiceImpl itemServiceImp;
	//private IItemServiceFeignImpl itemServiceImp;
	private IItemService itemService; 
	
	@GetMapping(value ="/listar")
	public List<Item> lisar(@RequestParam(name="nombreParametro", required = false) String nombreParametro, 
			@RequestHeader(name="token-request", required = false) String tokenRequest) {
		LOGGER.info("El nombre de el parámetro recibido desde el filtro es: {}", nombreParametro);
		LOGGER.info("El nombre de el token-request recibido desde el filtro es: {}", tokenRequest);
	
		return itemService.findAll();
	}
	
	
	/* Indicamos una configuración por si falla, que el microservicio tenga otra alternativa y no se vea afectado. Por lo que se va a derivar
	  	a otro microservicio - compatible solo con la versión de spring boot 2.3.X
	  	@HystrixCommand(fallbackMethod = "metodoAlternativo")
	 */
	@GetMapping(value = "/ver/{id}/cantidad/{cantidad}")
	public Item detalle(@PathVariable Long id,@PathVariable Integer cantidad) {	
		/* Creamos un nuevo circuito que lo llamamos "items" y trabajamos con expresiones lambda		 
		   
		   Configuración: (Como trabaja Resilience) 
		   	- Por defecto ya viene configurado el circuite breake con 100 request. Si la tasa de fallo supera el 50% (100 peticiones falla 55) el proceso pasará a circuito abierto.
		   	Esto quiere decir que en las primeras 55 peticiones al fallar se ha ejecutado la clase 'metodoAlternativo' evitando que el proceso falle y no responda a Postman.
		   	Pero ahora desde otra api se puede realizar 45 llamadas (son las restantes que quedan 55 + 45 = 100 request) que ejecutará correctamente el servicio pero cuando llegue a 100
		   	de forma directa se ejecutara el circuito abierto y no realizará más 'Request' durante 1 minuto siempre se ejecutara el 'metodoAlternativo'.
		   	Posteriormente volvera a circuito semi-abierto y tendremos otras 100 request con un umbral del 50% de tolerancia a fallos.
		   	
		   	Ejemplo:
		   	A este endpoint devolverá todo correcto: http://localhost:8002/ver/2/cantidad/5 (Endpoint A)
		   	A este endpoint devolverá siempre un fallo, no se acpeta el id= 10 -> http://localhost:8002/ver/10/cantidad/5 (Endpoint B) 
		   	
		   	Al endpoint B se realiza 55 peticiones, va todo dirigido al -> metodoAlternativo(Long id,Integer cantidad, Throwable e)
		   	Al endpoint A se realizar 45 peticiones -> Todo se ejecuta correctamente.
		   	
		   	En la peticióin 101 -> Ya no acepta más request, se ha sobrepasado las 100 peticiones con un umbral del 50% en fallo, da igual el endpoint que se ejecute que siempre va por el
		   		metodoAlternativo();
		   	
		   	- Depues de 1 minutos en circuito abierto, vuelve a semi-abierto, esto quiere decir que permite hacer 10 request y la tasa de error es supera el 50% vuelve al corto circuito,
		   	de lo contrario volvemos a estado cerrado.
		   		- De 10 peticiones hacemos 7 correctas y 3 mal, entonces el umbral supera las peticiones buenas por tanto el estado cerrado vuelve a tener 100 request
		   	
		   	-Se configura de la siguiente forma
		   		e -> metodoAlternativo(id,cantidad)
		   
		   Utilizamos Resilience sin anotaciones utilizando expresiones lambda
		   
		   return circuitBreaker.create("items").run(
				// Expresión Lambda que sin parámetros de entrada con 'flecha' hacia el servicio -> itemService.findById(id, cantidad)
				() -> itemService.findById(id, cantidad),
					// Si falla, se llama al método alternativo
					e -> metodoAlternativo(id,cantidad)
				);
		   
		 */
		return circuitBreaker.create("items").run(
				// Expresión Lambda que sin parámetros de entrada con 'flecha' hacia el servicio -> itemService.findById(id, cantidad)
				() -> itemService.findById(id, cantidad),
					// Si falla, se llama al método alternativo
					e -> metodoAlternativo(id,cantidad,e)
				);
	}
	
	/* Utilizando anotaciones de @CircuitBreaker - La configuración se carga desde application.properties no vale configurarlo en un fichero de configuración*/
	@CircuitBreaker(name="items", fallbackMethod = "metodoAlternativo")
	@GetMapping(value = "/ver2/{id}/cantidad/{cantidad}")
	public Item detalle2(@PathVariable Long id, @PathVariable Integer cantidad) {
		LOGGER.info("Ejecutando el metodo anotado con @CircuitBreaker");
		return itemService.findById(id, cantidad);
	}
	
	public Item metodoAlternativo(Long id,Integer cantidad, Throwable e) {
		LOGGER.info(e.getMessage());
		LOGGER.info("El método inicial contiene una falla ha sido redireccionado a metodoAlternativo(), se procede a crear el producto y el item");
		// Hacemos algo alternativo al método que ha fallado 'public Item detalle(..)'
		Producto producto = new Producto();
		Item item = new Item();
		
		item.setCantidad(cantidad);
		producto.setId(id);
		producto.setNombre("Método Alternativo a Fallos");
		producto.setPrecio(500.00);
		producto.setCreateAt(new Date());
		producto.setPuerto(0);
		item.setProducto(producto);
		LOGGER.info("Devolviendo el método alternativo para evitar una falla en nuestro microservicio");
		return item;
	}
		
	
	/* Utilizando anotaciones de @TimeLimiter - La configuración se carga desde application.properties no vale configurarlo en un fichero de configuración
	  	Debemos envolverlo "" para una llamada asincrónica que representa una llamada futura que ocurre en el tiempo depues y maneja todo lo que ha hecho el timeout
	  	** Nota ** -> Cuando entra en juego el @TimeLimiter hay que tener en cuenta que no se contabiliza el umbral, dicho de otro modo si tenemos 10 peticiones
	  	y 5 se ejecutan con un tiempo de espera superior a 2s, (configurado en el application.properties) estás no se contabilizan por lo que siempre se ejecutara 
	  	el metodoAlternativoLimiter sin contabilizar el umbral de fallo.
	  	En resumen, no entra en corto-circuito, no se aplican los tiempos de estado abierto, cerrado, etc ...
	  */
	@TimeLimiter(name="items", fallbackMethod = "metodoAlternativoTimeLimiter")
	@GetMapping(value = "/ver3/{id}/cantidad/{cantidad}")
	public CompletableFuture<Item> detalle3(@PathVariable Long id, @PathVariable Integer cantidad) {
		LOGGER.info("Ejecutando el metodo anotado con @TimeLimiter");
		/* Envolvemos la llamda en una representación futura asíncrona para calcular el tiempo de espera, si tenemos un método alternativo al fallo también tiene que implementar
			CompletableFuture<Item>
		*/
		return CompletableFuture.supplyAsync(() -> itemService.findById(id, cantidad)) ;
	}
	
	/* Podemos combinar @CircuitBreaker con @TimeLimiter en este caso ya si entra la casuística de el umbral de fallo, mientra de un timeout dicho umbral quedará vacío, pero 
	   si por alguna motivo falla el endpoint entonces el umbral de fallo será 1/10 si supera el 50% entrara en cortocircuito y solo devolverla el metodoAlternativo que tengamos
	   configurado. Tal y como sucede en la explicación de más arriba
	   
	   Para que el @CircuitBreaker como el @TimeLimiter ejecuten el mismo método alternativo, se deberá de configurar de la siguiente forma:
	   @CircuitBreaker(name="items", fallbackMethod = "metodoAlternativoTimeLimiter")
	   @TimeLimiter(name="items")
	   
	   Por defecto está este código: (Siempre se va a ejecutar el metodoAlernativoTimeLimiter
	   	@CircuitBreaker(name="items")
	   	@TimeLimiter(name="items", fallbackMethod = "metodoAlternativoTimeLimiter")
	   
	   Para ejecutar tanto ambos métodos que están anotadon con @CircuitBreaker y @TimeLimiter se debe anotar de la siguiente forma
		@CircuitBreaker(name="items", fallbackMethod = "metodoAlternativoTimeLimiter")
		@TimeLimiter(name="items"
	   Ahora todo los fallos ya sea por TimeOut o por fallo de la aplicación lo recogera y sera enviado al metodoAlternativoTimeLimiter() pero con la diferencia
	   que está vez si entrará en corto circuito el endpoint, esto quiere decir que si el umbral supera es igual o superior en llamadas lentas
	   o en fallas 3/6 una vez finalizada las 6 peticiones totales ejecutará siempre el metodoAlternativoTimeLimiter() y hasta que no pase
	   los 20 segundos configurado en el fichero de propiedades no empezará de nuevo el circuito.
	   */

	@CircuitBreaker(name="items", fallbackMethod = "metodoAlternativoTimeLimiter")
	@TimeLimiter(name="items")
	@GetMapping(value = "/ver4/{id}/cantidad/{cantidad}")
	public CompletableFuture<Item> detalle4(@PathVariable Long id, @PathVariable Integer cantidad) {
		LOGGER.info("Ejecutando el metodo anotado con @TimeLimiter");
		/* Envolvemos la llamda en una representación futura asíncrona para calcular el tiempo de espera, si tenemos un método alternativo al fallo también tiene que implementar
			CompletableFuture<Item>
		*/
		return CompletableFuture.supplyAsync(() -> itemService.findById(id, cantidad));
	}


	public CompletableFuture<Item> metodoAlternativoTimeLimiter(Long id,Integer cantidad, Throwable e) {
		LOGGER.info("Utilizando el método Alternativo con la anotación @TimeLimiter");
		// Hacemos algo alternativo al método que ha fallado 'public Item detalle(..)'
		Producto producto = new Producto();
		Item item = new Item();
		
		item.setCantidad(cantidad);
		producto.setId(id);
		producto.setNombre("Método Alternativo anotado con @TimeLimiter");
		producto.setPrecio(500.00);
		producto.setCreateAt(new Date());
		producto.setPuerto(0);
		item.setProducto(producto);
		LOGGER.info("Devolviendo el método alternativo para evitar una falla en nuestro microservicio");
		return CompletableFuture.supplyAsync(() -> item);
	}
	

	@GetMapping(value ="/obtener-config")
	public ResponseEntity<?> obtenerConfiguracion(@Value("${server.port}") String puerto) {
		Map<String,String> json = new HashMap<>();
		json.put("puerto", puerto);
		json.put("text", texto);
		json.put("Ambiente de Ejecucion", entornoEjecucion);
		return new ResponseEntity<Map<String,String>>(json,HttpStatus.OK);
	}
		
	@PostMapping(value = "/crearItem")
	@ResponseStatus(value = HttpStatus.CREATED)
	public Producto crear(@RequestBody Producto producto) {
		return itemService.guardarItem(producto);
	}
	
	@PutMapping(value = "/editarItem/{id}")
	@ResponseStatus(value = HttpStatus.CREATED)
	public Producto editar(@RequestBody Producto producto, @PathVariable("id") Long id) {
		return itemService.actualizarItem(producto, id);
	}
	
	@DeleteMapping(value = "/eliminarItem/{id}")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void eliminar(@PathVariable("id") Long id) {
		itemService.eliminarItem(id);
	}
	
	
}
