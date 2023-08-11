package com.formacionbdi.springboot.app.item.clientes;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.formacionbdi.springboot.app.shared.library.models.entity.Producto;

/* 1.- Con la anotacion @FeignClient() indicamos que este cliente de un microservicio
	Con la propiedad name="" -> Le indicamos que se conecte al servicio configurado en el fichero properties anotado con:
		spring.application.name = servicio-productos seguido de la url
*/

/* 2.- Ahora para utilizar Ribbon devemos desaclopar url="" para utilizar el properties. De está forma
  podremos utilizar el balanceador de carga.
*/
// ,url = "localhost:8001"
@FeignClient(name = "servicio-productos") 
public interface ProductoClientesRest {
	
	/* Para obtener la lista utilizamos el endpoint que consume el servicio '@Get/Post/Put' 
	  	Nota: 
	  		Debe ser el mismo Endpoint que el controlador al que nos queremos conectar:
	  		Proyecto -> 'springboot-servicio-productos' / ProductoController.java / @GetMapping("/listar")
	 */
	@GetMapping("/listar")
	public List<Producto> listar();
	
	@GetMapping("/ver/{id}")
	public Producto detalleProducto(@PathVariable Long id);
	
	@PostMapping("/crear")
	public Producto crearProductoFeign(@RequestBody Producto producto);
	
	@PutMapping("/editar/{id}")
	public Producto actualizarProductoFeign(@RequestBody Producto producto, @PathVariable Long id);
	
	@DeleteMapping("/eliminar/{id}")
	public void eliminarProductoFeign(@PathVariable Long id);
	
}
