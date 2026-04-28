package com.alquiler.alquilerbicicletas.modelos;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "accesorio", schema = "alquiler_bicicletas")
public class Accesorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "precio_dia", nullable = false)
    private BigDecimal precioDia;

    @Column(nullable = false)
    private Integer stock; // Cantidad disponible

    @Column(length = 100)
    private String observaciones;

    @Column(nullable = false)
    private Boolean activo = true; // Para "borrado" lógico


}