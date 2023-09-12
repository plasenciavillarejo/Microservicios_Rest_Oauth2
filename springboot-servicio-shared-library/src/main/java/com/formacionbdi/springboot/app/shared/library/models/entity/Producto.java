package com.formacionbdi.springboot.app.shared.library.models.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name ="productos")
public class Producto implements Serializable {

	private static final long serialVersionUID = 1285454306356845809L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String nombre;
	
	
	private Double precio;
	
	@NotNull
	@Min(1)
	@Max(999999)
	@Transient // Indicamos que no es persistente, no está mapeado a ningún campo en BBDD
	private Integer puerto;
	
	@NotNull(message = "La fecha no puede ser nula")
	@Column(name = "create_at")
	@Temporal(TemporalType.DATE)
	private Date createAt;	
	
	private String foto; 
	
	/* Antes de que se inserte en la base de datos se guardara antes de hacer la persistencia en base de datos
	@PrePersist
	public void asignacionFechaAutomatica() {
		createAt = new Date();
	}
	*/
	
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
	public Double getPrecio() {
		return precio;
	}
	public void setPrecio(Double precio) {
		this.precio = precio;
	}
	public Date getCreateAt() {
		return createAt;
	}
	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	
	public Integer getPuerto() {
		return puerto;
	}
	public void setPuerto(Integer puerto) {
		this.puerto = puerto;
	}
	public String getFoto() {
		return foto;
	}
	public void setFoto(String foto) {
		this.foto = foto;
	}
	
	
	
}
