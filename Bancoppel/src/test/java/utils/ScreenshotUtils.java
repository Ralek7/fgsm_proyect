package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

public class ScreenshotUtils {
    private static final AtomicReference<String> executionFolderName = new AtomicReference<>(null);
    private WebDriver driver;

    // Constructor
    public ScreenshotUtils(WebDriver driver) {
        this.driver = driver;
        createExecutionFolder();
    }

    // Crear la carpeta raíz para la ejecución si no existe
    private void createExecutionFolder() {
        if (executionFolderName.get() == null) {
            synchronized (executionFolderName) {
                if (executionFolderName.get() == null) {
                    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String folderName = "screenshots/Execution_" + timestamp;
                    File folder = new File(folderName);
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    executionFolderName.set(folderName);
                }
            }
        }
    }

    // Obtener la carpeta raíz de la ejecución
    public String getExecutionFolderName() {
        return executionFolderName.get();
    }

    // Método para capturar pantalla cuando el elemento esté listo
    public void takeScreenshotWhenElementReady(By locator, String fileName, String scenarioName) {
        try {
            // Crear subcarpeta específica para cada escenario
            String scenarioFolderName = executionFolderName.get() + "/" + scenarioName + "_TestScreenshots";
            File scenarioFolder = new File(scenarioFolderName);
            if (!scenarioFolder.exists()) {
                scenarioFolder.mkdirs();
            }

            // Esperar hasta que el elemento sea interactuable
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));

            // Nombre del archivo con timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fullFileName = fileName + "_" + timestamp + ".png";

            // Tomar la captura de pantalla
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            File destination = new File(scenarioFolderName + "/" + fullFileName);
            FileUtils.copyFile(source, destination);

            System.out.println("Captura de pantalla guardada en: " + destination.getAbsolutePath());
        } catch (TimeoutException e) {
            System.out.println("El elemento no estuvo listo a tiempo: " + locator);
        } catch (IOException e) {
            System.out.println("No se pudo tomar la captura de pantalla.");
        }
    }

    // Método para capturar pantalla completa usando Robot
    public void captureFullScreenWithRobot(String fileName, String scenarioName) {
        try {
            // Crear subcarpeta específica para cada escenario
            String scenarioFolderName = executionFolderName.get() + "/" + scenarioName + "_TestScreenshots";
            File scenarioFolder = new File(scenarioFolderName);
            if (!scenarioFolder.exists()) {
                scenarioFolder.mkdirs();
            }

            // Crear un objeto Robot para capturar la pantalla
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenCapture = robot.createScreenCapture(screenRect);

            // Nombre del archivo con timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fullFileName = fileName + "_" + timestamp + ".png";

            // Guardar la captura de pantalla
            File outputFile = new File(scenarioFolderName + "/" + fullFileName);
            ImageIO.write(screenCapture, "png", outputFile);

            System.out.println("Captura de pantalla (Robot) guardada en: " + outputFile.getAbsolutePath());
        } catch (AWTException e) {
            System.out.println("Error al crear el objeto Robot: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error al guardar la captura de pantalla (Robot): " + e.getMessage());
        }
    }
}
