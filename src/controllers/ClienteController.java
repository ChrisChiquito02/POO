package controllers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import models.Cliente;
import utils.JPAUtil;

import java.util.List;

public class ClienteController {

    public void crearCliente(Cliente cliente) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(cliente);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally { em.close(); }
    }

    public void actualizarCliente(Cliente cliente) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(cliente);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally { em.close(); }
    }

    public boolean eliminarClientes(List<Cliente> lista) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
                try {
            tx.begin();
            for (Cliente c : lista) {
                Cliente ref = em.find(Cliente.class, c.getId());
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

    public List<Cliente> obtenerTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Cliente> q = em.createQuery(
                "SELECT c FROM Cliente c ORDER BY c.id", Cliente.class);
            return q.getResultList();
        } finally { em.close(); }
    }

    public boolean existePorCorreo(String correo) {
        return existePorCorreo(correo, null);
    }

    public boolean existePorCorreo(String correo, Long excluirId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(c) FROM Cliente c WHERE LOWER(c.correo) = :correo";
            if (excluirId != null) {
                jpql += " AND c.id <> :id";
            }
            TypedQuery<Long> q = em.createQuery(jpql, Long.class)
                    .setParameter("correo", correo.toLowerCase());
            if (excluirId != null) {
                q.setParameter("id", excluirId);
            }
            return q.getSingleResult() > 0;
        } finally { em.close(); }
    }
}
