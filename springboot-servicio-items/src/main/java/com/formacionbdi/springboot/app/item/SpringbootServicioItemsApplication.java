package com.formacionbdi.springboot.app.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

//Habilitamos hystrix -> Se encarga de la comunicación de los microservicios, envuelve a ribbon envuelto en eureka - Compatible con la versión de spring boot 2.3.X
//@EnableCircuitBreaker

// Habilitamos el cliente Eureka de forma esplícita, aunque basta solo con indicar la dependencía en el pom.xml
@EnableEurekaClient
// Anotamos Ribbon para poder habilitar el balanceador de carga con un solo cliente
// Tiene que indicar el mismo nombre que está anotado en @FeingClient("servicio-producto")
// Se comenta ya que se utiliza Eurek Client
//@RibbonClient(name = "servicio-productos")
// Habilitamos el Feign para realizar la inyección de los clientes en nuestros controladores
@EnableFeignClients
@SpringBootApplication
/* Como hemos creado el proyecto de shared-library y hemos movido la entidad Producto.java allí para que 
se pueda reutilizar en el proyecto de productos e items, ahora necesitamaos anotar con @EntityScan ya que 
está fuera de el contexto que maneja la aplicación de productos 
*/
@EntityScan({"com.formacionbdi.springboot.app.shared.library.models.entity"})
/* Vamos a excluir la configuración obligatoria de implementar un motor e BBDD, este proyecto es de librerías y no necesita de BBDD.
exclude = {DataSourceAutoConfiguration.class} -> Deshabilita la autoconfiguración
*/
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class SpringbootServicioItemsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootServicioItemsApplication.class, args);
	}

}
