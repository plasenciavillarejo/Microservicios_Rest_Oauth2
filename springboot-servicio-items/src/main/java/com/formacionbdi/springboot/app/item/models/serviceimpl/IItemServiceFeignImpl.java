package com.formacionbdi.springboot.app.item.models.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.formacionbdi.springboot.app.item.clientes.ProductoClientesRest;
import com.formacionbdi.springboot.app.item.models.Item;
import com.formacionbdi.springboot.app.shared.library.models.entity.Producto;
import com.formacionbdi.springboot.app.item.models.service.IItemService;

@Service("serviceFeign")
public class IItemServiceFeignImpl implements IItemService {

	@Autowired
	private ProductoClientesRest clienteFeign;
	
	@Override
	public List<Item> findAll() {
		return clienteFeign.listar().stream().map(p -> new Item(p,1)).collect(Collectors.toList());
	}

	@Override
	public Item findById(Long id, Integer cantidad) {
		return new Item(clienteFeign.detalleProducto(id), cantidad);
	}

	@Override
	public Producto guardarItem(Producto producto) {
		return clienteFeign.crearProductoFeign(producto);
	}

	@Override
	public Producto actualizarItem(Producto producto, Long id) {
		return clienteFeign.actualizarProductoFeign(producto, id);
	}

	@Override
	public void eliminarItem(Long id) {
		clienteFeign.eliminarProductoFeign(id);
	}

}
