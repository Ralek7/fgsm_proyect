package utils;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

public class PDFReportUtils {
    private String executionFolder;
    private Instant startTime;
    private Instant endTime;

    public PDFReportUtils(String executionFolder) {
        this.executionFolder = executionFolder;
    }

    public void generatePDFReport(String reportName, String status, String executionTime, String scenario) {
        String pdfPath = executionFolder + "/" + reportName + ".pdf";
        try (PdfWriter writer = new PdfWriter(pdfPath)) {
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Cargar el logo
            File logoFile = new File("src/test/resources/reports/Logo_BanCoppel.png");
            if (logoFile.exists()) {
                ImageData logoImageData = ImageDataFactory.create(logoFile.getAbsolutePath());
                Image logoImage = new Image(logoImageData);
                logoImage.setWidth(200); // Ajustar el tamaño del logo si es necesario
                logoImage.setHorizontalAlignment(com.itextpdf.layout.property.HorizontalAlignment.CENTER);
                document.add(logoImage); // Agregar el logo al documento
            } else {
                System.out.println("Logo no encontrado en la ruta: " + logoFile.getAbsolutePath());
            }

            // Fecha de inicio de la ejecución
            startTime = Instant.now();
            String startDate = formatInstant(startTime);

            // Título del reporte
            document.add(new Paragraph("Reporte de Ejecución de Test:")
                    .setFontSize(12)
                    .setFontColor(ColorConstants.BLUE)
                    .setTextAlignment(TextAlignment.CENTER));

            // Nombre del escenario
            document.add(new Paragraph(scenario)
                    .setBold()
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER));

            // Estado con color condicional (rojo si "FAILED", verde si "PASSED")
            Paragraph statusParagraph = new Paragraph("Status: " + status)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER);
            if (status.equalsIgnoreCase("FAILED")) {
                statusParagraph.setFontColor(ColorConstants.RED); // Rojo si falla
            } else {
                statusParagraph.setFontColor(ColorConstants.GREEN); // Verde si pasa
            }
            document.add(statusParagraph);

            // Tiempo de ejecución
            document.add(new Paragraph("Tiempo de ejecución: " + executionTime + " segundos")
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER));

            // Hora de inicio de la ejecución
            document.add(new Paragraph("Hora de inicio: " + startDate)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER));

            // Hora de fin de la ejecución
            endTime = Instant.now();
            String endDate = formatInstant(endTime);
            document.add(new Paragraph("Hora de fin: " + endDate)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER));

            // Añadir capturas de pantalla
            addScreenshotsToDocument(document, scenario);

            // Numeración de páginas
            addPageNumbers(pdf);

            document.close();
            System.out.println("PDF report generated: " + pdfPath);
        } catch (IOException e) {
            System.out.println("Error generating PDF: " + e.getMessage());
        }
    }

    private String formatInstant(Instant instant) {
        // Convertir el instante a fecha y hora local con milisegundos
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        // Formatear en el estilo deseado: "yyyy-MM-dd_HH-mm-ss.SSS" (incluso milisegundos)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss.SSS");
        return localDateTime.format(formatter);
    }

    private void addScreenshotsToDocument(Document document, String scenarioName) {
        String scenarioFolderPath = executionFolder + "/" + scenarioName + "_TestScreenshots";
        File scenarioFolder = new File(scenarioFolderPath);
        File[] screenshotFiles = scenarioFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));

        if (screenshotFiles != null && screenshotFiles.length > 0) {
            // Ordenar los archivos por la fecha de última modificación (ascendente)
            Arrays.sort(screenshotFiles, Comparator.comparingLong(File::lastModified));

            for (File screenshot : screenshotFiles) {
                try {
                    // Crear la imagen a partir de la captura de pantalla
                    ImageData imageData = ImageDataFactory.create(screenshot.getAbsolutePath());
                    Image image = new Image(imageData);
                    image.setMaxWidth(400); // Ajustar el tamaño máximo de la imagen
                    image.setHorizontalAlignment(com.itextpdf.layout.property.HorizontalAlignment.CENTER); // Centrar la imagen

                    // Agregar la imagen al documento
                    document.add(image);

                    // Agregar el nombre del archivo de la imagen debajo de la imagen
                    document.add(new Paragraph(screenshot.getName())
                            .setTextAlignment(TextAlignment.CENTER)
                            .setFontSize(10));
                } catch (IOException e) {
                    System.out.println("Error adding screenshot: " + screenshot.getName());
                }
            }
        } else {
            document.add(new Paragraph("No se encontraron capturas de pantalla para este escenario.")
                    .setTextAlignment(TextAlignment.CENTER));
        }
    }



    public void addPageNumbers(PdfDocument pdf) {
        try {
            // Cargar fuente estándar (Helvetica)
            PdfFont font = PdfFontFactory.createFont("Helvetica");
            for (int i = 1; i <= pdf.getNumberOfPages(); i++) {
                PdfPage page = pdf.getPage(i);
                PdfCanvas canvas = new PdfCanvas(page);

                // Agregar texto del número de página
                canvas.beginText()
                        .setFontAndSize(font, 8)
                        .moveText(270, 10) // Posición del texto
                        .showText("Página " + i + " de " + pdf.getNumberOfPages())
                        .endText()
                        .release();
            }
        } catch (Exception e) {
            System.out.println("Error al añadir números de página: " + e.getMessage());
        }
    }
}
