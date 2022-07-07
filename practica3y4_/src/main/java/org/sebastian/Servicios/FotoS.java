package org.sebastian.Servicios;
import org.sebastian.DBService;
import org.sebastian.Entidades.Foto;

public class FotoS extends DBService<Foto> {

    private static FotoS instance;

    private FotoS(){
        super(Foto.class);
    }

    public static FotoS getInstancia(){
        if(instance==null){
            instance = new FotoS();
        }
        return instance;
    }
}

