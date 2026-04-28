package com.alquiler.alquilerbicicletas.servicios;

import com.alquiler.alquilerbicicletas.enumerados.EstadoReserva;
import com.alquiler.alquilerbicicletas.modelos.Reserva;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
public class EmailService {
    @Value("${frontend.base-url}")
    private String frontendUrl;

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String remitente;

    @Value("${app.email.sender.name:Bike Rental}")
    private String senderName;

    @Value("${app.email.copy.to:alba.munozydiezma@gmail.com}")
    private String emailCopia;

    public void sendEmail(String to, String subject, String text) throws MailException, MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        messageHelper.setFrom(remitente, senderName);
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);
        messageHelper.setText(text, true);

        emailSender.send(mimeMessage);
    }

    @Async
    public void enviarConfirmacionReserva(Reserva reserva, String contratoFilePath) {
        try {
            logger.info("Preparando email de confirmación para reserva {} a {}",
                    reserva.getId(), reserva.getCliente().getEmail());

            // ENVIAR EMAIL BONITO AL CLIENTE
            enviarEmailCliente(reserva, contratoFilePath);

            // ENVIAR COPIA FUNCIONAL A LA EMPRESA
            enviarCopiaEmpresa(reserva, contratoFilePath);

        } catch (Exception e) {
            logger.error("❌ Error general al enviar emails para reserva {}", reserva.getId(), e);
        }
    }

    /**
     * Envía email BONITO al cliente
     */
    private void enviarEmailCliente(Reserva reserva, String contratoFilePath) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(remitente, senderName);
            helper.setTo(reserva.getCliente().getEmail());
            helper.setReplyTo(remitente);

            String asunto = String.format("Confirmación de Reserva #%d - Bike Rental", reserva.getId());
            helper.setSubject(asunto);

            message.setHeader("X-Priority", "1");
            message.setHeader("X-Mailer", "BikeRentalApp");

            // CONTENIDO BONITO PARA EL CLIENTE
            String contenido = generarContenidoClienteBonito(reserva, true);
            helper.setText(contenido, true);

            // Generar QR
            String reservaUrl = frontendUrl + "/api/reservas/verReserva/" + reserva.getId();
            ByteArrayOutputStream qrOutputStream = generarQRCode(reservaUrl);
            helper.addInline("qrReserva", new ByteArrayResource(qrOutputStream.toByteArray()), "image/png");

            // Adjuntar PDF
            boolean pdfAdjuntado = adjuntarContratoPDF(helper, reserva, contratoFilePath);

            if (!pdfAdjuntado) {
                contenido = generarContenidoClienteBonito(reserva, true, true);
                helper.setText(contenido, true);
            }

            emailSender.send(message);
            logger.info("✅ Email BONITO para CLIENTE enviado a {}", reserva.getCliente().getEmail());

        } catch (Exception e) {
            logger.error("❌ Error al enviar email al cliente para reserva {}", reserva.getId(), e);
            enviarEmailClienteFallback(reserva);
        }
    }

    /**
     * Envía copia FUNCIONAL a la empresa
     */
    private void enviarCopiaEmpresa(Reserva reserva, String contratoFilePath) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(remitente, senderName);
            helper.setTo(emailCopia);
            helper.setSubject(String.format("📋 NUEVA RESERVA #%d - %s", reserva.getId(), reserva.getCliente().getNombre()));

            // CONTENIDO FUNCIONAL PARA LA EMPRESA
            String contenido = generarContenidoEmpresaFuncional(reserva);
            helper.setText(contenido, true);

            // Adjuntar PDF del contrato
            adjuntarContratoPDF(helper, reserva, contratoFilePath);

            emailSender.send(message);
            logger.info("✅ Copia FUNCIONAL para EMPRESA enviada a {}", emailCopia);

        } catch (Exception e) {
            logger.error("❌ Error al enviar copia a la empresa para reserva {}", reserva.getId(), e);
        }
    }

    /**
     * Genera contenido BONITO para el cliente
     */
    private String generarContenidoClienteBonito(Reserva reserva, boolean incluirCancelacion) {
        return generarContenidoClienteBonito(reserva, incluirCancelacion, false);
    }

    private String generarContenidoClienteBonito(Reserva reserva, boolean incluirCancelacion, boolean forzarEnlace) {
        String bicicletasHTML = reserva.getBicicletas().isEmpty()
                ? "<li>Ninguna bicicleta seleccionada</li>"
                : reserva.getBicicletas().stream()
                .map(b -> "<li>🚲 " + b.getBicicleta().getNombre() + "</li>")
                .collect(Collectors.joining());

        String accesoriosHTML = reserva.getAccesorios().isEmpty()
                ? "<li>Ningún accesorio seleccionado</li>"
                : reserva.getAccesorios().stream()
                .map(a -> "<li>🎒 " + a.getAccesorio().getNombre() + " (x" + a.getCantidad() + ")" +
                        (a.getPrecioTotal() != null ? " - " + String.format("%.2f €", a.getPrecioTotal()) : "") + "</li>")
                .collect(Collectors.joining());

        // Construir el contenido base
        String contenidoBase = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body { font-family: 'Arial', sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; }
                .container { max-width: 600px; margin: 0 auto; background: #ffffff; }
                .header { background: linear-gradient(135deg, #2563eb, #1e40af); color: white; padding: 30px 20px; text-align: center; }
                .content { padding: 30px 20px; background: #f8fafc; }
                .footer { padding: 20px; text-align: center; font-size: 12px; color: #64748b; background: #e2e8f0; }
                .button { background: #2563eb; color: white; padding: 12px 24px; text-decoration: none; border-radius: 8px; display: inline-block; margin: 10px 0; font-weight: bold; }
                .info-box { background: white; padding: 20px; border-radius: 10px; border-left: 4px solid #2563eb; margin: 20px 0; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                .warning-box { background: #fef3cd; padding: 15px; border-radius: 8px; border-left: 4px solid #f59e0b; margin: 15px 0; }
                .success-box { background: #d1fae5; padding: 15px; border-radius: 8px; border-left: 4px solid #10b981; margin: 15px 0; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1 style="margin: 0; font-size: 28px;">✅ Confirmación de Reserva</h1>
                    <p style="margin: 10px 0 0 0; opacity: 0.9;">Bike Rental - Taller de Bicicletas</p>
                </div>
                
                <div class="content">
                    <p>Hola <strong style="color: #2563eb;">%s</strong>,</p>
                    <p>¡Gracias por confiar en nosotros! Tu reserva ha sido confirmada exitosamente. 🎉</p>
                    
                    <div class="info-box">
                        <h3 style="color: #2563eb; margin-top: 0;">📋 Detalles de tu reserva</h3>
                        <p><strong>Número de reserva:</strong> #%d</p>
                        <p><strong>Fecha de recogida:</strong> %s</p>
                        <p><strong>Fecha de devolución:</strong> %s</p>
                        <p><strong>Precio total:</strong> <span style="color: #059669; font-weight: bold;">%.2f €</span></p>
                        <p><strong>Estado:</strong> <span style="color: #059669;">%s</span></p>
                        
                        <div style="margin-top: 15px;">
                            <h4 style="margin-bottom: 10px;">🚲 Bicicletas reservadas:</h4>
                            <ul style="margin: 0; padding-left: 20px;">%s</ul>
                        </div>
                        
                        <div style="margin-top: 15px;">
                            <h4 style="margin-bottom: 10px;">🎒 Accesorios:</h4>
                            <ul style="margin: 0; padding-left: 20px;">%s</ul>
                        </div>
                    </div>

                    <div class="success-box">
                        <h3 style="color: #059669; margin-top: 0;">💳 Información de pago</h3>
                        <p><strong>PAGO EN TIENDA:</strong> Efectivo o Tarjeta</p>
                        <p>El pago se realizará al recoger las bicicletas en nuestra tienda.</p>
                    </div>
        """.formatted(
                reserva.getCliente().getNombre(),
                reserva.getId(),
                reserva.getFechaInicio().format(formatter),
                reserva.getFechaFin().format(formatter),
                reserva.getPrecioTotal().doubleValue(),
                reserva.getEstado(),
                bicicletasHTML,
                accesoriosHTML
        );

        // Construir las secciones dinámicas
        StringBuilder contenidoCompleto = new StringBuilder(contenidoBase);

        // SECCIÓN DEL CONTRATO
        if (forzarEnlace) {
            contenidoCompleto.append("""
            <div class="warning-box">
                <h3 style="color: #d97706; margin-top: 0;">📄 Contrato de reserva</h3>
                <p><strong>Puedes ver y descargar tu contrato en el siguiente enlace:</strong></p>
                <p style="text-align: center;">
                    <a href="%s" class="button">📄 Ver y Descargar Contrato PDF</a>
                </p>
            </div>
            """.formatted(reserva.getContratoUrl()));
        } else {
            contenidoCompleto.append("""
            <div class="info-box">
                <h3 style="color: #2563eb; margin-top: 0;">📄 Contrato de reserva</h3>
                <p>✅ <strong>Hemos adjuntado tu contrato en PDF</strong> a este email.</p>
            </div>
            """.formatted(reserva.getContratoUrl()));
        }

        // CÓDIGO QR
        contenidoCompleto.append("""
        <div style="text-align: center; margin: 25px 0; padding: 20px; background: white; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
            <h3 style="color: #2563eb; margin-top: 0;">📱 Escanea para ver tu reserva</h3>
            <img src="cid:qrReserva" alt="Código QR de la reserva" style="max-width: 180px; border: 2px solid #e2e8f0; border-radius: 10px;"/>
            <p style="font-size: 0.9em; color: #64748b; margin-top: 10px;">Escanea este código QR para acceder rápidamente a los detalles de tu reserva</p>
        </div>
        """);

        // CANCELACIÓN
        if (incluirCancelacion) {
            contenidoCompleto.append("""
            <div class="warning-box">
                <h3 style="color: #dc2626; margin-top: 0;">❌ ¿Necesitas cancelar?</h3>
                <p>Si no puedes asistir, puedes cancelar tu reserva:</p>
                <p style="text-align: center;">
                    <a href="%s" style="background-color: #dc2626; color: white; padding: 12px 24px; text-decoration: none; border-radius: 8px; font-weight: bold; display: inline-block;">Cancelar Reserva</a>
                </p>
            </div>
            """.formatted(frontendUrl + "/api/reservas/cancelar/" + reserva.getCancelToken()));
        }

        // CIERRE
        contenidoCompleto.append("""
                <p>Si tienes alguna pregunta, no dudes en respondernos a este email.</p>
                <p>¡Esperamos que disfrutes de tu experiencia con Bike Rental! 🚴‍♂️</p>
                <p>Saludos cordiales,<br><strong>El equipo de Bike Rental</strong></p>
            </div>
            
            <div class="footer">
                <p><strong>Bike Rental - Taller de Bicicletas 1908 SL</strong></p>
                <p>Almirante Tenorio, 1, 41003 Sevilla</p>
                <p>📞 +34 664 022 266 | ✉️ i@bikerental.com.es</p>
                <p>© 2025 Bike Rental - Todos los derechos reservados</p>
            </div>
        </div>
    </body>
    </html>
    """);

        return contenidoCompleto.toString();
    }

    /**
     * Genera contenido FUNCIONAL para la empresa con enlace específico a la reserva
     */
    private String generarContenidoEmpresaFuncional(Reserva reserva) {
        String bicicletasHTML = reserva.getBicicletas().isEmpty()
                ? "<li>Ninguna bicicleta seleccionada</li>"
                : reserva.getBicicletas().stream()
                .map(b -> "<li>" + b.getBicicleta().getNombre() + " (ID: " + b.getBicicleta().getId() + ")</li>")
                .collect(Collectors.joining());

        String accesoriosHTML = reserva.getAccesorios().isEmpty()
                ? "<li>Ningún accesorio seleccionado</li>"
                : reserva.getAccesorios().stream()
                .map(a -> "<li>" + a.getAccesorio().getNombre() + " - Cantidad: " + a.getCantidad() + " - Total: " +
                        (a.getPrecioTotal() != null ? String.format("%.2f €", a.getPrecioTotal()) : "0.00 €") + "</li>")
                .collect(Collectors.joining());

        // URL específica para la reserva en el admin (si tu frontend soporta parámetros)
        String urlAdminReserva = "http://localhost:4200/admin/reservas";
        // Si tu admin permite ver una reserva específica, podrías usar:
        // String urlAdminReserva = "http://localhost:4200/admin/reservas/" + reserva.getId();

        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <style>
                body { font-family: Arial, sans-serif; line-height: 1.4; color: #333; }
                .container { max-width: 700px; margin: 0 auto; }
                .header { background: #dc3545; color: white; padding: 15px; text-align: center; }
                .section { background: #f8f9fa; padding: 15px; margin: 10px 0; border-radius: 5px; }
                .section h3 { margin-top: 0; color: #495057; }
                .btn { display: inline-block; padding: 10px 20px; background: #007bff; color: white; text-decoration: none; border-radius: 5px; margin: 5px; font-weight: bold; }
                .btn:hover { background: #0056b3; }
                .btn-admin { background: #28a745; }
                .btn-admin:hover { background: #218838; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h2>📋 NUEVA RESERVA #%d</h2>
                    <p>%s %s - %.2f €</p>
                </div>
                
                <div class="section">
                    <h3>👤 Información del Cliente</h3>
                    <p><strong>Nombre:</strong> %s %s</p>
                    <p><strong>Email:</strong> %s</p>
                    <p><strong>Teléfono:</strong> %s</p>
                    <p><strong>Documento:</strong> %s</p>
                </div>
                
                <div class="section">
                    <h3>📅 Detalles de la Reserva</h3>
                    <p><strong>Fecha/Hora Inicio:</strong> %s</p>
                    <p><strong>Fecha/Hora Fin:</strong> %s</p>
                    <p><strong>Tarifa:</strong> %s</p>
                    <p><strong>Precio Total:</strong> <strong style="color: #28a745; font-size: 1.1em;">%.2f €</strong></p>
                    <p><strong>Estado:</strong> <span style="color: %s;">%s</span></p>
                </div>
                
                <div class="section">
                    <h3>🚲 Bicicletas Reservadas</h3>
                    <ul>%s</ul>
                </div>
                
                <div class="section">
                    <h3>🎒 Accesorios Reservados</h3>
                    <ul>%s</ul>
                </div>
                
                <div class="section">
                    <h3>🔗 Acciones Rápidas</h3>
                    <div style="text-align: center;">
                        <a href="%s" class="btn" target="_blank">📄 Ver Contrato PDF</a>
                        <a href="%s" class="btn btn-admin" target="_blank">⚙️ Panel de Administración</a>
                    </div>
                    <p style="text-align: center; margin-top: 10px;">
                        <a href="%s" style="color: #6c757d; font-size: 0.9em;">🔍 Ver detalles técnicos de la reserva</a>
                    </p>
                </div>
                
                <div style="text-align: center; margin-top: 20px; color: #6c757d; font-size: 12px; padding: 15px; background: #e9ecef; border-radius: 5px;">
                    <p><strong>🚨 :</strong> Revisar la reserva en el sistema</p>
                    <p>Notificación automática - Bike Rental Admin</p>
                    <p>📧 Recibido el: %s</p>
                </div>
            </div>
        </body>
        </html>
        """.formatted(
                reserva.getId(),
                reserva.getCliente().getNombre(),
                reserva.getCliente().getApellido(),
                reserva.getPrecioTotal().doubleValue(),
                reserva.getCliente().getNombre(),
                reserva.getCliente().getApellido(),
                reserva.getCliente().getEmail(),
                reserva.getCliente().getTelefono(),
                reserva.getCliente().getDocumentoIdentidad(),
                reserva.getFechaInicio().format(formatter),
                reserva.getFechaFin().format(formatter),
                reserva.getTarifa().getNombre(),
                reserva.getPrecioTotal().doubleValue(),
                getColorEstado(reserva.getEstado()), // Método auxiliar para colores
                reserva.getEstado(),
                bicicletasHTML,
                accesoriosHTML,
                reserva.getContratoUrl(),
                "http://localhost:4200/admin/reservas", // ENLACE AL PANEL DE ADMIN
                frontendUrl + "/api/reservas/" + reserva.getId(),
                java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
    }

    // Método auxiliar para colores según el estado
    private String getColorEstado(EstadoReserva estado) {
        switch (estado) {
            case CONFIRMADA: return "#28a745";
            case PENDIENTE: return "#ffc107";
            case CANCELADA: return "#dc3545";
            case COMPLETADA: return "#17a2b8";
            default: return "#6c757d";
        }
    }

    /**
     * Genera QR Code
     */
    private ByteArrayOutputStream generarQRCode(String texto) throws WriterException, IOException {
        ByteArrayOutputStream qrOutputStream = new ByteArrayOutputStream();
        BitMatrix bitMatrix = new QRCodeWriter().encode(
                texto,
                BarcodeFormat.QR_CODE,
                200,
                200
        );
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", qrOutputStream);
        return qrOutputStream;
    }

    /**
     * Fallback para email al cliente
     */
    private void enviarEmailClienteFallback(Reserva reserva) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(remitente, senderName);
            helper.setTo(reserva.getCliente().getEmail());
            helper.setSubject(String.format("Confirmación de Reserva #%d - Bike Rental", reserva.getId()));

            String contenido = generarContenidoClienteBonito(reserva, true, true);
            helper.setText(contenido, true);

            emailSender.send(message);
            logger.info("✅ Email de fallback enviado al cliente");

        } catch (Exception e) {
            logger.error("❌ Error incluso al enviar email de fallback", e);
        }
    }

    private boolean adjuntarContratoPDF(MimeMessageHelper helper, Reserva reserva, String contratoFilePath) {
        try {
            if (contratoFilePath == null || contratoFilePath.trim().isEmpty()) {
                logger.warn("Ruta de contrato vacía para reserva {}", reserva.getId());
                return false;
            }

            File contratoFile = new File(contratoFilePath);
            if (!contratoFile.exists()) {
                logger.error("❌ El archivo del contrato no existe: {}", contratoFilePath);
                return false;
            }

            if (!contratoFile.canRead()) {
                logger.error("❌ No se puede leer el archivo del contrato: {}", contratoFilePath);
                return false;
            }

            long fileSize = contratoFile.length();
            if (fileSize == 0) {
                logger.error("❌ El archivo del contrato está vacío: {}", contratoFilePath);
                return false;
            }

            if (fileSize > 5 * 1024 * 1024) {
                logger.warn("⚠️ PDF demasiado grande para Outlook ({} bytes), usando enlace", fileSize);
                return false;
            }

            FileSystemResource file = new FileSystemResource(contratoFile);
            String nombreArchivo = String.format("Contrato_Reserva_%d_BikeRental.pdf", reserva.getId());
            helper.addAttachment(nombreArchivo, file);

            logger.info("✅ PDF adjuntado correctamente: {} ({} bytes)", contratoFilePath, fileSize);
            return true;

        } catch (Exception e) {
            logger.error("❌ Error al adjuntar PDF para reserva {}", reserva.getId(), e);
            return false;
        }
    }
}