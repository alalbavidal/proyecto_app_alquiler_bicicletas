package com.alquiler.alquilerbicicletas.configuracion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/static/**")
//                .addResourceLocations("classpath:/static/");
//    }

    @Value("${app.uploads.documentos.path}") private String docsPath;
    @Value("${app.uploads.bicicletas.path}") private String bikesPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/documentos/**")
                .addResourceLocations("file:" + docsPath + "/");

        registry.addResourceHandler("/imagenes/bicicletas/**")
                .addResourceLocations("file:" + bikesPath + "/");
    }





}
