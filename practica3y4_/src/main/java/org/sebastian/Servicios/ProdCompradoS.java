package org.sebastian.Servicios;
import org.sebastian.DBService;
import org.sebastian.Entidades.ProdComprado;
import org.sebastian.Entidades.Producto;
import java.util.ArrayList;
import java.util.List;

public class ProdCompradoS extends DBService<ProdComprado> {
    private static ProdCompradoS instance;

    private ProdCompradoS(){ super(ProdComprado.class);}

    public static ProdCompradoS getInstance(){
        if(instance == null){
            instance = new ProdCompradoS();
        }
        return instance;
    }

    public List<ProdComprado> convertProd(List<Producto> productos, long venta){
        List<ProdComprado> list = new ArrayList<ProdComprado>();
        for (Producto prod:productos) {
            ProdComprado temp = new ProdComprado(prod.getId(),venta,prod.getCantidad(),prod.getPrecio(),prod.getNombre());
            getInstance().create(temp);
            list.add(temp);
        }
        return list;
    }
}
