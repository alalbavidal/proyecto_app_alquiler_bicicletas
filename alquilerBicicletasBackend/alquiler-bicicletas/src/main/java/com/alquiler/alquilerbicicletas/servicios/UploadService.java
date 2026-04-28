package com.alquiler.alquilerbicicletas.servicios;

import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    UploadResult store(MultipartFile file, String bucket, @Nullable String filenamePrefix);

    // azucar sintáctica si no pasas prefijo
    default UploadResult store(MultipartFile file, String bucket) {
        return store(file, bucket, null);
    }

    // Respuesta típica: URL pública y nombre de fichero
    record UploadResult(String url, String filename) {}
}
