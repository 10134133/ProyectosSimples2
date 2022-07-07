package org.sebastian.Servicios;
import org.sebastian.DBService;
import org.sebastian.Entidades.Usuario;

public class UsuarioS extends DBService<Usuario>  {

    private static UsuarioS instance;

    private UsuarioS(){
        super(Usuario.class);
    }

    public static UsuarioS getInstancia(){
        if(instance==null){
            instance = new UsuarioS();
        }
        return instance;
    }

    public static String autentificarUsuario(Usuario aux) {
        return "ADM";
    }
}
