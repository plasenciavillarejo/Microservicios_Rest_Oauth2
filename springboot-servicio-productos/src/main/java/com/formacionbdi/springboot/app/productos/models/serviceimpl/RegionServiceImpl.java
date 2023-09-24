package com.formacionbdi.springboot.app.productos.models.serviceimpl;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.formacionbdi.springboot.app.productos.models.dao.IRegionDao;
import com.formacionbdi.springboot.app.productos.models.service.IRegionService;
import com.formacionbdi.springboot.app.shared.library.models.entity.Region;

@Service
public class RegionServiceImpl implements IRegionService{

	@Autowired
	private IRegionDao regionDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Region> buscarListaRegiones() {
		return regionDao.findAll();
	}

}
