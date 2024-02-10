package com.formacionbdi.springboot.app.productos.models.service;

import java.util.Optional;

import org.springframework.data.repository.query.Param;

import com.formacionbdi.springboot.app.shared.library.models.entity.Factura;

public interface IFacturaService {

  public Optional<Factura> findById(@Param("id") Long id);
  
  public Factura saveFactura(Factura factura);
  
  public void deleteFactura(Long id);
  
}
