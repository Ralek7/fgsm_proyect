package pages;

import org.openqa.selenium.*;
import utils.ElementUtils;

public class LoanSimulationPage {
    private WebDriver driver;
    private ElementUtils elementUtils;

    // Locators
    private By closePopUpButton = By.xpath("(//button[contains(@aria-label,'Close')])[2]");
    private By prestamosButton = By.xpath("//span[contains(.,'Préstamos')]");
    private By prestamoPersonalButton = By.xpath("//a[contains(.,'Préstamo Personal BanCoppel')]");
    private By simuladorButton = By.xpath("//*[@id='contenido']/div[1]/ul/li[4]/a");
    private By montoInput = By.xpath("//*[@id='cantidad']");
    private By termDropdown = By.id("select-plazo");
    private By calcularButton = By.xpath("//*[@id='btn_calcular']/img");
    private By resultText = By.xpath("//*[@id='content4']/div/div/div[5]/span");
    private By prestamoDigitalButton = By.xpath("//a[contains(@data-name,'Préstamo Digital BanCoppel')]");

    // Locator para el mensaje "Access denied"
    private By accessDeniedMessage = By.className("error-title");

    // ID del iframe que contiene el mensaje "Access denied"
    private String iframeId = "main-iframe";

    // Constructor
    public LoanSimulationPage(WebDriver driver) {
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

    public void clickPrestamos() {
        elementUtils.clickElement(prestamosButton);
    }

    public void clickPrestamoPersonal() {
        elementUtils.clickElement(prestamoPersonalButton);
    }

    public void clickPrestamoDigital() {
        elementUtils.clickElement(prestamoDigitalButton);
    }

    public void clickSimulador() {
        elementUtils.clickElement(simuladorButton);
    }

    public void sendMonto(String amount) {
        elementUtils.sendText(montoInput, amount);
    }

    public void selectLoanTerm(String term) {
        elementUtils.selectFromDropdown(termDropdown, term);
    }

    public void clickCalcular() {
        //WebElement BtnCalcular = elementUtils.waitForElement(calcularButton);
        //elementUtils.scrollToElement(BtnCalcular);
        elementUtils.clickElement(calcularButton);
    }

    public String clickCalcularAndHandleAlert(String scenarioName) {
        return elementUtils.clickElementAndHandleAlert(calcularButton, scenarioName);
    }

    public String getResult() {
        WebElement resultElement = elementUtils.waitForElement(resultText);
        elementUtils.scrollToElementTop(resultElement); // Coloca el elemento en la parte superior de la pantalla
        return resultElement.getText();
    }

    // Métodos para exponer los locators (necesarios para capturas de pantalla)
    public By getPrestamosButtonLocator() {
        return prestamosButton;
    }

    public By getSimuladorButtonLocator() {
        return simuladorButton;
    }

    public By getPrestamoDigitalButton() {
        return prestamoDigitalButton;
    }

    public By getResultTextLocator() {
        return resultText;
    }
}
