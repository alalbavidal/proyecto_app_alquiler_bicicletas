package com.alquiler.alquilerbicicletas.configuracion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class StaticResourcesConfig implements WebMvcConfigurer {

    @Value("${app.uploads.documentos.path}") private String docsPath;
    @Value("${app.uploads.bicicletas.path}") private String bikesPath;
    @Value("${app.contratos.path}") private String contratosPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {



        registry.addResourceHandler("/documentos/**")
                .addResourceLocations("file:" + Paths.get(docsPath).toAbsolutePath().toString() + "/");

        registry.addResourceHandler("/imagenes/bicicletas/**")
                .addResourceLocations("file:" + Paths.get(bikesPath).toAbsolutePath().toString() + "/");

        String contratos = Paths.get(contratosPath).toAbsolutePath().toUri().toString();
        if (!contratos.endsWith("/")) contratos += "/";
        registry.addResourceHandler("/contratos/**")
                .addResourceLocations(contratos);
    }
}

