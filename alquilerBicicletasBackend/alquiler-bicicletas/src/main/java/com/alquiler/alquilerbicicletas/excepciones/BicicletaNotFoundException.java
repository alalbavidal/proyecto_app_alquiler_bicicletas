package com.alquiler.alquilerbicicletas.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BicicletaNotFoundException extends RuntimeException {

    public BicicletaNotFoundException(String message) {
        super(message);
    }

    public BicicletaNotFoundException(Long id) {
        super("No se encontró la bicicleta con ID: " + id);
    }



}