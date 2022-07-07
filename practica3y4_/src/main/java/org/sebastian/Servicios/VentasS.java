package org.sebastian.Servicios;
import org.sebastian.DBService;
import org.sebastian.Entidades.VentasProductos;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class VentasS extends DBService<VentasProductos> {

    private static VentasS instance;

    private VentasS(){ super(VentasProductos.class);}

    public static VentasS getInstance(){
        if(instance == null){
            instance = new VentasS();
        }
        return instance;
    }

    public List<VentasProductos> getVentas(){
        EntityManager em = getEntityManager();
        Query query = em.createNativeQuery("select * from VENTASPRODUCTOS ", VentasProductos.class);
        List<VentasProductos> lista = query.getResultList();
        return lista;
    }
}
