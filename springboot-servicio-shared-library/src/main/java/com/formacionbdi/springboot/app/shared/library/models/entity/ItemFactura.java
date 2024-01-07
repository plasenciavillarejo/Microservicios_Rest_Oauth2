package com.formacionbdi.springboot.app.shared.library.models.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "facturas_items")
public class ItemFactura implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  private Integer cantidad;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "producto_id") // Si no lo ponemos lo crea por defecto ya que es el dueño de la llave, la crea dentro de ItemFactura con el nombre producto_id
  // Excluimos los atributos que se genera con el LAZY, si no lo ignoramos nos va generar un error ya que son propios de hibernate del objeto proxy
  // De está forma solo tenemos los atributos propios de la clase REGION
  @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
  private Producto producto;
  
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getCantidad() {
    return cantidad;
  }

  public void setCantidad(Integer cantidad) {
    this.cantidad = cantidad;
  }

  /**
   * Función para calcular el total
   */
  public Double calcularImporte() {
    return cantidad.doubleValue() * producto.getPrecio();
  }
  
  public Producto getProducto() {
    return producto;
  }

  public void setProducto(Producto producto) {
    this.producto = producto;
  }




  private static final long serialVersionUID = -8305436081716943366L;

}
