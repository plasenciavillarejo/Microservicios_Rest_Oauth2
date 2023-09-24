package com.formacionbdi.springboot.app.shared.library.models.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "regiones")
public class Region implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nombre;

	/* En caso de quere hacer una bidireccinales se debe de indicar
	 
	@OneToMany(mappedBy = "region", fetch = FetchType.LAZY)
	private List<Producto> productos;		
	*/

	private static final long serialVersionUID = -7612071335641771428L;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

}
