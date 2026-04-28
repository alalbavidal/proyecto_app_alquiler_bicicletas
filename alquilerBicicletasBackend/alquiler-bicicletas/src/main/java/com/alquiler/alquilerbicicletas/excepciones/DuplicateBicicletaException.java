package com.alquiler.alquilerbicicletas.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateBicicletaException extends RuntimeException {

    public DuplicateBicicletaException(String message) {
        super(message);
    }

    public DuplicateBicicletaException(String campo, String valor) {
        super("Ya existe una bicicleta con " + campo + ": " + valor);
    }
}