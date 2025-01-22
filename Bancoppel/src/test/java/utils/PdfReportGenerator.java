package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.Canvas;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Arrays;
import java.util.Optional;

public class PdfReportGenerator {
    private int totalExitosos = 0;
    private int totalFallidos = 0;

    public void generatePdfFromLatestJson(String reportsFolderPath, String outputFolderPath) {
        try {
            File latestJsonFile = getLatestJsonFile(reportsFolderPath);
            if (latestJsonFile == null) {
                System.err.println("No se encontró ningún archivo JSON en: " + reportsFolderPath);
                return;
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String outputPdfPath = outputFolderPath + "/ReportePruebas_" + timestamp + ".pdf";

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(latestJsonFile);

            // Configurar el documento PDF
            PdfWriter writer = new PdfWriter(outputPdfPath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);

            // 1. Agregar logo
            Image logo = new Image(ImageDataFactory.create("src/test/resources/reports/Logo_BanCoppel.png"));
            logo.setWidth(200);
            logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(logo);

            // 2. Título del reporte
            document.add(new Paragraph("Reporte de Pruebas Automatizadas")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(18));

            // 3. Fecha y hora de ejecución
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            document.add(new Paragraph("Fecha de ejecución: " + dateFormat.format(new Date()))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(12));

            // 4. Contar resultados
            countResults(rootNode);

            // 5. Sección de resumen
            document.add(new Paragraph("Resumen de Resultados")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14));

            // 6. Tabla de resumen con gráfico
            Table summaryTable = new Table(1);
            summaryTable.setWidth(UnitValue.createPercentValue(100));
            summaryTable.setMarginTop(20);
            summaryTable.setMarginBottom(20);

            Cell chartCell = new Cell();
            chartCell.setBorder(null);
            chartCell.setHeight(200);
            chartCell.setNextRenderer(new ChartCellRenderer(chartCell, totalExitosos, totalFallidos));
            summaryTable.addCell(chartCell);

            document.add(summaryTable);
            document.add(new Paragraph("\n"));

            // 7. Procesar features y mostrar tablas
            processFeatures(document, rootNode);

            // 8. Agregar números de página
            int numberOfPages = pdf.getNumberOfPages();
            for (int i = 1; i <= numberOfPages; i++) {
                document.showTextAligned(
                        new Paragraph(String.format("Página %s", i))
                                .setFontColor(ColorConstants.BLACK),
                        559, 20, i, TextAlignment.RIGHT, VerticalAlignment.BOTTOM, 0);
            }

            document.close();
            System.out.println("PDF generado en: " + outputPdfPath);
        } catch (IOException e) {
            System.err.println("Error al generar el PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void countResults(JsonNode rootNode) {
        totalExitosos = 0;
        totalFallidos = 0;

        for (JsonNode featureNode : rootNode) {
            JsonNode elementsNode = featureNode.get("elements");
            for (JsonNode scenarioNode : elementsNode) {
                String status = getScenarioStatus(scenarioNode);
                if (status.equals("EXITOSO")) {
                    totalExitosos++;
                } else {
                    totalFallidos++;
                }
            }
        }
    }

    private void processFeatures(Document document, JsonNode rootNode) {
        DeviceRgb headerColor = new DeviceRgb(135, 206, 235); // Azul claro
        DeviceRgb successColor = new DeviceRgb(0, 128, 0);    // Verde
        DeviceRgb failColor = new DeviceRgb(255, 0, 0);       // Rojo

        for (JsonNode featureNode : rootNode) {
            String featureName = featureNode.get("name").asText();
            // Feature sin color
            document.add(new Paragraph("Feature: " + featureName)
                    .setFontSize(14)
                    .setFontColor(ColorConstants.BLACK));

            Table table = new Table(new float[]{300f, 100f});
            table.setWidth(UnitValue.createPercentValue(100));

            Cell headerCell1 = new Cell().add(new Paragraph("Escenario")
                    .setFontColor(ColorConstants.WHITE));
            Cell headerCell2 = new Cell().add(new Paragraph("Estado")
                    .setFontColor(ColorConstants.WHITE));

            headerCell1.setBackgroundColor(headerColor);
            headerCell2.setBackgroundColor(headerColor);

            table.addHeaderCell(headerCell1);
            table.addHeaderCell(headerCell2);

            JsonNode elementsNode = featureNode.get("elements");
            for (JsonNode scenarioNode : elementsNode) {
                String scenarioName = scenarioNode.get("name").asText();
                String status = getScenarioStatus(scenarioNode);

                // Texto del escenario en negro normal
                Cell scenarioCell = new Cell().add(new Paragraph(scenarioName)
                        .setFontColor(ColorConstants.BLACK));

                // Solo el estado mantiene su color según el resultado
                Cell statusCell = new Cell().add(new Paragraph(status)
                        .setFontColor(status.equals("EXITOSO") ? successColor : failColor));

                table.addCell(scenarioCell);
                table.addCell(statusCell);
            }

            document.add(table);
            document.add(new Paragraph("\n"));
        }
    }
    private static class ChartCellRenderer extends CellRenderer {
        private final int totalExitosos;
        private final int totalFallidos;

        public ChartCellRenderer(Cell cell, int totalExitosos, int totalFallidos) {
            super(cell);
            this.totalExitosos = totalExitosos;
            this.totalFallidos = totalFallidos;
        }

        @Override
        public void draw(DrawContext drawContext) {
            PdfCanvas canvas = drawContext.getCanvas();
            Rectangle rect = getOccupiedAreaBBox();

            float total = totalExitosos + totalFallidos;
            if (total == 0) return;

            float exitososPorcentaje = (totalExitosos / total) * 100;
            float fallidosPorcentaje = (totalFallidos / total) * 100;

            // Centrar el gráfico en la celda
            float centerX = (rect.getLeft() + rect.getRight()) / 2;
            float centerY = rect.getTop() - 80;
            float radius = 50;

            DeviceRgb successColor = new DeviceRgb(0, 128, 0);
            DeviceRgb failColor = new DeviceRgb(255, 0, 0);

            // Dibujar el gráfico circular
            if (totalFallidos == 0) {
                canvas.setFillColor(successColor);
                canvas.circle(centerX, centerY, radius);
                canvas.fill();
            } else if (totalExitosos == 0) {
                canvas.setFillColor(failColor);
                canvas.circle(centerX, centerY, radius);
                canvas.fill();
            } else {
                // Sector exitoso (verde)
                canvas.setFillColor(successColor);
                canvas.moveTo(centerX, centerY);
                float exitososAngle = (exitososPorcentaje / 100) * 360;
                canvas.arc(centerX - radius, centerY - radius, centerX + radius, centerY + radius, 0, exitososAngle);
                canvas.lineTo(centerX, centerY);
                canvas.fill();

                // Sector fallidos (rojo)
                canvas.setFillColor(failColor);
                canvas.moveTo(centerX, centerY);
                canvas.arc(centerX - radius, centerY - radius, centerX + radius, centerY + radius, exitososAngle, 360);
                canvas.lineTo(centerX, centerY);
                canvas.fill();
            }

            // Agregar leyendas debajo del gráfico
            Paragraph exitososLegend = new Paragraph()
                    .add(new Text("Exitosos: ").setFontColor(ColorConstants.BLACK))
                    .add(new Text(String.format("%.1f%%", exitososPorcentaje)).setFontColor(successColor));

            Paragraph fallidosLegend = new Paragraph()
                    .add(new Text("Fallidos: ").setFontColor(ColorConstants.BLACK))
                    .add(new Text(String.format("%.1f%%", fallidosPorcentaje)).setFontColor(failColor));

            // Posicionar leyendas debajo del gráfico
            new Canvas(canvas, rect)
                    .showTextAligned(exitososLegend, centerX, centerY - radius - 20, TextAlignment.CENTER)
                    .showTextAligned(fallidosLegend, centerX, centerY - radius - 40, TextAlignment.CENTER);
        }
    }

    private String getScenarioStatus(JsonNode scenarioNode) {
        JsonNode stepsNode = scenarioNode.get("steps");
        for (JsonNode stepNode : stepsNode) {
            JsonNode resultNode = stepNode.get("result");
            if (resultNode != null) {
                String status = resultNode.get("status").asText();
                if ("failed".equalsIgnoreCase(status)) {
                    return "FALLÓ";
                }
            }
        }
        return "EXITOSO";
    }

    private File getLatestJsonFile(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            return null;
        }

        Optional<File> latestFile = Arrays.stream(folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json")))
                .max((file1, file2) -> Long.compare(file1.lastModified(), file2.lastModified()));

        return latestFile.orElse(null);
    }
}