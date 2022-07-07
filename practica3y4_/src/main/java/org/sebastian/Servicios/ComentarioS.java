package org.sebastian.Servicios;
import org.hibernate.*;
import org.sebastian.DBService;
import org.sebastian.Entidades.Comentario;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.util.List;

public class ComentarioS extends DBService<Comentario> {
    private static ComentarioS instance;

    private ComentarioS(){
        super(Comentario.class);
    }

    public static ComentarioS getInstancia(){
        if(instance==null){
            instance = new ComentarioS();
        }
        return instance;
    }

    public List<Comentario> findComments(int id) {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("select e from Comentario e where e.estado = true and e.productoId like :id");
        query.setParameter("id", id);
        List<Comentario> lista = query.getResultList();
        return lista;
    }
    public void deleteComent(int id) throws PersistenceException {
        Session session = getEntityManager().unwrap(org.hibernate.Session.class);;
        session.beginTransaction();
        Query query = session.createQuery("delete from Comentario where id =" + id);
        int row = query.executeUpdate();
        session.getTransaction().commit();
        session.close();
    }
}
