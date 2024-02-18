package com.formacionbdi.springboot.app.productos.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.formacionbdi.springboot.app.shared.library.models.entity.Producto;

public interface IProductoService {

	public List<Producto> findAll();
	
	public Producto fingById(Long id);
	
	public Producto guardarProducto(Producto producto);
	
	public void deleteById(Long id);
	
	public Page<Producto> paginador(Pageable pageable);
	
	public Producto findByFoto(String nombreFoto);
	
	public List<Producto> findByNombreContainingIgnoreCase(String nombre);
	
}
