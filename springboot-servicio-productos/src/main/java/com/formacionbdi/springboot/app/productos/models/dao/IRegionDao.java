package com.formacionbdi.springboot.app.productos.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.formacionbdi.springboot.app.shared.library.models.entity.Region;

public interface IRegionDao extends JpaRepository<Region, Long> {

	@Query(value = "from Region")
	public List<Region> buscarListaRegiones();
}
