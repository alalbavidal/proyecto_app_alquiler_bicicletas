package com.alquiler.alquilerbicicletas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AlquilerBicicletasApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlquilerBicicletasApplication.class, args);
    }

}
