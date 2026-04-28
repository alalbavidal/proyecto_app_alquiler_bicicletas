package com.alquiler.alquilerbicicletas.modelos;

import com.alquiler.alquilerbicicletas.enumerados.TipoDocumentoIdentidad;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cliente", schema = "alquiler_bicicletas")
public class Cliente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100) private String nombre;
    @Column(nullable = false, length = 100) private String apellido;
    @Column(name="fecha_nacimiento", nullable = false) private LocalDate fechaNacimiento;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(length = 20) private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(name="tipo_documento", nullable = false, length = 20)
    private TipoDocumentoIdentidad tipoDocumento;

    @Column(name="documento_identidad", nullable = false, length = 50)
    private String documentoIdentidad;

    @Column(name="foto_documento_url", length = 255)
    private String fotoDocumentoUrl;

    @Column(name="fecha_registro")
    private LocalDate fechaRegistro;
}