package com.formacionbdi.springboot.app.productos.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.formacionbdi.springboot.app.shared.library.models.entity.Producto;

public interface IProductoDao extends PagingAndSortingRepository<Producto, Long>{

	public Producto findByFoto(String nombreFoto);
	
	//@Query(value = "select p from Producto p where p.nombre like %?1%")
	public List<Producto> findByNombreContainingIgnoreCase(String nombre);
}
