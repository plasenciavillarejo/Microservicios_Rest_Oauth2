package com.formacionbdi.springboot.app.shared.library;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//Este Proyecto no se tiene que levantar ya que solo nos sirve para acceder a el y obtener la Entidades desde cualquier proyecto

@SpringBootApplication
/* Vamos a excluir la configuración obligatoria de implementar un motor e BBDD, este proyecto es de librerías y no necesita de BBDD.
	exclude = {DataSourceAutoConfiguration.class} -> Deshabilita la autoconfiguración
*/
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class SpringbootServicioSharedLibraryApplication {

	/* Como es un proyecto para contener la librerías centralizadas debe comentarse el main
	public static void main(String[] args) {
		SpringApplication.run(SpringbootServicioSharedLibraryApplication.class, args);
	}
	*/
}
