package org.sebastian.Entidades;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity
public class VentasProductos implements Serializable {
    @Id
    @GeneratedValue
    private long id;
    @Temporal(TemporalType.DATE)
    private Date fechaCompra;
    private String nombreCliente;

    @OneToMany(fetch = FetchType.EAGER)
    private List<ProdComprado> listaProductos;

    public VentasProductos() {

    }

    public VentasProductos(String nombre) {
        this.nombreCliente = nombre;
        fechaCompra = new Date();
    }

    public long getId() {
        return id;
    }

    public String getFechaCompra() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY");
        String date = dateFormat.format(fechaCompra);
        return date;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public List<ProdComprado> getListaProductos() {
        return listaProductos;
    }

    public Integer getTotal(){
        Integer total = 0;
        for (ProdComprado producto : listaProductos) {
            total += producto.getPrecio()*producto.getCantidad();
        }
        return total;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setFechaCompra() {
        fechaCompra = new Date();
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public void setListaProductos(List<ProdComprado> listaProductos) {
        this.listaProductos = listaProductos;
    }

}
