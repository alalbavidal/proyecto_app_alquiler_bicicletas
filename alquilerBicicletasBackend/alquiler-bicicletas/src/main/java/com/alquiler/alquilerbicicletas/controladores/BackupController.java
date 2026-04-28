package com.alquiler.alquilerbicicletas.controladores;

import com.alquiler.alquilerbicicletas.servicios.BackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;
import java.util.Comparator;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/backups")
@RequiredArgsConstructor
public class BackupController {

    private final BackupService backupService;

    // DTO simple para listar backups
    public static record BackupItem(String name, long size, long lastModified) {}

    @PostMapping
    public ResponseEntity<?> triggerBackup() {
        try {
            Map<String, Object> res = backupService.runBackupNow();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            log.error("Error ejecutando backup manual", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<BackupItem>> listBackups(@Value("${app.backup.dir}") String dir) {
        try (Stream<Path> stream = Files.list(Path.of(dir))) {
            List<BackupItem> files = stream
                    .filter(Files::isRegularFile)
                    .sorted(Comparator.comparingLong((Path p) -> p.toFile().lastModified()).reversed())
                    .map(p -> new BackupItem(
                            p.getFileName().toString(),
                            p.toFile().length(),
                            p.toFile().lastModified()
                    ))
                    .toList();

            return ResponseEntity.ok(files);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<?> downloadBackup(@PathVariable String fileName,
                                            @Value("${app.backup.dir}") String dir) {
        try {
            Path p = Path.of(dir, fileName).normalize();
            if (!Files.exists(p) || !Files.isRegularFile(p)) {
                return ResponseEntity.notFound().build();
            }
            FileSystemResource res = new FileSystemResource(p.toFile());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + p.getFileName() + "\"")
                    .body(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
