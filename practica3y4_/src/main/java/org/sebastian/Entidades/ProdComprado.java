package org.sebastian.Entidades;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class ProdComprado implements Serializable {
    @Id
    private int productId;
    @Id
    private long ventaID;
    private int cantidad;
    private int precio;
    private String nombre;

    public ProdComprado(int productId, long ventaID, int cantidad, int precio, String nombre) {
        this.productId = productId;
        this.ventaID = ventaID;
        this.cantidad = cantidad;
        this.precio = precio;
        this.nombre = nombre;
    }

    public ProdComprado() {

    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public long getVentaID() {
        return ventaID;
    }

    public void setVentaID(long ventaID) {
        this.ventaID = ventaID;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int total(){
        return cantidad * precio;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public String getNombre() { return nombre; }

}
