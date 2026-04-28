package com.alquiler.alquilerbicicletas.controladores;

import com.alquiler.alquilerbicicletas.dto.BicicletaDTO;
import com.alquiler.alquilerbicicletas.dto.BicicletaDisponibleDTO;
import com.alquiler.alquilerbicicletas.enumerados.EstadoBicicleta;
import com.alquiler.alquilerbicicletas.modelos.Bicicleta;
import com.alquiler.alquilerbicicletas.servicios.BicicletaService;

import com.alquiler.alquilerbicicletas.servicios.UploadService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bicicletas")
@RequiredArgsConstructor
public class BicicletaController {

    private final BicicletaService bicicletaService;
    private final UploadService uploadService; // ⬅️ nuevo



    @GetMapping
     public ResponseEntity<List<BicicletaDTO>> getAllBicicletas() {
         Logger logger = LoggerFactory.getLogger(BicicletaController.class);
         logger.info("Fetching all bicycles");
         return ResponseEntity.ok(bicicletaService.findAllBicicletas());
     }

    @GetMapping("/{id}")
    public ResponseEntity<BicicletaDTO> getBicicletaById(@PathVariable Long id) {
        return ResponseEntity.ok(bicicletaService.findBicicletaById(id));
    }

    @PostMapping
    public ResponseEntity<BicicletaDTO> createBicicleta(@RequestBody BicicletaDTO bicicletaDTO) {
        return new ResponseEntity<>(bicicletaService.saveBicicleta(bicicletaDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BicicletaDTO> updateBicicleta(@PathVariable Long id, @RequestBody BicicletaDTO bicicletaDTO) {
        return ResponseEntity.ok(bicicletaService.updateBicicleta(id, bicicletaDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Bicicleta> deleteBicicleta(@PathVariable Long id) {
        Bicicleta bicicletaActualizada = bicicletaService.deleteBicicleta(id);
        return ResponseEntity.ok(bicicletaActualizada);
    }



    @GetMapping("/disponibles")
    public ResponseEntity<List<BicicletaDisponibleDTO>> getBicicletasDisponibles(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam("tarifaId") Long tarifaId) {

        List<BicicletaDisponibleDTO> disponibles =
                bicicletaService.findBicicletasDisponiblesPorFechaYTarifa(fecha, tarifaId);

        return ResponseEntity.ok(disponibles);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<BicicletaDTO>> getBicicletasByEstado(@PathVariable EstadoBicicleta estado) {
        return ResponseEntity.ok(bicicletaService.findByEstado(estado));
    }

    @GetMapping("/modelo/{modelo}")
    public ResponseEntity<List<BicicletaDTO>> getBicicletasByModelo(@PathVariable String modelo) {
        return ResponseEntity.ok(bicicletaService.findByModelo(modelo));
    }

    @GetMapping("/bastidor/{bastidor}")
    public ResponseEntity<BicicletaDTO> getBicicletaByBastidor(@PathVariable String bastidor) {
        return bicicletaService.findByNumeroBastidor(bastidor)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/por-numero/{numero}")
    public ResponseEntity<BicicletaDTO> getBicicletaByNumero(@PathVariable String numero) {
        return bicicletaService.findByNumero(numero)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping(value = "/{id}/imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BicicletaDTO> actualizarImagen(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        // 1) Guarda el fichero en bucket "bicicletas"
        var upload = uploadService.store(file, "bicicletas", "bike_" + id + "_");

        // 2) Persiste la URL en la entidad Bicicleta
        BicicletaDTO dto = bicicletaService.actualizarImagenUrl(id, upload.url());
        return ResponseEntity.ok(dto);
    }







}
