package com.formacionbdi.springboot.app.shared.usuarios.models.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.formacionbdi.springboot.app.usuarios.commons.models.entity.Usuario;


/* Automatizamos la validacion de la seguridad de los usuarios con spring security utilizando:
 
Utilizando la api @RepositoryRestResource -> Podemos crear de forma automática el listar, crear, editar y borrar un usuario sin necesidad de implementar nada de código.
	1.- Se configura en gateway/zuul para acceder al endpoint /api/usuarios/**:
		1.1.- Desde Gateway en Postman: http://localhost:8090/api/usuarios/usuarios
		1.2.- Desde la api de usuarios en postman: http://localhost:61722/usuarios -> (61722 es un puerto dínamico, deberos de ver que puerto es cada vez que levantemos la aplicación)
		
	2.- Internamente cuando nos devuelve la información mediante Postman, nos da mucho más metodo para poder consultar de forma automática como por ejemplo:
		2.1.- Detalle de el usuario con id=1 -> http://localhost:8090/api/usuarios/usuarios/1 (Tantos usuarios como disponga la aplicación)
		2.2.- Perfil de el usuario -> http://localhost:8090/api/usuarios/profile/usuarios
	
	3.- Si quiero crear un usuario me basta con indicar el mismo endpoint en POST -> http://localhost:8090/api/usuarios/usuarios
		3.1.- Copia el Json devuelto a al listar un usuario específico y le borro los links: http://localhost:8090/api/usuarios/usuarios/1
		3.2.- En los roles le indicao el id  de cada rol que va a estar asociado a el y listo, al mandar la petición ya tendé creado un nuevo usuario con los roles que yo deseo.
	
	4.- Si quiero editar un usuario ya creado indicando el endpoint PUT y cambiaro lo que deseo del json me hara el update de forma correcta:
		4.1.- http://localhost:8090/api/usuarios/usuarios/4 -> Nos actualiza el usuario
	
	5.- Para elminarlo indicando el endpoint DELETE con el json correspondiente nos eliminará el usuario.
		5.1.- http://localhost:8090/api/usuarios/usuarios/4 -> Nos elimina el usuario
*/
@RepositoryRestResource(path = "usuarios")
public interface IUsuarioDao extends PagingAndSortingRepository<Usuario, Long> {

	/* Anotamos con RestResource para adaptar la ruta de busqueda desde la api en vez de el nombre de el método
	  	http://localhost:8090/api/usuarios/usuarios/search/buscar-username?username=jose
	  	Para cambiar el username podemos anotar el @Param(value="nombre") y con esto obtiene la variable de la url
	 */
	@RestResource(path = "buscar-username")
	public Usuario findByUsername(@Param(value = "username") String username);
	
}
