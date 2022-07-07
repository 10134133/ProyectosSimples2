package org.sebastian.Entidades;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Usuario implements Serializable {
    @Id
    private String usuario;
    private String password;

    public Usuario() {
        usuario ="";
        password = "";
    }
    public Usuario(String user, String pass) {
        usuario = user;
        password = pass;
    }

    public String getUsuario() {
        return usuario;
    }
    public String getPassword() {
        return password;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
