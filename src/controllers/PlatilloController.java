package controllers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import models.Platillo;
import utils.JPAUtil;

import java.util.List;

public class PlatilloController {

    public void crearPlatillo(Platillo platillo) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(platillo);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally { em.close(); }
    }

    public void actualizarPlatillo(Platillo platillo) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(platillo);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally { em.close(); }
    }

    public boolean eliminarPlatillos(List<Platillo> lista) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
try {
            tx.begin();
            for (Platillo p : lista) {
                Platillo ref = em.find(Platillo.class, p.getId());
                if (ref != null) em.remove(ref);
            }
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally { em.close(); }
    }

    public List<Platillo> obtenerTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Platillo> q = em.createQuery(
                "SELECT p FROM Platillo p ORDER BY p.id", Platillo.class);
            return q.getResultList();
        } finally { em.close(); }
    }

    public boolean existePorNombre(String nombre) {
        return existePorNombre(nombre, null);
    }

    public boolean existePorNombre(String nombre, Long excluirId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(p) FROM Platillo p WHERE LOWER(p.nombre) = :nom";
            if (excluirId != null) {
                jpql += " AND p.id <> :id";
            }
            TypedQuery<Long> q = em.createQuery(jpql, Long.class)
                    .setParameter("nom", nombre.toLowerCase());
            if (excluirId != null) {
                q.setParameter("id", excluirId);
            }
            return q.getSingleResult() > 0;
        } finally { em.close(); }
    }
}