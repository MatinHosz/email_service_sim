package aut.ap.freamwork;

import java.util.List;
import java.util.function.Function;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.RootGraph;

import jakarta.persistence.EntityGraph;

public abstract class ServiceBase<T extends EServiceEntity> {
    private Class<T> classw;

    protected ServiceBase(Class<T> classw) {
        this.classw = classw;
    }

    public void persist(T entity) {
        getSessionFactory().inTransaction(session -> { 
        session.persist(entity);
        });
    }

    public void remove(int id) {
        getSessionFactory().inTransaction(session -> {
            session.remove(session.getReference(classw, id));
        });
    }

    public void remove(T entity) {
        getSessionFactory().inTransaction(session -> {
            session.remove(entity);
        });
    }

    public T fetchById(int id) {
        return getSessionFactory().fromTransaction(session -> session.get(classw, id));
    }

    public T fetchById(int id, Function<Session, RootGraph<T>> graphCreator) {
        return getSessionFactory().fromTransaction(session -> {
            RootGraph<T> graph = graphCreator.apply(session);
            
            return session.byId(classw).withLoadGraph(graph).load(id);
        });
    } 

    public List<T> fetchAll() {
        return getSessionFactory().fromTransaction(session -> {
            return session.createSelectionQuery("from " + classw.getSimpleName(), classw)
            .getResultList();
        });
    }

    public List<T> fetchAll(Function<Session, RootGraph<T>> graphCreator) {
        return getSessionFactory().fromTransaction(session -> {
            EntityGraph<T> graph = graphCreator.apply(session);

            return session.createSelectionQuery("from" + classw.getSimpleName(), classw)
            .setEntityGraph(graph, GraphSemantic.LOAD)
            .getResultList();
        });
    }

    public T fetchRefById(int id) {
        return getSessionFactory().fromTransaction(session -> session.getReference(classw, id));
    } 

    protected SessionFactory getSessionFactory() {
        return SingletonSessionFactory.getSessionFactory();
    }
}