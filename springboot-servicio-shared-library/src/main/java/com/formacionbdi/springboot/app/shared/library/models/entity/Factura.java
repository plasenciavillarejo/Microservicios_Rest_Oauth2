package com.formacionbdi.springboot.app.shared.library.models.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "facturas")
public class Factura implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  private String descripcion;
  
  private String observacion;
  
  @Column(name ="create_at")
  @Temporal(TemporalType.DATE)
  private Date createAte;
  
  // One -> Factura
  // Many -> ItemFactura
  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "factura_id") // Este campo se crea en ItemFactura
  // Excluimos los atributos que se genera con el LAZY, si no lo ignoramos nos va generar un error ya que son propios de hibernate del objeto proxy
  // De está forma solo tenemos los atributos propios de la clase REGION
  @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
  private List<ItemFactura> itemsFactura;
  
  public Factura() {
    itemsFactura = new ArrayList<>();
  }
  
  @PrePersist
  public void prePersit() {
    this.createAte = new Date();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getObservacion() {
    return observacion;
  }

  public void setObservacion(String observacion) {
    this.observacion = observacion;
  }

  public Date getCreateAte() {
    return createAte;
  }

  public void setCreateAte(Date createAte) {
    this.createAte = createAte;
  }
  
  public List<ItemFactura> getItemsFactura() {
    return itemsFactura;
  }

  public void setItemsFactura(List<ItemFactura> itemsFactura) {
    this.itemsFactura = itemsFactura;
  }
  
  public Double getTotal() {
    /* Double total  = 0.00;
    itemsFactura.forEach(item -> total += item.calcularImporte());
    return total;*/
    return itemsFactura.stream()
        .map(ItemFactura::calcularImporte)
        .reduce(0.00, Double::sum);
  }
  
  private static final long serialVersionUID = 9202200621690987752L;
  
}
