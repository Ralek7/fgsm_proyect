package runner;

import utils.PdfReportGenerator;
import io.cucumber.core.cli.Main;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TestExecutorWithReport {
    public static void main(String[] args) {
        try {
            String reportsFolderPath = "target/cucumber-reports";
            String outputFolderPath = "target/cucumber-reports";

            // 1. Ejecutar los tests
            System.out.println("Ejecutando pruebas...");

            // Obtener los argumentos de cucumber
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String reportDir = "target/cucumber-reports/report_" + timestamp;

            String[] cucumberArgs = new String[]{
                    "-p", "pretty",
                    "-p", "html:" + reportDir + ".html",
                    "-p", "json:" + reportDir + ".json",
                    "-g", "steps",
                    "src/test/java/features"
            };

            // Ejecutar Cucumber y esperar el resultado
            int result = Main.run(cucumberArgs);

            if (result != 0) {
                throw new RuntimeException("Las pruebas Cucumber fallaron con código de salida: " + result);
            }

            // Esperar un momento para asegurar que el archivo JSON se haya escrito completamente
            TimeUnit.SECONDS.sleep(2);

            // 2. Generar el reporte en PDF
            System.out.println("Generando reporte en PDF...");
            PdfReportGenerator pdfGenerator = new PdfReportGenerator();
            pdfGenerator.generatePdfFromLatestJson(reportsFolderPath, outputFolderPath);

            System.out.println("Reporte PDF generado con éxito en: " + outputFolderPath);

        } catch (Exception e) {
            System.err.println("Ocurrió un error durante la ejecución o la generación del reporte.");
            e.printStackTrace();
            System.exit(1);
        }
    }
}