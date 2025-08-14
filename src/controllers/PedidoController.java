package controllers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import models.Cliente;
import models.Pedido;
import repositories.PedidoRepository;
import utils.JPAUtil;
public class PedidoController {

    private final PedidoRepository repo = new PedidoRepository();

    public List<Pedido> obtenerTodos() {
        return repo.findAllWithClienteAndDetalles();
    }

    public void crearPedido(Pedido pedido) {
        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("No se puede crear un pedido sin platillos.");
        }
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(pedido); // cascade en Pedido → persiste detalles
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally { em.close(); }
    }

    public void actualizarPedido(Pedido pedido) {
        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("No se puede actualizar un pedido sin platillos.");
        }
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(pedido); // orphanRemoval actualiza detalles
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        } finally { em.close(); }
    }

    public List<Pedido> obtenerPedidosPorCliente(Cliente cliente) {
        // si necesitas por cliente con JOIN FETCH, duplica la query del repo y agrega WHERE
        return obtenerTodos().stream()
                .filter(p -> p.getCliente() != null && p.getCliente().getId().equals(cliente.getId()))
                .toList();
    }

    public boolean eliminarPedidos(List<Pedido> pedidos) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            for (Pedido p : pedidos) {
                Pedido ref = em.find(Pedido.class, p.getId());
                if (ref != null) em.remove(ref); // orphanRemoval elimina detalles
            }
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        } finally { em.close(); }
    }
}