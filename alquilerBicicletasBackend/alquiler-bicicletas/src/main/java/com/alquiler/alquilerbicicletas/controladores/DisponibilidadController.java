package com.alquiler.alquilerbicicletas.controladores;

import com.alquiler.alquilerbicicletas.dto.DiaNoDisponibleDTO;
import com.alquiler.alquilerbicicletas.servicios.DisponibilidadService;
import com.alquiler.alquilerbicicletas.servicios.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/disponibilidad")
@CrossOrigin(origins = "*")
public class DisponibilidadController {

    @Autowired
    private DisponibilidadService disponibilidadService;

    @GetMapping("/calendario")
    public List<DiaNoDisponibleDTO> obtenerDiasNoDisponibles(
            @RequestParam int mes,
            @RequestParam int anio
    ) {
        return disponibilidadService.obtenerDiasNoDisponibles(mes, anio);
    }
}






