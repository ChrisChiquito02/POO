package repositories;

import jakarta.persistence.EntityManager;
import java.util.List;
import models.Pedido;
import utils.JPAUtil;

public class PedidoRepository {

    /** Trae pedidos con cliente y detalles + platillos para mostrar nombres en la UI */
    public List<Pedido> findAllWithClienteAndDetalles() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT DISTINCT p FROM Pedido p " +
                "JOIN FETCH p.cliente " +
                "LEFT JOIN FETCH p.detalles d " +
                "LEFT JOIN FETCH d.platillo " +
                "ORDER BY p.id DESC", Pedido.class
            ).getResultList();
        } finally { em.close(); }
    }
}
