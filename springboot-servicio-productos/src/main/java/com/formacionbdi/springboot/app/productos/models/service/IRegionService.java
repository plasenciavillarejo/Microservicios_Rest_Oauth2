package com.formacionbdi.springboot.app.productos.models.service;

import java.util.List;

import com.formacionbdi.springboot.app.shared.library.models.entity.Region;

public interface IRegionService {

	public List<Region> buscarListaRegiones();
	
}
