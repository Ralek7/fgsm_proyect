package steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.SavingsAndInvestmentsPage;
import utils.ElementUtils;
import utils.ExcelUtils;
import utils.ScreenshotUtils;
import utils.PDFReportUtils;
import java.time.Instant;
import java.util.Map;

public class SavingsAndInvestmentsSteps {
    WebDriver driver;
    SavingsAndInvestmentsPage savingsAndInvestmentsPage;
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
        String excelPath = "src/test/resources/test_data/datos_ahorro_e_inversiones.xlsx";
        testData = ExcelUtils.readExcel(excelPath);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        savingsAndInvestmentsPage = new SavingsAndInvestmentsPage(driver);
        elementUtils = new ElementUtils(driver);
        screenshotUtils = new ScreenshotUtils(driver); // Inicializa ScreenshotUtils

        // Inicializa PDFReportUtils pasando el directorio donde se guardarán las capturas de pantalla
        String executionFolder = screenshotUtils.getExecutionFolderName();
        pdfReportUtils = new PDFReportUtils(executionFolder); // Asignamos el folder de ejecución
    }

    @Given("IC, Acceder a la página de Bancoppel")
    public void accederALaPáginaDeBancoppel() {
        driver.get("https://www.bancoppel.com/");
        savingsAndInvestmentsPage = new SavingsAndInvestmentsPage(driver);
        elementUtils = new ElementUtils(driver);
        screenshotUtils = new ScreenshotUtils(driver); // Inicializa ScreenshotUtils
        // Verificar y recargar la página si aparece "Access denied" (hasta 3 intentos)
        savingsAndInvestmentsPage.checkAndReloadIfAccessDenied(3);
    }

    // ------>   Simular una Inversión Creciente de $50,000

    @When("Navegar a la opción Inversión Creciente")
    public void navegarALaOpciónInversiónCreciente() {
        screenshotUtils.takeScreenshotWhenElementReady(savingsAndInvestmentsPage.getAhorroButtonLocator(),
                "pagina_bancoppel", "Simular una Inversión Creciente de $50,000"); // Captura al acceder
        savingsAndInvestmentsPage.clickClosePopUp();
        savingsAndInvestmentsPage.clickAhorro();
        savingsAndInvestmentsPage.clickInversionCreciente();
    }

    @And("Seleccionar la opción Simulador e ingresar {string}, seleccionar Calcular")
    public void seleccionarLaOpciónSimuladorEIngresarSeleccionarCalcular(String montoKey) {
        // Obtener valores desde el Excel
        String monto = testData.get(montoKey);

        screenshotUtils.takeScreenshotWhenElementReady(savingsAndInvestmentsPage.getSimuladorButtonLocator(),
                "inversion_creciente", "Simular una Inversión Creciente de $50,000"); // Captura después de seleccionar
        savingsAndInvestmentsPage.sendMonto(monto);
        savingsAndInvestmentsPage.clickCalcular();
        try {
            Thread.sleep(1000); // Espera 1 segundo
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        savingsAndInvestmentsPage.clickCalcular();
    }

    @Then("muestra la cotización de la inversión")
    public void muestraLaCotizaciónDeLaInversión() {
        String result = savingsAndInvestmentsPage.getResult();
        System.out.println("Cotización: " + result);
        screenshotUtils.takeScreenshotWhenElementReady(savingsAndInvestmentsPage.getResultTextLocator(),
                "resultado_calculado", "Simular una Inversión Creciente de $50,000"); // Captura del resultado
        driver.quit();

    }


    @After
    public void tearDown(Scenario scenario) {
        try {
            // Calcular duración
            long duration = Instant.now().getEpochSecond() - startTime.getEpochSecond();

            // Obtener la carpeta de ejecución
            String executionFolder = screenshotUtils.getExecutionFolderName();

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
