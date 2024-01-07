package com.formacionbdi.springboot.app.productos.models.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.formacionbdi.springboot.app.shared.library.models.entity.Factura;

public interface IFacturaDao extends JpaRepository<Factura, Long> {
 
}
