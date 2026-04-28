package com.alquiler.alquilerbicicletas.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // Usamos BAD_REQUEST ya que es un error del cliente
public class BicicletaYaReservadaException extends RuntimeException {

    public BicicletaYaReservadaException(String message) {
        super(message);
    }


}
