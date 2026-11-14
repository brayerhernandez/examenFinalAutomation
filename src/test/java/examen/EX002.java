package examen;

import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * EX002 - RF-002: Compra de productos sin autenticación (Guest Checkout)
 * Evaluación Final QA Automation
 */
public class EX002 {

    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "https://teststore.automationtesting.co.uk/index.php";

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Test
    @DisplayName("EX002 - Compra de producto como invitado (sin autenticacion previa)")
    public void testCompraSinAutenticacionGuestCheckout() {

        // =========================================================================
        // 1. Ingresar al sitio web
        // =========================================================================
        System.out.println("Paso 1: Ingresando al sitio web...");
        driver.get(BASE_URL);

        // =========================================================================
        // 2. Navegar por el catálogo de productos
        // =========================================================================
        System.out.println("Paso 2: Navegando al catalogo de productos (Accessories)...");
        WebElement categoriaAccesorios = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("#category-6 > a")));
        categoriaAccesorios.click();

        // =========================================================================
        // 3. Seleccionar uno o más productos
        // =========================================================================
        System.out.println("Paso 3: Seleccionando un producto del catalogo...");
        WebElement primerProducto = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector(".product-description a")));
        String nombreProductoCatalogo = primerProducto.getText().trim();
        System.out.println("Producto seleccionado: " + nombreProductoCatalogo);
        primerProducto.click();

        // =========================================================================
        // 4. Agregar los productos al carrito de compras
        // =========================================================================
        System.out.println("Paso 4: Agregando el producto al carrito...");
        WebElement botonAgregarCarrito = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("button.add-to-cart")));
        botonAgregarCarrito.click();

        // Esperar el modal de confirmación
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("blockcart-modal")));

        // =========================================================================
        // 5. Acceder al carrito y validar los productos agregados
        // =========================================================================
        System.out.println("Paso 5: Accediendo al carrito de compras...");
        WebElement botonIrAlCarrito = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("#blockcart-modal div.cart-content-btn a.btn-primary")));
        botonIrAlCarrito.click();

        // VALIDACIÓN: Verificar que los productos seleccionados se encuentren en el carrito
        System.out.println("Validacion: Verificando que el producto seleccionado se encuentre en el carrito...");
        List<WebElement> productosEnCarrito = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".cart-item .product-line-info a")));

        boolean productoEncontrado = false;
        for (WebElement item : productosEnCarrito) {
            System.out.println("Item presente en el carrito: " + item.getText().trim());
            if (item.getText().trim().equalsIgnoreCase(nombreProductoCatalogo)) {
                productoEncontrado = true;
                break;
            }
        }

        Assertions.assertTrue(productoEncontrado,
                "Error: El producto seleccionado (" + nombreProductoCatalogo +
                        ") no fue encontrado entre los productos del carrito.");
        System.out.println("Producto encontrado y validado en el carrito con exito: " + nombreProductoCatalogo);

        // =========================================================================
        // 6. Iniciar el checkout sin autenticarse
        // =========================================================================
        System.out.println("Paso 6: Iniciando checkout sin autenticarse (modo invitado)...");
        WebElement botonProceedCheckout = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector(".cart-detailed-actions a.btn-primary")));
        botonProceedCheckout.click();

        // =========================================================================
        // 7. Completar la información requerida utilizando datos ficticios
        // =========================================================================
        System.out.println("Paso 7: Completando datos personales ficticios como invitado...");

        // 7.1 Seleccionar título social (Mr.)
        List<WebElement> radiosGenero = driver.findElements(By.id("field-id_gender-1"));
        if (!radiosGenero.isEmpty()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radiosGenero.get(0));
        }

        // 7.2 Llenar datos personales ficticios
        WebElement inputNombre = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("field-firstname")));
        inputNombre.clear();
        inputNombre.sendKeys("Juan");

        WebElement inputApellido = driver.findElement(By.id("field-lastname"));
        inputApellido.clear();
        inputApellido.sendKeys("Perez");

        // Generar un correo ficticio unico para evitar colisiones
        String emailFicticio = "invitado." + System.currentTimeMillis() + "@correo-prueba.com";
        WebElement inputEmail = driver.findElement(By.id("field-email"));
        inputEmail.clear();
        inputEmail.sendKeys(emailFicticio);
        System.out.println("Correo ficticio utilizado: " + emailFicticio);

        // 7.3 Aceptar terminos de privacidad requeridos para invitados
        List<WebElement> checkPrivacidad = driver.findElements(By.name("customer_privacy"));
        if (!checkPrivacidad.isEmpty() && !checkPrivacidad.get(0).isSelected()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkPrivacidad.get(0));
        }

        List<WebElement> checkGdpr = driver.findElements(By.name("psgdpr"));
        if (!checkGdpr.isEmpty() && !checkGdpr.get(0).isSelected()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkGdpr.get(0));
        }

        // Continuar a la seccion de direcciones
        WebElement botonContinuarDatos = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("continue")));
        botonContinuarDatos.click();

        // 7.4 Completar direccion ficticia
        System.out.println("Completando formulario de direccion ficticia...");
        WebElement inputDireccion = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("field-address1")));
        inputDireccion.sendKeys("Avenida Central 789");

        driver.findElement(By.id("field-city")).sendKeys("Miami");

        List<WebElement> selectorEstado = driver.findElements(By.id("field-id_state"));
        if (!selectorEstado.isEmpty() && selectorEstado.get(0).isDisplayed()) {
            Select estadoSelect = new Select(selectorEstado.get(0));
            estadoSelect.selectByIndex(1);
        }

        driver.findElement(By.id("field-postcode")).sendKeys("33101");

        // Continuar al metodo de envio
        WebElement botonConfirmarDireccion = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("confirm-addresses")));
        botonConfirmarDireccion.click();

        // 7.5 Metodo de envio
        System.out.println("Confirmando metodo de envio...");
        List<WebElement> opcionesEnvio = driver.findElements(By.cssSelector(".delivery-option input[type='radio']"));
        if (!opcionesEnvio.isEmpty() && !opcionesEnvio.get(0).isSelected()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", opcionesEnvio.get(0));
        }

        WebElement botonConfirmarEnvio = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("confirmDeliveryOption")));
        botonConfirmarEnvio.click();

        // 7.6 Metodo de pago
        System.out.println("Seleccionando metodo de pago...");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("checkout-payment-step")));

        WebElement opcionPago = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("payment-option-1")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", opcionPago);

        // Aceptar terminos y condiciones
        WebElement checkTerminos = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[id*='conditions_to_approve']")));
        if (!checkTerminos.isSelected()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkTerminos);
        }

        // =========================================================================
        // 8. Continuar hasta la pantalla final de confirmación
        // 9. Confirmar la orden
        // =========================================================================
        System.out.println("Paso 8 y 9: Confirmando la orden...");
        WebElement botonConfirmarOrden = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("#payment-confirmation button")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botonConfirmarOrden);

        // =========================================================================
        // VALIDACIÓN FINAL: Verificar mensaje "Your order is confirmed"
        // =========================================================================
        System.out.println("Validacion Final: Verificando mensaje de confirmacion de orden...");
        WebElement mensajeConfirmacion = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#content-hook_order_confirmation h3")));

        String textoConfirmacion = mensajeConfirmacion.getText().trim();
        System.out.println("Texto obtenido de la confirmacion: " + textoConfirmacion);

        Assertions.assertTrue(textoConfirmacion.toLowerCase().contains("your order is confirmed"),
                "Error: No se mostro el mensaje de confirmacion esperado. Se obtuvo: " + textoConfirmacion);

        System.out.println("Prueba EX002 (Guest Checkout) ejecutada y validada con EXITO.");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            System.out.println("Cerrando navegador...");
            driver.quit();
        }
    }
}
