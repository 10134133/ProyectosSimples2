package org.sebastian;
import javax.persistence.*;



public class DBService<T> {

    private static EntityManagerFactory e;
    private Class<T> clase;

    public DBService(Class<T> clase){
        if(e == null){
            e = Persistence.createEntityManagerFactory("MiUnidadPersistencia");
            this.clase = clase;
        }
    }

    public EntityManager getEntityManager(){
        return e.createEntityManager();
    }

    public T create(T entity) throws IllegalArgumentException, PersistenceException{
        EntityManager em = getEntityManager();
        try{
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        }finally {
            em.close();
        }
        return entity;
    }

    public T edit(T entity) throws PersistenceException{
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        try{
            em.merge(entity);
            em.getTransaction().commit();
        }finally {
            em.close();
        }
        return entity;
    }
    public T find(Object id) throws PersistenceException{
        EntityManager em = getEntityManager();

        try {
            return em.find(clase,id);
        }finally {
            em.close();
        }

    }



}

