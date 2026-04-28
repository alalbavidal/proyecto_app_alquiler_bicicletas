package com.alquiler.alquilerbicicletas.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando se intenta reservar un accesorio que ya está reservado
 * para el período seleccionado y no hay suficiente stock disponible.
 */

@ResponseStatus(HttpStatus.BAD_REQUEST) // Usamos BAD_REQUEST ya que es un error del cliente
public class AccesorioYaReservadoException extends RuntimeException {

    public AccesorioYaReservadoException(String mensaje) {

        super(mensaje);
    }

}