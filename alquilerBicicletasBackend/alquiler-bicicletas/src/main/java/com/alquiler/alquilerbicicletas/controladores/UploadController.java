package com.alquiler.alquilerbicicletas.controladores;

import com.alquiler.alquilerbicicletas.servicios.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @PostMapping(value = "/{bucket}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> upload(
            @PathVariable String bucket,
            @RequestParam("file") MultipartFile file
    ) {
        var res = uploadService.store(file, bucket); // valida bucket y tipo
        return ResponseEntity.ok(Map.of(
                "url", res.url(),
                "filename", res.filename()
        ));
    }
}
