package utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import models.Cliente;
import models.Platillo;
import models.Pedido;
import models.PedidoDetalle;

import java.time.LocalDateTime;
import java.util.List;

public class Seeder {
    public static void seed() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // Only seed if there are no clients yet
            Long countClientes = em.createQuery("select count(c) from Cliente c", Long.class).getSingleResult();
            if (countClientes == 0) {
                // Clients
                Cliente c1 = new Cliente(null, "Ariadna Sol", "ari.sol@correo.com");
                Cliente c2 = new Cliente(null, "Matías Quintero", "mat.q@correo.com");
                Cliente c3 = new Cliente(null, "Luna Zaragoza", "luna.z@correo.com");
                em.persist(c1); em.persist(c2); em.persist(c3);

                // Platillos (totally new)
                Platillo p1 = new Platillo(null, "Ramen de miso", 129.50);
                Platillo p2 = new Platillo(null, "Tacos de portobello", 89.00);
                Platillo p3 = new Platillo(null, "Ensalada mediterránea", 102.75);
                Platillo p4 = new Platillo(null, "Panini de pesto", 95.20);
                em.persist(p1); em.persist(p2); em.persist(p3); em.persist(p4);

                // One order
                Pedido ped = new Pedido();
                ped.setCliente(c1);
                ped.setFecha(LocalDateTime.now());
                em.persist(ped);

                PedidoDetalle d1 = new PedidoDetalle();
                d1.setPedido(ped);
                d1.setPlatillo(p1);
                d1.setCantidad(2);
                em.persist(d1);

                PedidoDetalle d2 = new PedidoDetalle();
                d2.setPedido(ped);
                d2.setPlatillo(p3);
                d2.setCantidad(1);
                em.persist(d2);
            }

            em.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }
}