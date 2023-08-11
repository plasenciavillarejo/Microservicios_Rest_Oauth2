package com.formacionbdi.springboot.app.productos.models.service;

import java.util.List;

import com.formacionbdi.springboot.app.shared.library.models.entity.Producto;

public interface IProductoService {

	public List<Producto> findAll();
	
	public Producto fingById(Long id);
	
	public Producto guardarProducto(Producto producto);
	
	public void deleteById(Long id);
}
