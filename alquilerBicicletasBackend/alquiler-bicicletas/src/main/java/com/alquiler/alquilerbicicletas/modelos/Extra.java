package com.alquiler.alquilerbicicletas.modelos;

import com.alquiler.alquilerbicicletas.enumerados.AplicableExtra;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "extra", schema = "alquiler_bicicletas")
public class Extra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, precision = 10)
    private Double precio;

    @Enumerated(EnumType.STRING)
    @Column(name = "aplicable_a", nullable = false, length = 20)
    private AplicableExtra aplicableA;

    @Column(columnDefinition = "TEXT")
    private String restricciones;

    @Column(updatable = false)
    private LocalDateTime fechaCreacion;
}