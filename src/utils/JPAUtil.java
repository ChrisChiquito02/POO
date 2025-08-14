package utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {
    private static final String PU_NAME = "DeliciasPU";
    private static EntityManagerFactory emf;

    /** Llamar al inicio (p.ej., en App.start) */
    public static synchronized void ensureReady() {
        if (emf == null) {
            try {
                emf = Persistence.createEntityManagerFactory(PU_NAME);
                // prueba real de conexión
                try (EntityManager em = emf.createEntityManager()) {
                    em.getTransaction().begin();
                    em.createNativeQuery("SELECT 1").getSingleResult();
                    em.getTransaction().commit();
                }
                System.out.println("✅ Conexión a la base de datos establecida correctamente.");
            } catch (Throwable ex) {
                System.err.println("❌ No se pudo inicializar JPA (" + PU_NAME + "): " + ex);
                throw new RuntimeException(ex);
            }
        }
    }

    /** Compatibilidad con código antiguo */
    public static EntityManagerFactory getEntityManagerFactory() {
        ensureReady();
        return emf;
    }

    public static EntityManager getEntityManager() {
        ensureReady();
        return emf.createEntityManager();
    }

    public static synchronized void close() {
        if (emf != null) {
            try { emf.close(); } catch (Exception ignore) {}
            emf = null;
        }
    }
}
