package com.formacionbdi.springboot.app.productos;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

//Habilitamos el cliente Eureka de forma esplícita, aunque basta solo con indicar la dependencía en el pom.xml
@EnableEurekaClient
//@EnableDiscoveryClient
@SpringBootApplication
/* Como hemos creado el proyecto de shared-library y hemos movido la entidad Producto.java allí para que 
 se pueda reutilizar en el proyecto de productos e items, ahora necesitamaos anotar con @EntityScan ya que 
 está fuera de el contexto que maneja la aplicación de productos 
 */
@EntityScan({"com.formacionbdi.springboot.app.shared.library.models.entity"})
public class SpringbootServicioProductosApplication {

	public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SpringbootServicioProductosApplication.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "8001"));
        app.run(args);
	}

}
