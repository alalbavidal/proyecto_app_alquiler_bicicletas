package com.alquiler.alquilerbicicletas.servicios;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackupService {

    @Value("${app.backup.enabled:true}")
    private boolean enabled;

    @Value("${app.backup.cron:0 30 3 * * *}")
    private String cron; // informativo

    @Value("${app.backup.dir:./backups}")
    private String backupDir;

    @Value("${app.backup.keep-days:14}")
    private int keepDays;

    @Value("${app.backup.pgDumpPath:pg_dump}")
    private String pgDumpPath;

    // DB props de Spring
    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPass;

    // Extras (carpetas)
    @Value("${app.backup.include-contratos:true}")
    private boolean includeContratos;

    @Value("${app.backup.contratos.path:}")
    private String contratosPath;

    @Value("${app.backup.include-documentos:true}")
    private boolean includeDocs;

    @Value("${app.backup.documentos.path:}")
    private String docsPath;

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @PostConstruct
    void ensureDir() throws IOException {
        Files.createDirectories(Path.of(backupDir));
    }

    // --- PROGRAMADO ---
    @Scheduled(cron = "${app.backup.cron}")
    public void scheduledBackup() {
        if (!enabled) {
            log.info("Backups deshabilitados (app.backup.enabled=false)");
            return;
        }
        try {
            runBackupNow();
        } catch (Exception e) {
            log.error("Error en backup programado", e);
        }
    }

    // --- INVOCACIÓN MANUAL ---
    public Map<String, Object> runBackupNow() throws Exception {
        var timestamp = LocalDateTime.now().format(TS);
        var created = new ArrayList<String>();

        // 1) Dump de Postgres (solo esquema actual si está en la URL)
        String dbDumpFile = doDatabaseDump(timestamp);
        created.add(dbDumpFile);

        // 2) Comprimir contratos (si procede)
        if (includeContratos && contratosPath != null && !contratosPath.isBlank()) {
            String zip = zipFolderIfExists(contratosPath, "contratos_" + timestamp + ".zip");
            if (zip != null) created.add(zip);
        }

        // 3) Comprimir documentos subidos (si procede)
        if (includeDocs && docsPath != null && !docsPath.isBlank()) {
            String zip = zipFolderIfExists(docsPath, "documentos_" + timestamp + ".zip");
            if (zip != null) created.add(zip);
        }

        // 4) Retención
        cleanupOldBackups();

        Map<String, Object> res = new HashMap<>();
        res.put("timestamp", timestamp);
        res.put("files", created);
        res.put("keepDays", keepDays);
        return res;
    }

    // --- Dump de la BBDD con pg_dump ---
    private String doDatabaseDump(String ts) throws Exception {
        DbInfo info = parseJdbcUrl(jdbcUrl);
        String dbName = info.database();
        String host = info.host();
        int port = info.port();
        String schema = info.schema() == null ? "public" : info.schema();

        String fileName = "db_" + dbName + "_" + schema + "_" + ts + ".dump";
        Path out = Path.of(backupDir, fileName);

        List<String> cmd = new ArrayList<>();
        cmd.add(pgDumpPath);
        cmd.add("-h"); cmd.add(host);
        cmd.add("-p"); cmd.add(Integer.toString(port));
        cmd.add("-U"); cmd.add(dbUser);
        cmd.add("-F"); cmd.add("c"); // formato "custom"
        cmd.add("-b");                // blobs
        cmd.add("-v");                // verbose
        cmd.add("-n"); cmd.add(schema); // solo esquema
        cmd.add("-f"); cmd.add(out.toAbsolutePath().toString());
        cmd.add(dbName);

        ProcessBuilder pb = new ProcessBuilder(cmd);
        // Evitar password en la línea de comandos
        Map<String, String> env = pb.environment();
        env.put("PGPASSWORD", dbPass);

        pb.redirectErrorStream(true);
        Process p = pb.start();

        try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = r.readLine()) != null) {
                log.info("[pg_dump] {}", line);
            }
        }

        int exit = p.waitFor();
        if (exit != 0) {
            throw new RuntimeException("pg_dump terminó con código " + exit);
        }
        log.info("Dump generado: {}", out);
        return out.toString();
    }

    // --- Comprimir carpetas ---
    private String zipFolderIfExists(String folderPath, String zipName) throws IOException {
        Path src = Path.of(folderPath);
        if (!Files.exists(src) || !Files.isDirectory(src)) {
            log.warn("Carpeta no encontrada, no se incluye en backup: {}", folderPath);
            return null;
        }
        Path dest = Path.of(backupDir, zipName);
        zipDirectory(src, dest);
        log.info("ZIP creado: {}", dest);
        return dest.toString();
    }

    private void zipDirectory(Path sourceDirPath, Path zipFilePath) throws IOException {
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(zipFilePath))) {
            Files.walk(sourceDirPath)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(sourceDirPath.relativize(path).toString().replace("\\", "/"));
                        try (InputStream is = Files.newInputStream(path)) {
                            zs.putNextEntry(zipEntry);
                            is.transferTo(zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        }
    }

    // --- Retención ---
    private void cleanupOldBackups() throws IOException {
        if (keepDays <= 0) return;
        Instant limit = Instant.now().minus(Duration.ofDays(keepDays));
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(Path.of(backupDir))) {
            for (Path p : ds) {
                if (!Files.isRegularFile(p)) continue;
                FileTime ft = Files.getLastModifiedTime(p);
                if (ft.toInstant().isBefore(limit)) {
                    log.info("Eliminando backup antiguo: {}", p.getFileName());
                    Files.deleteIfExists(p);
                }
            }
        }
    }

    // --- Parseo del JDBC URL ---
    private DbInfo parseJdbcUrl(String url) {
        // ejemplo: jdbc:postgresql://localhost:5432/postgres?currentSchema=alquiler_bicicletas
        String u = url.startsWith("jdbc:") ? url.substring(5) : url; // quita "jdbc:"
        URI uri = URI.create(u); // scheme=postgresql
        String host = uri.getHost();
        int port = (uri.getPort() == -1) ? 5432 : uri.getPort();
        String path = uri.getPath(); // "/postgres"
        String db = (path != null && path.length() > 1) ? path.substring(1) : "postgres";

        String schema = null;
        String query = uri.getQuery(); // "currentSchema=alquiler_bicicletas"
        if (query != null) {
            for (String kv : query.split("&")) {
                String[] parts = kv.split("=");
                if (parts.length == 2 && parts[0].equalsIgnoreCase("currentSchema")) {
                    schema = parts[1];
                }
            }
        }
        return new DbInfo(host, port, db, schema);
    }

    private record DbInfo(String host, int port, String database, String schema) {}
}
