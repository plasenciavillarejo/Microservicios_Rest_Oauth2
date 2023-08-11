package com.formacionbdi.springboot.app.item.models.service;

import java.util.List;

import com.formacionbdi.springboot.app.item.models.Item;
import com.formacionbdi.springboot.app.shared.library.models.entity.Producto;

public interface IItemService {

	public List<Item> findAll();
	
	public Item findById(Long id, Integer cantidad);
	
	public Producto guardarItem(Producto producto);
	
	public Producto actualizarItem(Producto producto, Long id);
	
	public void eliminarItem(Long id);
}
