package com.alquiler.alquilerbicicletas.servicios;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class UploadServiceImpl implements UploadService {

    @Value("${app.uploads.documentos.path}")  private String docsPath;
    @Value("${app.uploads.documentos.url-base}") private String docsUrlBase;

    @Value("${app.uploads.bicicletas.path}")  private String bikesPath;
    @Value("${app.uploads.bicicletas.url-base}") private String bikesUrlBase;

    private static final long MAX_BYTES = 5L * 1024 * 1024; // 5MB
    private static final Set<String> ALLOWED_IMAGE_CT =
            Set.of("image/jpeg", "image/png", "image/webp");

    @Override
    public UploadResult store(MultipartFile file, String bucket, @Nullable String filenamePrefix) {
        validate(file, bucket);

        String original = StringUtils.cleanPath(Objects.requireNonNullElse(file.getOriginalFilename(), "file"));
        String ext = extractExtensionOrDefault(original, ".jpg");
        String name = (filenamePrefix != null ? filenamePrefix : UUID.randomUUID().toString()) + ext;

        Path dir = getDir(bucket);
        String urlBase = getUrlBase(bucket);

        try {
            Files.createDirectories(dir);
            Path target = dir.resolve(name).normalize();
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
            return new UploadResult(urlBase + "/" + name, name);
        } catch (IOException e) {
            log.error("Error guardando fichero en {}: {}", dir, e.getMessage(), e);
            throw new RuntimeException("Error guardando fichero", e);
        }
    }

    private void validate(MultipartFile file, String bucket) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Archivo vacío.");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("Archivo demasiado grande (máx 5MB).");
        }
        // Para este proyecto: ambos buckets aceptan imágenes (DNI/NIE escaneado como imagen).
        String ct = file.getContentType();
        if (ct == null || !ALLOWED_IMAGE_CT.contains(ct)) {
            throw new IllegalArgumentException("Formato no permitido. Usa JPG, PNG o WEBP.");
        }
        if (!"documentos".equals(bucket) && !"bicicletas".equals(bucket)) {
            throw new IllegalArgumentException("Bucket no permitido: " + bucket);
        }
    }

    private static String extractExtensionOrDefault(String filename, String def) {
        int idx = filename.lastIndexOf('.');
        return (idx >= 0) ? filename.substring(idx).toLowerCase() : def;
    }

    private Path getDir(String bucket) {
        return switch (bucket) {
            case "documentos" -> Paths.get(docsPath);
            case "bicicletas" -> Paths.get(bikesPath);
            case "contratos" -> Paths.get(docsPath);
            default -> throw new IllegalArgumentException("Bucket no permitido: " + bucket);
        };
    }

    private String getUrlBase(String bucket) {
        return switch (bucket) {
            case "documentos" -> docsUrlBase;
            case "bicicletas" -> bikesUrlBase;
            default -> throw new IllegalArgumentException("Bucket no permitido: " + bucket);
        };
    }
}
