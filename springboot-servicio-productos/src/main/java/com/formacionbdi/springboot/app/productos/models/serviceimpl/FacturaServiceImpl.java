package com.formacionbdi.springboot.app.productos.models.serviceimpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.formacionbdi.springboot.app.productos.models.dao.IFacturaDao;
import com.formacionbdi.springboot.app.productos.models.service.IFacturaService;
import com.formacionbdi.springboot.app.shared.library.models.entity.Factura;

@Service
public class FacturaServiceImpl implements IFacturaService {

  @Autowired
  private IFacturaDao facturaDao;
  
  @Override
  @Transactional(readOnly = true)
  public Optional<Factura> findById(Long id) {
    return facturaDao.findById(id);
  }

}
