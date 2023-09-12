package com.formacionbdi.springboot.app.productos.models.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.formacionbdi.springboot.app.shared.library.models.entity.Producto;

public interface IProductoDao extends JpaRepository<Producto, Long>{

	public Producto findByFoto(String nombreFoto);
}
