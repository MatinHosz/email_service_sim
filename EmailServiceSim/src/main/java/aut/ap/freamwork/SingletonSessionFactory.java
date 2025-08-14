package aut.ap.freamwork;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class SingletonSessionFactory {
    private static SessionFactory sessionFactory;

    private SingletonSessionFactory() {}

    public static SessionFactory getSessionFactory() {
        if (sessionFactory != null)
            return sessionFactory;
        
            sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory();

            return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory == null)
            return;
        
        sessionFactory.close();
    }
}
