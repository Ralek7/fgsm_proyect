package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.ElementUtils;

public class SavingsAndInvestmentsPage {
    private WebDriver driver;
    private ElementUtils elementUtils;

    // Locators
    private By closePopUpButton = By.xpath("(//button[contains(@aria-label,'Close')])[2]");
    private By ahorroButton = By.xpath("//span[contains(.,'Ahorro e Inversiones')]");
    private By inversionCrecienteButton = By.xpath("(//a[contains(.,'Inversión Creciente')])[1]");
    private By simuladorButton = By.xpath("//*[@id='contenido']/div[1]/ul/li[4]/a");
    private By montoInput = By.xpath("//*[@id=\'cantidadInvertir\']");
    private By calcularButton = By.xpath("//*[@id='btn_calcular']/img");
    private By resultText = By.xpath("//*[@id=\'montoFinalRes\']");

    // Locator para el mensaje "Access denied"
    private By accessDeniedMessage = By.className("error-title");

    // ID del iframe que contiene el mensaje "Access denied"
    private String iframeId = "main-iframe";

    // Constructor
    public SavingsAndInvestmentsPage(WebDriver driver) {
        this.driver = driver;
        this.elementUtils = new ElementUtils(driver);
    }

    // Método para verificar y recargar la página si aparece "Access denied"
    public void checkAndReloadIfAccessDenied(int maxAttempts) {
        elementUtils.checkAndReloadIfAccessDenied(accessDeniedMessage, iframeId, maxAttempts);
    }

    // Métodos para usar ElementUtils
    public void clickClosePopUp() {
        elementUtils.clickElement(closePopUpButton);
    }

    public void clickAhorro(){
        elementUtils.clickElement(ahorroButton);
    }

    public void clickInversionCreciente(){
        elementUtils.clickElement(inversionCrecienteButton);
    }

    public void clickSimulador() {
        elementUtils.clickElement(simuladorButton);
    }

    public void sendMonto(String amount) {
        elementUtils.sendText(montoInput, amount);
    }

    public void clickCalcular() {
        WebElement buttonCalcular = elementUtils.waitForElement(calcularButton);
        elementUtils.scrollToElement(buttonCalcular);
        elementUtils.clickElement(calcularButton);
    }

    public String getResult() {
        WebElement resultElement = elementUtils.waitForElement(resultText);
        elementUtils.scrollToElementTop(resultElement); // Coloca el elemento en la parte superior de la pantalla
        return resultElement.getText();
    }

    // Métodos para exponer los locators (necesarios para capturas de pantalla)
    public By getAhorroButtonLocator() {
        return ahorroButton;
    }

    public By getSimuladorButtonLocator() {
        return simuladorButton;
    }

    public By getResultTextLocator() {
        return resultText;
    }

}
