package org.sebastian.Entidades;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Comentario implements Serializable {
    @Id
    @GeneratedValue
    private int id;

    private String comentario;
    private int productoId;
    boolean estado;

    public Comentario(String comentario, int productoId) {
        this.comentario = comentario;
        this.productoId = productoId;
        estado = true;
    }

    public Comentario() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }

    public boolean isEstado() {
        return estado;
    }
    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public int getProductoId() {
        return productoId;
    }


    public void setEstado(boolean estado) {
        this.estado = estado;
    }

}
