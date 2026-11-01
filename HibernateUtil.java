package util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/** Menyediakan satu SessionFactory (mahal dibuat, cukup sekali). */
public final class HibernateUtil {

    private static SessionFactory sessionFactory;

    private HibernateUtil() {
    }

    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                sessionFactory = new Configuration()
                        .configure("hibernate.cfg.xml")
                        .buildSessionFactory();
            } catch (Throwable t) {
                throw new RuntimeException("Gagal menyiapkan Hibernate: " + t.getMessage(), t);
            }
        }
        return sessionFactory;
    }

    public static synchronized void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }
}
