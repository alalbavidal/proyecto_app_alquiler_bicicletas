// src/main/java/com/alquiler/alquilerbicicletas/dto/ReservaExtraDTO.java
package com.alquiler.alquilerbicicletas.dto;

public record ReservaExtraDTO(Long id, ExtraDTO extra, Integer cantidad, Double precioTotal) {}
