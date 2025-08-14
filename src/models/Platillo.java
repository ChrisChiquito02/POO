package models;

import jakarta.persistence.*;

@Entity
@Table(name = "platillos", uniqueConstraints = @UniqueConstraint(columnNames = "nombre"))
public class Platillo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "precio", nullable = false)
    private double precio;

    public Platillo() {}
    public Platillo(String nombre, double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
}
