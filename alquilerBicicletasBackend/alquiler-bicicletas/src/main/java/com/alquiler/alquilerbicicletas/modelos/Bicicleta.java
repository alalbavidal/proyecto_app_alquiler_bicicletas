package com.alquiler.alquilerbicicletas.modelos;

import com.alquiler.alquilerbicicletas.enumerados.EstadoBicicleta;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "bicicleta", schema = "alquiler_bicicletas") // → Eliminado catalog
public class Bicicleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // → Eliminado @Column(name = "id") (redundante)

    @Column(nullable = false, length = 50) // → Simplificado
    private String modelo;

    @Column(nullable = false, length = 10, unique = true) // → Añadido unique
    private String numero;

    @Column(unique = true, length = 50)
    private String bastidor;

    @Column(length = 50)
    private String candado;

    @Column(length = 20)
    private String claveCandado;

    @Column(columnDefinition = "TEXT") // → Opcional: Puedes mover esto a una migración Flyway
    private String descripcion;

    @Column(length = 300) // → Coincide con tu migración (VARCHAR(255) sería más estándar)
    private String imagenUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoBicicleta estado;

    @Column(updatable = false)
    private LocalDateTime fechaCreacion; // → Valor por defecto se define en Flyway

    @Column
    private LocalDateTime fechaBaja;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        if (this.estado == EstadoBicicleta.BAJA && this.fechaBaja == null) {
            this.fechaBaja = LocalDateTime.now();
        }
    }

    public String getNombre() {
        return modelo != null ? modelo + " (ID: " + id + ")" : "Bicicleta ID: " + id;
    }

    @Column(columnDefinition = "TEXT")
    private String observaciones;


}