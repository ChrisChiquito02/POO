package models;

import jakarta.persistence.*;

@Entity
@Table(name = "pedido_detalle")
public class PedidoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // FK a pedidos(id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    // FK a platillos(id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platillo_id", nullable = false)
    private Platillo platillo;

    @Column(name = "cantidad", nullable = false)
    private int cantidad;

    // ----- helpers -----
    @Transient
    public double getSubtotal() {
        return (platillo != null ? platillo.getPrecio() : 0.0) * cantidad;
    }

    // ----- getters/setters -----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public Platillo getPlatillo() { return platillo; }
    public void setPlatillo(Platillo platillo) { this.platillo = platillo; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
