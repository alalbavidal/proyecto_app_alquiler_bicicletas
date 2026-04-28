package com.alquiler.alquilerbicicletas.dto;

import com.alquiler.alquilerbicicletas.enumerados.TipoDocumentoIdentidad;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private String email;
    private String telefono;
    private TipoDocumentoIdentidad tipoDocumento;  // ← NUEVO
    private String documentoIdentidad;
    private String fotoDocumentoUrl; // Opcional: solo si se sube desde el frontend
}
