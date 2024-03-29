package com.formacionbdi.springboot.app.productos.models.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.formacionbdi.springboot.app.productos.models.dao.IProductoDao;
import com.formacionbdi.springboot.app.shared.library.models.entity.Producto;
import com.formacionbdi.springboot.app.productos.models.service.IProductoService;

@Service
public class ProductoServiceImpl implements IProductoService{

	@Autowired
	private IProductoDao productoDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Producto> findAll() {
		return (List<Producto>) productoDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Producto fingById(Long id) {
		return productoDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public Producto guardarProducto(Producto producto) {
		return productoDao.save(producto);
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		productoDao.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Producto> paginador(Pageable pageable) {
		return productoDao.findAll(pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Producto findByFoto(String nombreFoto) {
		return productoDao.findByFoto(nombreFoto);
	}

  @Override
  @Transactional(readOnly = true)
  public List<Producto> findByNombreContainingIgnoreCase(String nombre) {
    return productoDao.findByNombreContainingIgnoreCase(nombre);
  }

}
