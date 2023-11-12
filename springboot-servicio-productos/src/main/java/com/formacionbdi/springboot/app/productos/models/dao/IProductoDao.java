package com.formacionbdi.springboot.app.productos.models.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.formacionbdi.springboot.app.shared.library.models.entity.Producto;

public interface IProductoDao extends PagingAndSortingRepository<Producto, Long>{

	public Producto findByFoto(String nombreFoto);
}
