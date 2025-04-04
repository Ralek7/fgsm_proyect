package steps;

import io.cucumber.java.Before;
import org.junit.Assert;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.LoanSimulationPage;
import io.cucumber.java.en.*;
import utils.ElementUtils;
import utils.ScreenshotUtils;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import utils.ExcelUtils;
import utils.PDFReportUtils;
import java.time.Instant;
import java.util.Map;


public class LoanSimulatorSteps {
    WebDriver driver;
    LoanSimulationPage loanSimulationPage;
    ElementUtils elementUtils;
    ScreenshotUtils screenshotUtils;
    PDFReportUtils pdfReportUtils;
    private Instant startTime;
    // Map para almacenar los datos del Excel
    private Map<String, String> testData;

    @Before
    public void setUp(Scenario scenario) {
        startTime = Instant.now(); // Capturar hora de inicio
        // Cargar datos del Excel
        String excelPath = "src/test/resources/test_data/datos_prestamo.xlsx";
        testData = ExcelUtils.readExcel(excelPath);

        ChromeOptions options = new ChromeOptions();
        /*options.addArguments("--headless");
        options.addArguments("--disable-gpu");*/
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        loanSimulationPage = new LoanSimulationPage(driver);
        elementUtils = new ElementUtils(driver);
        screenshotUtils = new ScreenshotUtils(driver);

        // Inicializa PDFReportUtils pasando el directorio donde se guardarán las capturas de pantalla
        String executionFolder = screenshotUtils.getExecutionFolderName();
        pdfReportUtils = new PDFReportUtils(executionFolder); // Asignamos el folder de ejecución
    }



    @Given("Acceder a la página de Bancoppel")
    public void accederALaPáginaDeBancoppel() {
        driver.get("https://www.bancoppel.com/");
        //driver.navigate().refresh();
        loanSimulationPage = new LoanSimulationPage(driver);
        elementUtils = new ElementUtils(driver);
        screenshotUtils = new ScreenshotUtils(driver);
        // Verificar y recargar la página si aparece "Access denied" (hasta 3 intentos)
        loanSimulationPage.checkAndReloadIfAccessDenied(3);
    }


// ------>   Scenario: Simular un préstamo de $20,000 a 12 meses

    @When("Navegar a la opción Préstamo Personal Bancoppel")
    public void navegarALaOpciónPréstamoPersonalBancoppel() {
        screenshotUtils.takeScreenshotWhenElementReady(loanSimulationPage.getPrestamosButtonLocator(),
                "pagina_bancoppel", "Simular un préstamo de $20,000 a 12 meses"); // Captura al acceder
        loanSimulationPage.clickClosePopUp();
        loanSimulationPage.clickPrestamos();
        loanSimulationPage.clickPrestamoPersonal();
    }

    @And("Seleccionar la opción Simulador e ingresar {string} en monto y {string} en plazo, seleccionar Calcular")
    public void seleccionarLaOpciónSimuladorEIngresarEnMontoYEnPlazoSeleccionarCalcular(String montoKey, String plazoKey) {
        // Obtener valores desde el Excel
        String monto = testData.get(montoKey);
        String plazo = testData.get(plazoKey);

        screenshotUtils.takeScreenshotWhenElementReady(loanSimulationPage.getSimuladorButtonLocator(),
                "prestamo_personal", "Simular un préstamo de $20,000 a 12 meses"); // Captura después de seleccionar
        loanSimulationPage.clickSimulador();
        loanSimulationPage.sendMonto(monto);
        loanSimulationPage.selectLoanTerm(plazo);
        loanSimulationPage.clickCalcular();
    }

    @Then("muestra la cotización del préstamo")
    public void muestraLaCotizaciónDelPréstamo() {
        String result = loanSimulationPage.getResult();
        System.out.println("Cotización: " + result);
        screenshotUtils.takeScreenshotWhenElementReady(loanSimulationPage.getResultTextLocator(),
                "resultado_calculado", "Simular un préstamo de $20,000 a 12 meses"); // Captura del resultado
    }


// ------>   Scenario: Simular un préstamo de $10,000 a 12 meses

    @When("Navegar a la opción Préstamo Digital Bancoppel")
    public void navegarALaOpciónPréstamoDigitalBancoppel() {
        loanSimulationPage.clickClosePopUp();
        loanSimulationPage.clickPrestamos();
        screenshotUtils.takeScreenshotWhenElementReady(loanSimulationPage.getPrestamoDigitalButton(),
                "pagina_bancoppel", "Simular un préstamo de $10,000 a 12 meses");
        loanSimulationPage.clickPrestamoDigital();
    }

    @And("PD, Seleccionar la opción Simulador e ingresar {string} en monto y {string} en plazo, seleccionar Calcular")
    public void pdSeleccionarLaOpciónSimuladorEIngresarEnMontoYEnPlazoSeleccionarCalcular(String montoKey, String plazoKey) {
        // Obtener valores desde el Excel
        String monto = testData.get(montoKey);
        String plazo = testData.get(plazoKey);

        screenshotUtils.takeScreenshotWhenElementReady(loanSimulationPage.getSimuladorButtonLocator(),
                "prestamo_digital", "Simular un préstamo de $10,000 a 12 meses"); // Captura después de seleccionar
        loanSimulationPage.clickSimulador();
        loanSimulationPage.sendMonto(monto);
        loanSimulationPage.selectLoanTerm(plazo);
        loanSimulationPage.clickCalcular();
    }

    @Then("PD, muestra la cotización del préstamo")
    public void pdMuestraLaCotizaciónDelPréstamo() {
        String result = loanSimulationPage.getResult();
        System.out.println("Cotización: " + result);
        screenshotUtils.takeScreenshotWhenElementReady(loanSimulationPage.getResultTextLocator(),
                "resultado_calculado", "Simular un préstamo de $10,000 a 12 meses"); // Captura del resultado
    }


// ------>  Scenario: Simular un préstamo con $1 menos del límite inferior

    @When("LI, Navegar a la opción Préstamo Personal Bancoppel")
    public void liNavegarALaOpciónPréstamoPersonalBancoppel() {
        screenshotUtils.takeScreenshotWhenElementReady(loanSimulationPage.getPrestamosButtonLocator(),
                "pagina_bancoppel", "Simular un préstamo con $1 menos del límite inferior"); // Captura al acceder
        loanSimulationPage.clickClosePopUp();
        loanSimulationPage.clickPrestamos();
        loanSimulationPage.clickPrestamoPersonal();
    }

    @And("LI, Seleccionar la opción Simulador e ingresar {string} en monto y {string} en plazo, seleccionar Calcular")
    public void liSeleccionarLaOpciónSimuladorEIngresarEnMontoYEnPlazoSeleccionarCalcular(String montoKey, String plazoKey) {
        // Obtener valores desde el Excel
        String monto = testData.get(montoKey);
        String plazo = testData.get(plazoKey);

        loanSimulationPage.clickSimulador();
        screenshotUtils.takeScreenshotWhenElementReady(loanSimulationPage.getSimuladorButtonLocator(),
                "prestamo_personal", "Simular un préstamo con $1 menos del límite inferior"); // Captura después de seleccionar
        loanSimulationPage.sendMonto(monto);
        loanSimulationPage.selectLoanTerm(plazo);
        loanSimulationPage.clickCalcular();
    }

    @Then("LI, muestra mensaje de error: {string}")
    public void liMuestraMensajeDeError(String expectedMessageKey) {
        String expectedMessage = testData.get(expectedMessageKey);
        String actualMessage = loanSimulationPage.clickCalcularAndHandleAlert("Simular un préstamo con $1 menos del límite inferior");

        Assert.assertNotNull("Se esperaba una alerta, pero no se detectó ninguna.", actualMessage);
        Assert.assertEquals("El mensaje de alerta no coincide con el esperado.", expectedMessage, actualMessage);
        System.out.println("Mensaje de alerta validado: " + actualMessage);
    }


// ------>  Scenario: Simular un préstamo con $1 más del límite superior

    @When("LS,Navegar a la opción Préstamo Personal Bancoppel")
    public void lsNavegarALaOpciónPréstamoPersonalBancoppel() {
        screenshotUtils.takeScreenshotWhenElementReady(loanSimulationPage.getPrestamosButtonLocator(),
                "pagina_bancoppel", "Simular un préstamo con $1 más del límite superior"); // Captura al acceder
        loanSimulationPage.clickClosePopUp();
        loanSimulationPage.clickPrestamos();
        loanSimulationPage.clickPrestamoPersonal();
    }

    @And("LS, Seleccionar la opción Simulador e ingresar {string} en monto y {string} en plazo, seleccionar Calcular")
    public void lsSeleccionarLaOpciónSimuladorEIngresarEnMontoYEnPlazoSeleccionarCalcular(String montoKey, String plazoKey) {
        // Obtener valores desde el Excel
        String monto = testData.get(montoKey);
        String plazo = testData.get(plazoKey);

        loanSimulationPage.clickSimulador();
        screenshotUtils.takeScreenshotWhenElementReady(loanSimulationPage.getSimuladorButtonLocator(),
                "prestamo_personal", "Simular un préstamo con $1 más del límite superior"); // Captura después de seleccionar
        loanSimulationPage.sendMonto(monto);
        loanSimulationPage.selectLoanTerm(plazo);
        loanSimulationPage.clickCalcular();
    }

    @Then("LS, muestra mensaje de error: {string}")
    public void lsMuestraMensajeDeError(String expectedMessageKey) {
        String expectedMessage = testData.get(expectedMessageKey);
        String actualMessage = loanSimulationPage.clickCalcularAndHandleAlert("Simular un préstamo con $1 más del límite superior");

        Assert.assertNotNull("Se esperaba una alerta, pero no se detectó ninguna.", actualMessage);
        Assert.assertEquals("El mensaje de alerta no coincide con el esperado.", expectedMessage, actualMessage);
        System.out.println("Mensaje de alerta validado: " + actualMessage);
    }


// ------>


    @After
    public void tearDown(Scenario scenario) {
        try {
            // Calcular duración
            long duration = Instant.now().getEpochSecond() - startTime.getEpochSecond();
            // Obtener el estado del escenario (si pasó o falló)
            String status = scenario.getStatus().toString();
            // Generar PDF con el contenido necesario
            pdfReportUtils.generatePDFReport(scenario.getName(), status, String.valueOf(duration), scenario.getName());

        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }


}

