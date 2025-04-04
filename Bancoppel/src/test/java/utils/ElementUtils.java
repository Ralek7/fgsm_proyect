package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;


public class ElementUtils {
    private WebDriver driver;
    private WebDriverWait wait;
    private ScreenshotUtils screenshotUtils;

    // Constructor
    public ElementUtils(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.screenshotUtils = new ScreenshotUtils(driver);

    }

    // Método para verificar y recargar la página si aparece "Access denied"
    public void checkAndReloadIfAccessDenied(By accessDeniedLocator, String iframeId, int maxAttempts) {
        int attempts = 0;

        while (attempts < maxAttempts) {
            try {
                // Cambiar al iframe que contiene el mensaje "Access denied"
                wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(iframeId));

                // Esperar a que el mensaje "Access denied" aparezca (timeout corto)
                WebElement accessDeniedElement = wait.until(ExpectedConditions.presenceOfElementLocated(accessDeniedLocator));

                if (accessDeniedElement != null) {
                    System.out.println("Access denied detected. Reloading page...");
                    driver.navigate().refresh(); // Recargar la página
                    attempts++;
                }
            } catch (Exception e) {
                // Si no se encuentra el mensaje, salir del bucle
                System.out.println("No access denied message found.");
                break;
            } finally {
                // Volver al contexto principal (fuera del iframe)
                driver.switchTo().defaultContent();
            }
        }

        if (attempts == maxAttempts) {
            System.out.println("Max reload attempts reached. Access denied may persist.");
        }
    }

    // Método de espera explícita
    public WebElement waitForElement(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }


    // Método para resaltar el elemento
    public void highlightElement(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid red'", element);
        } catch (Exception e) {
            System.out.println("No se pudo resaltar el elemento.");
        }
    }

    // Método para quitar el resaltado del elemento
    public void unhighlightElement(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].style.border=''", element);
        } catch (Exception e) {
            System.out.println("No se pudo quitar el marco del elemento.");
        }
    }

    // Método para hacer clic
    public void clickElement(By locator) {
        WebElement element = null;
        try {
            element = waitForElement(locator);
            highlightElement(element);
            element.click();
        } catch (TimeoutException e) {
            System.out.println("El elemento no se cargó a tiempo: " + locator);
        } catch (Exception e) {
            System.out.println("Error al interactuar con el elemento: " + e.getMessage());
        } finally {
            if (element != null) {
                unhighlightElement(element);
            }
        }
    }

    // Método para verificar si hay una alerta presente
    private boolean isAlertPresent(WebDriverWait wait) {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            return true;
        } catch (TimeoutException e) {
            return false; // No hay alerta presente
        }
    }

    // Método para gacer clic y hacer captura de pantalla cuando hay un alert
    public String clickElementAndHandleAlert(By locator, String scenarioName) {
        String alertMessage = null;
        try {
            // Intentar hacer clic en el elemento
            WebElement element = waitForElement(locator);
            element.click();

            // Verificar si aparece una alerta
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
            if (isAlertPresent(wait)) {
                Alert alert = driver.switchTo().alert();
                alertMessage = alert.getText();
                System.out.println("Mensaje de alerta detectado: " + alertMessage);
                // Aceptar la alerta
                alert.accept();
                // Captura de pantalla completa usando Robot
                ScreenshotUtils screenshotUtils = new ScreenshotUtils(driver);
                screenshotUtils.captureFullScreenWithRobot("mensaje_de_alerta", scenarioName);
            }

        } catch (NoAlertPresentException e) {
            System.out.println("No se detectó ninguna alerta.");
        } catch (UnhandledAlertException e) {
            System.out.println("Error inesperado: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error al manejar el clic y la alerta: " + e.getMessage());
        }
        return alertMessage;
    }

    // Método para enviar texto con manejo de excepciones
    public void sendText(By locator, String text) {
        try {
            WebElement element = waitForElement(locator);
            highlightElement(element);
            element.sendKeys(text);
            unhighlightElement(element);
        } catch (TimeoutException e) {
            System.out.println("El campo de texto no se cargó a tiempo: " + locator);
        }
    }

    // Método para seleccionar un valor de un dropdown con manejo de excepciones
    public void selectFromDropdown(By locator, String visibleText) {
        try {
            WebElement dropdown = waitForElement(locator);
            highlightElement(dropdown);
            dropdown.sendKeys(visibleText);
            unhighlightElement(dropdown);
        } catch (TimeoutException e) {
            System.out.println("El dropdown no se cargó a tiempo: " + locator);
        }
    }

    // Método para desplazarse hasta un elemento
    public void scrollToElement(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        } catch (Exception e) {
            System.out.println("No se pudo desplazar hasta el elemento.");
        }
    }

    // Método para desplazar un elemento a la parte superior de la pantalla
    public void scrollToElementTop(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "window.scrollTo(0, arguments[0].getBoundingClientRect().top + window.pageYOffset);",
                    element
            );
        } catch (Exception e) {
            System.out.println("No se pudo desplazar el elemento a la parte superior.");
        }
    }
}