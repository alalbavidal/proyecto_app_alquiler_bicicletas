package com.alquiler.alquilerbicicletas.servicios;

import com.alquiler.alquilerbicicletas.modelos.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
public class ContratoService {

    private static final Logger logger = LoggerFactory.getLogger(ContratoService.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Value("${app.contratos.path:/tmp/contratos}")
    private String contratosPath;

    @Value("${app.contratos.url-base}")
    private String contratosUrlBase;

    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(contratosPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                logger.info("Directorio de contratos creado en: {}", path.toAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("No se pudo crear el directorio de contratos", e);
            throw new RuntimeException("No se pudo crear el directorio de contratos", e);
        }
    }

    // Método original (por defecto español) para compatibilidad
    public ContratoGenerado generarContratoPDF(Reserva reserva) {
        return generarContratoPDF(reserva, "es");
    }

    // Nuevo método con parámetro de idioma
    public ContratoGenerado generarContratoPDF(Reserva reserva, String idioma) {
        logger.info("Generando contrato para la reserva con ID {} en idioma: {}", reserva.getId(), idioma);

        try {
            // 1) Asegura carpeta
            Path dirPath = Paths.get(contratosPath);
            Files.createDirectories(dirPath);

            // 2) Nombre del archivo (siempre el mismo para la reserva)
            String fileName = "contrato_" + reserva.getId() + ".pdf";
            Path destino = dirPath.resolve(fileName).normalize();

            // 3) Generación PDF
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(destino.toFile()));
            document.open();

            // Determinar textos según idioma
            boolean esIngles = "en".equals(idioma);

            // --- LOGO ---
            try {
                Image logo = Image.getInstance(
                        Objects.requireNonNull(
                                getClass().getClassLoader().getResource("static/img/logo.png")
                        )
                );
                logo.scaleToFit(200, 200);
                logo.setAlignment(Element.ALIGN_CENTER);
                document.add(logo);
            } catch (Exception e) {
                logger.warn("No se pudo cargar el logo", e);
            }

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

            // Título principal
            String titulo = esIngles ? "BICYCLE RENTAL AGREEMENT" : "CONTRATO DE ALQUILER DE BICICLETAS";
            Paragraph title = new Paragraph(titulo, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // --- EMPRESA ---
            document.add(new Paragraph("BIKE RENTAL", subtitleFont));
            document.add(new Paragraph("Taller de Bicicletas 1908 SL", normalFont));
            document.add(new Paragraph("NIF - B91767558", normalFont));
            String direccionLabel = esIngles ? "Address" : "Dirección";
            document.add(new Paragraph(direccionLabel + ": Almirante Tenorio, 1, 41003 Sevilla", normalFont));
            String telefonoLabel = esIngles ? "Phone" : "Teléfono";
            document.add(new Paragraph(telefonoLabel + ": +34 664 022 266", normalFont));
            String emailLabel = esIngles ? "Email" : "Email";
            document.add(new Paragraph(emailLabel + ": i@bikerental.com.es", normalFont));
            document.add(new Paragraph(" ", normalFont));

            // --- CLIENTE ---
            var cliente = reserva.getCliente();
            String datosCliente = esIngles ? "CUSTOMER INFORMATION" : "DATOS DEL CLIENTE";
            document.add(new Paragraph(datosCliente, subtitleFont));
            String nombreLabel = esIngles ? "Name" : "Nombre";
            document.add(new Paragraph(nombreLabel + ": " + cliente.getNombre() + " " + cliente.getApellido(), normalFont));
            String documentoLabel = esIngles ? "ID/Passport" : "DNI/NIE/Pasaporte";
            document.add(new Paragraph(documentoLabel + ": " + cliente.getDocumentoIdentidad(), normalFont));
            document.add(new Paragraph(emailLabel + ": " + cliente.getEmail(), normalFont));
            document.add(new Paragraph(telefonoLabel + ": " + cliente.getTelefono(), normalFont));
            document.add(new Paragraph(" ", normalFont));

            // --- RESERVA ---
            String detallesReserva = esIngles ? "RESERVATION DETAILS" : "DETALLES DE LA RESERVA";
            document.add(new Paragraph(detallesReserva, subtitleFont));
            String numeroReserva = esIngles ? "Reservation number" : "Número de reserva";
            document.add(new Paragraph(numeroReserva + ": " + reserva.getId(), normalFont));
            String fechaInicio = esIngles ? "Start date" : "Fecha de inicio";
            document.add(new Paragraph(fechaInicio + ": " + reserva.getFechaInicio().format(formatter), normalFont));
            String fechaFin = esIngles ? "End date" : "Fecha de fin";
            document.add(new Paragraph(fechaFin + ": " + reserva.getFechaFin().format(formatter), normalFont));
            String tarifaAplicada = esIngles ? "Applied rate" : "Tarifa aplicada";
            document.add(new Paragraph(tarifaAplicada + ": " + reserva.getTarifa().getNombre(), normalFont));
            String descripcionTarifa = esIngles ? "Rate description" : "Descripción tarifa";
            document.add(new Paragraph(descripcionTarifa + ": " + reserva.getTarifa().getDescripcion(), normalFont));
            String precioTarifa = esIngles ? "Rate price" : "Precio de la tarifa";
            document.add(new Paragraph(precioTarifa + ": " + euros(BigDecimal.valueOf(reserva.getTarifa().getPrecio())), normalFont));
            document.add(new Paragraph(" ", normalFont));

            // --- BICICLETAS ---
            String bicicletasAlquiladas = esIngles ? "RENTED BICYCLES" : "BICICLETAS ALQUILADAS";
            document.add(new Paragraph(bicicletasAlquiladas, subtitleFont));
            PdfPTable tablaBicis = new PdfPTable(2);
            tablaBicis.setWidthPercentage(100);
            tablaBicis.setSpacingBefore(10f);
            tablaBicis.setSpacingAfter(10f);

            String idBicicleta = esIngles ? "Bike ID" : "ID Bicicleta";
            String modelo = esIngles ? "Model" : "Modelo";
            tablaBicis.addCell(new PdfPCell(new Phrase(idBicicleta, boldFont)));
            tablaBicis.addCell(new PdfPCell(new Phrase(modelo, boldFont)));

            for (ReservaBicicleta rb : reserva.getBicicletas()) {
                tablaBicis.addCell(new Phrase(String.valueOf(rb.getBicicleta().getId()), normalFont));
                tablaBicis.addCell(new Phrase(String.valueOf(rb.getBicicleta().getModelo()), normalFont));
            }
            document.add(tablaBicis);

            // --- ACCESORIOS ---
        if (reserva.getAccesorios() != null && !reserva.getAccesorios().isEmpty()) {
            String accesoriosAlquilados = esIngles ? "RENTED ACCESSORIES" : "ACCESORIOS ALQUILADOS";
            document.add(new Paragraph(accesoriosAlquilados, subtitleFont));
            PdfPTable tablaAccesorios = new PdfPTable(3);
            tablaAccesorios.setWidthPercentage(100);
            tablaAccesorios.setSpacingBefore(10f);
            tablaAccesorios.setSpacingAfter(10f);

            String accesorio = esIngles ? "Accessory" : "Accesorio";
            String cantidad = esIngles ? "Quantity" : "Cantidad";
            String precio = esIngles ? "Price" : "Precio";
            tablaAccesorios.addCell(new PdfPCell(new Phrase(accesorio, boldFont)));
            tablaAccesorios.addCell(new PdfPCell(new Phrase(cantidad, boldFont)));
            tablaAccesorios.addCell(new PdfPCell(new Phrase(precio, boldFont)));

            for (ReservaAccesorio ra : reserva.getAccesorios()) {
                tablaAccesorios.addCell(new Phrase(ra.getAccesorio().getNombre(), normalFont));
                tablaAccesorios.addCell(new Phrase(String.valueOf(ra.getCantidad()), normalFont));
                tablaAccesorios.addCell(new Phrase(euros(ra.getPrecioTotal()), normalFont));
            }
            document.add(tablaAccesorios);
        }

           // --- RESUMEN ECONÓMICO ---
        var extras = reserva.getExtrasTotal() == null ? BigDecimal.ZERO : reserva.getExtrasTotal();
        var base = reserva.getPrecioTotal().subtract(extras);
        
            // ✅ Calcular desglose del IVA (21%)
            BigDecimal ivaPorcentaje = new BigDecimal("0.21");
            BigDecimal subtotal = reserva.getPrecioTotal().divide(BigDecimal.ONE.add(ivaPorcentaje), 2, RoundingMode.HALF_UP);
            BigDecimal ivaCalculado = reserva.getPrecioTotal().subtract(subtotal);
            
            String resumenEconomico = esIngles ? "ECONOMIC SUMMARY" : "RESUMEN ECONÓMICO";
            document.add(new Paragraph(resumenEconomico, subtitleFont));
            String precioBase = esIngles ? "Subtotal (without VAT)" : "Subtotal (sin IVA)";
            document.add(new Paragraph(precioBase + ": " + euros(subtotal), normalFont));
            
            // ✅ Añadir el IVA desglosado
            String ivaText = esIngles ? "VAT (21%)" : "IVA (21%)";
            document.add(new Paragraph(ivaText + ": " + euros(ivaCalculado), normalFont));
            
            // ✅ Mostrar el total con IVA incluido
            String precioTotal = esIngles ? "TOTAL (VAT included)" : "TOTAL (IVA incluido)";
            document.add(new Paragraph(precioTotal + ": " + euros(reserva.getPrecioTotal()), boldFont));
            document.add(new Paragraph(" ", normalFont));

            String formaPago = esIngles ? "Payment method in STORE: cash or card" : "Forma de pago en TIENDA: efectivo o tarjeta";
            document.add(new Paragraph(formaPago, boldFont));
            document.add(new Paragraph(" ", normalFont));

            // Términos y condiciones
            String terminosCondiciones = esIngles ? "TERMS AND CONDITIONS" : "TÉRMINOS Y CONDICIONES";
            document.add(new Paragraph(terminosCondiciones, subtitleFont));

            // Textos de términos según idioma
            if (esIngles) {
                document.add(new Paragraph("1. The lessee must be over 18 years old and possess a National Identity Document for EU members and Passport for other countries. Likewise, they declare to be in perfect health condition, capable of handling a bicycle and having the necessary control of the rented equipment.", normalFont));
                document.add(new Paragraph("2. Payment for the total contracted service will be made upon delivery of the bicycle.", normalFont));
                document.add(new Paragraph("3. Upon delivery of the bicycle, the customer must leave a deposit and a copy of their identity document, or pre-authorize the charge on their credit card for the corresponding amount, in case damages to the bicycle or delivered material have to be charged, or in case of theft.", normalFont));
                document.add(new Paragraph("4. Failure to comply with the delivery time will result in the extension of the rental for equivalent periods, at the contracted price.", normalFont));
                document.add(new Paragraph("5. The return of the bicycle will be made at the address of Taller de Bicicletas, during business hours from 9 to 14 and from 17 to 21, Monday to Saturday, and from 9 to 14 on Saturdays. Check prices for out-of-hours collection.", normalFont));
                document.add(new Paragraph("6. The bicycle is delivered in perfect working condition and with the following accessories: U-lock, front and rear lights. In addition to the accessories indicated in the \"Service\" table.", normalFont));
                document.add(new Paragraph("7. In case of theft, the lessee is responsible and must file a report with the Police and present it at our establishment. Likewise, they must pay the RRP of the stolen bicycle, which amounts to 399 €. A penalty of 15 € is established for loss of the lock key.", normalFont));
                document.add(new Paragraph("8. The lessor is not responsible for claims for accidents, injuries, blows or damages caused to themselves and/or third parties or their property, or for loss of objects during the rental period.", normalFont));
                document.add(new Paragraph("9. It is the responsibility of the lessee to respect Spanish traffic rules and drive with care and respect for pedestrians. It is forbidden to carry adult passengers on the bicycle. Fines for driving or parking are the responsibility of the lessee.", normalFont));
                document.add(new Paragraph("10. This agreement is governed by Spanish law.", normalFont));
                document.add(new Paragraph("11. Cancellation link: https://bikerental.com/cancelar/" + reserva.getCancelToken(), normalFont));
            } else {
                document.add(new Paragraph("1. El arrendatario debe ser mayor de 18 años y poseer Documento Nacional de Identidad para miembros de la Unión Europea y Pasaporte para el resto de países. Así mismo, declara estar en perfectas condiciones de salud, ser capaz de manejar una bicicleta y poseer el control necesario del equipamiento alquilado.", normalFont));
                document.add(new Paragraph("2. A la entrega de la bicicleta se efectuará el pago del total del servicio contratado.", normalFont));
                document.add(new Paragraph("3. A la entrega de la bicicleta el cliente deberá dejar en depósito una fianza y copia de su documento de identidad, o bien preautorizar el cargo en su tarjeta de crédito del importe correspondiente, para el caso de que tengan que cargarse daños causados a la bicicleta o al material entregado, o en caso de robo.", normalFont));
                document.add(new Paragraph("4. El incumplimiento de la hora de entrega supondrá la prórroga del alquiler por periodos equivalentes, al precio contratado.", normalFont));
                document.add(new Paragraph("5. La devolución de la bicicleta se realizará en el domicilio de Taller de Bicicletas, en horario comercial de 9 a 14 h y de 17 a 21 h, de lunes a sábado, y de 9 a 14 los sábados. Consultar precios para recogida fuera de horario.", normalFont));
                document.add(new Paragraph("6. La bicicleta se entrega en perfecto estado de funcionamiento y con los siguientes accesorios: candado tipo \"U\", luces delanteras y traseras. Además de los accesorios indicados en el cuadro de \"Servicio\".", normalFont));
                document.add(new Paragraph("7. En caso de robo el arrendatario es el responsable del mismo y deberá interponer una denuncia en la Policía y presentarla en nuestro establecimiento. Así mismo, deberá abonar el PVP de la bicicleta robada, que asciende a 399 €. Se establece una penalización por pérdida de la llave del candado de 15 €.", normalFont));
                document.add(new Paragraph("8. El arrendador no se hace responsable de reclamaciones por accidentes, heridas, golpes o daños ocasionados a sí mismos y/o a terceras personas o a su propiedad, o por pérdida de objetos durante el periodo del alquiler.", normalFont));
                document.add(new Paragraph("9. Es responsabilidad del arrendatario respetar las normas de tráfico españolas y conducir con cuidado y respeto a los peatones. Está prohibido llevar pasajeros adultos en la bicicleta. Las multas por conducción o aparcamiento son de cuenta del arrendatario.", normalFont));
                document.add(new Paragraph("10. Este contrato se rige por la ley española.", normalFont));
                document.add(new Paragraph("11. Enlace para cancelación: https://bikerental.com/cancelar/" + reserva.getCancelToken(), normalFont));
            }
            document.add(new Paragraph(" ", normalFont));

            // Protección de datos
            if (esIngles) {
                document.add(new Paragraph("DATA PROTECTION: In accordance with the provisions of Regulation (EU) 2016/679 of April 27 (GDPR) and Organic Law 3/2018 of December 5 (LOPDGDD), we inform you that the personal data and email address of the interested party will be processed under the responsibility of Taller de Bicicletas 1908 SL for a legitimate interest and for sending communications about our products and services and will be kept as long as neither party objects. The data will not be communicated to third parties, except legal obligation. We inform you that you can exercise the rights of access, rectification, portability and deletion of your data and those of limitation and opposition to its processing by contacting Virgen de los Gitanos 4 Acc., 41003 – Sevilla, Spain. Email: cfo@tallerdebicicletas.com. If you consider that the processing does not comply with current regulations, you can file a claim with the control authority at www.aepd.es.", normalFont));
            } else {
                document.add(new Paragraph("PROTECCIÓN DE DATOS: De conformidad con lo dispuesto en el Reglamento (UE) 2016/679 de 27 de abril (GDPR) y la Ley Orgánica 3/2018 de 5 de diciembre (LOPDGDD), le informamos que los datos personales y dirección de correo electrónico del interesado, serán tratados bajo la responsabilidad de Taller de Bicicletas 1908 SL por un interés legítimo y para el envío de comunicaciones sobre nuestros productos y servicios y se conservarán mientras ninguna de las partes se oponga a ello. Los datos no serán comunicados a terceros, salvo obligación legal. Le informamos que puede ejercer los derechos de acceso, rectificación, portabilidad y supresión de sus datos y los de limitación y oposición a su tratamiento dirigiéndose a Virgen de los Gitanos 4 Acc., 41003 – Sevilla, España. Email: cfo@tallerdebicicletas.com. Si considera que el tratamiento no se ajusta a la normativa vigente, podrá presentar una reclamación ante la autoridad de control en www.aepd.es.", normalFont));
            }
            document.add(new Paragraph(" ", normalFont));

            String aceptacionContrato = esIngles ?
                    "AGREEMENT ACCEPTANCE: The lessee declares having read and accepted the conditions of this agreement, as well as the data protection policy." :
                    "ACEPTACIÓN DEL CONTRATO: El arrendatario declara haber leído y aceptado las condiciones del presente contrato, así como la política de protección de datos.";
            document.add(new Paragraph(aceptacionContrato, normalFont));
            document.add(new Paragraph(" ", normalFont));

            // Recomendaciones
            String recomendaciones = esIngles ? "RECOMMENDATIONS" : "RECOMENDACIONES";
            document.add(new Paragraph(recomendaciones, subtitleFont));

            String textoRecomendaciones = esIngles ?
                    "We offer helmets, but they are not mandatory in the Spanish Traffic Code. If necessary, park the bicycle in designated areas, in crowded places, and securing all elements that ensure reliable parking. Whenever possible, keep the bicycle with you. Use lights during low visibility moments and, in any case, in winter from 5 PM and in summer from 8 PM." :
                    "Ofrecemos casco, pero no es obligatorio en el Código de Circulación español. En caso de ser necesario, aparcar la bicicleta en los lugares destinados para ello, en zonas concurridas, y amarrando todos los elementos que aseguren un aparcamiento fiable. Siempre que sea posible, mantenga la bicicleta consigo. Utilice las luces en los momentos de baja visibilidad y, en cualquier caso, en invierno a partir de las 17 h y en verano a partir de las 20 h.";
            document.add(new Paragraph(textoRecomendaciones, normalFont));
            document.add(new Paragraph(" ", normalFont));

            // Firmas
            String firmas = esIngles ? "SIGNATURES" : "FIRMAS";
            document.add(new Paragraph(firmas, subtitleFont));
            document.add(new Paragraph(" ", normalFont));
            document.add(new Paragraph(" ________________________                                    ________________________", normalFont));

            String firmaEmpresa = esIngles ? "Signed: Bike Rental / Taller de Bicicletas" : "Firmado: Bike Rental / Taller de Bicicletas";
            String firmaCliente = esIngles ? "Signed: The customer / lessee" : "Firmado: El cliente / arrendatario";
            document.add(new Paragraph(" " + firmaEmpresa + "                      " + firmaCliente, normalFont));

            document.close();

            logger.info("Contrato generado exitosamente en idioma {}: {}", idioma, destino);

            // 4) DEVUELVE URL pública COMPLETA
            String urlPublica = contratosUrlBase + "/contrato_" + reserva.getId() + ".pdf";

            // VALIDACIÓN: Asegurar que la URL sea completa
            if (!urlPublica.startsWith("http")) {
                logger.warn("URL del contrato no es completa: {}", urlPublica);
                // Fallback: construir URL completa
                urlPublica = "http://localhost:8085/contratos/contrato_" + reserva.getId() + ".pdf";
            }

            return new ContratoGenerado(destino.toAbsolutePath().toString(), urlPublica);

        } catch (DocumentException | IOException e) {
            logger.error("Error al generar contrato PDF", e);
            throw new RuntimeException("Error al generar contrato PDF", e);
        }
    }

    /** Helper para formatear BigDecimal de forma segura */
    private static String euros(BigDecimal v) {
        double d = (v == null) ? 0.0 : v.doubleValue();
        return String.format("%.2f €", d);
    }
}