package org.sebastian.Servicios;
import org.sebastian.DBService;
import org.sebastian.Entidades.Producto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.util.List;

public class ProductoS extends DBService<Producto> {
    private static ProductoS instance;

    private ProductoS(){
        super(Producto.class);
    }

    public static ProductoS getInstance(){
        if(instance == null){
            instance = new ProductoS();
        }
        return instance;
    }

    public void deleteProducto(Object id){
        Producto entity = find(id);
        entity.setEstado(false);
        entity = edit(entity);
    }


    public List<Producto> findProd(int ini, int fin) throws PersistenceException {
        EntityManager em = getEntityManager();
        Query query = em.createNativeQuery("select * from PRODUCTO WHERE ESTADO = true ", Producto.class);
        query.setFirstResult(ini);
        if(fin != 0) {
            query.setMaxResults(fin);
        }
        List<Producto> lista = query.getResultList();
        return lista;    }

    public int pag() {
        int pageSize = 10;
        EntityManager em = getEntityManager();
        Query query = em.createNativeQuery("select * from PRODUCTO WHERE ESTADO = true ", Producto.class);
        int countResults = query.getResultList().size();
        int lastPageNumber = (int) (Math.ceil(countResults / pageSize));
        System.out.println(countResults);
        return  lastPageNumber;
    }
}
