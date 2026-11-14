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
 * EX001 - RF-001: Compra de productos con usuario autenticado
 * Evaluación Final QA Automation
 */
public class EX001 {

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
    @DisplayName("EX001 - Compra completa de producto con usuario autenticado")
    public void testCompraUsuarioAutenticado() {

        // =========================================================================
        // 1. Ingresar al sitio web
        // =========================================================================
        System.out.println("Paso 1: Ingresando al sitio web...");
        driver.get(BASE_URL);

        // =========================================================================
        // 2. Autenticarse utilizando un usuario y contraseña válidos
        // =========================================================================
        System.out.println("Paso 2: Iniciando sesion con credenciales validas...");
        WebElement enlaceSignIn = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("#_desktop_user_info a")));
        enlaceSignIn.click();

        // Esperar a que cargue el formulario de login e ingresar credenciales
        WebElement inputEmail = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("field-email")));
        inputEmail.clear();
        inputEmail.sendKeys("brayer.hernandez@gmail.com");

        WebElement inputPassword = driver.findElement(By.id("field-password"));
        inputPassword.clear();
        inputPassword.sendKeys("Abcd.1234");

        WebElement botonLogin = driver.findElement(By.id("submit-login"));
        botonLogin.click();

        // VALIDACIÓN 1: Verificar que el usuario haya iniciado sesión correctamente
        System.out.println("Validacion 1: Verificando que el usuario inicio sesion correctamente...");
        WebElement enlaceSignOut = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a.logout")));
        Assertions.assertTrue(enlaceSignOut.isDisplayed(),
                "Error: El usuario no inicio sesion correctamente (no se encontro el boton Sign out).");

        // También validamos que el nombre de usuario sea visible en la cabecera
        WebElement nombreUsuario = driver.findElement(By.cssSelector("#_desktop_user_info a.account"));
        Assertions.assertTrue(nombreUsuario.isDisplayed(),
                "Error: El nombre de la cuenta de usuario no es visible.");
        System.out.println("Sesion iniciada con exito por: " + nombreUsuario.getText().trim());

        // =========================================================================
        // 3. Navegar por el catálogo de productos
        // =========================================================================
        System.out.println("Paso 3: Navegando al catalogo de productos (Clothes)...");
        WebElement categoriaRopa = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("#category-3 > a")));
        categoriaRopa.click();

        // =========================================================================
        // 4. Seleccionar uno o más productos
        // =========================================================================
        System.out.println("Paso 4: Seleccionando el primer producto del catalogo...");
        WebElement primerProducto = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector(".product-description a")));
        String nombreProductoCatalogo = primerProducto.getText().trim();
        System.out.println("Producto seleccionado: " + nombreProductoCatalogo);
        primerProducto.click();

        // =========================================================================
        // 5. Agregar los productos al carrito de compras
        // =========================================================================
        System.out.println("Paso 5: Agregando el producto al carrito...");
        WebElement botonAgregarCarrito = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("button.add-to-cart")));
        botonAgregarCarrito.click();

        // Esperar modal de confirmación de agregado
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("blockcart-modal")));

        // =========================================================================
        // 6. Acceder al carrito
        // =========================================================================
        System.out.println("Paso 6: Accediendo al carrito de compras...");
        WebElement botonIrAlCarrito = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("#blockcart-modal div.cart-content-btn a.btn-primary")));
        botonIrAlCarrito.click();

        // VALIDACIÓN 2: Verificar que los productos se encuentren en el carrito
        System.out.println("Validacion 2: Verificando que el producto este en el carrito...");
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
        System.out.println("Producto en carrito verificado exitosamente: " + nombreProductoCatalogo);

        // =========================================================================
        // 7. Iniciar el proceso de checkout
        // =========================================================================
        System.out.println("Paso 7: Iniciando el proceso de checkout...");
        WebElement botonProceedCheckout = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector(".cart-detailed-actions a.btn-primary")));
        botonProceedCheckout.click();

        // =========================================================================
        // 8. Completar la información requerida durante el proceso de compra
        // =========================================================================
        System.out.println("Paso 8: Completando informacion requerida durante el proceso de compra...");

        // 8.1 Direccion: Si el formulario de direccion esta visible, llenarlo; si ya existe direccion, confirmar.
        List<WebElement> camposDireccion = driver.findElements(By.id("field-address1"));
        if (!camposDireccion.isEmpty() && camposDireccion.get(0).isDisplayed()) {
            System.out.println("Completando formulario de direccion...");
            camposDireccion.get(0).sendKeys("Main Street 123");
            driver.findElement(By.id("field-city")).sendKeys("Miami");

            // Si hay selector de Estado
            List<WebElement> selectorEstado = driver.findElements(By.id("field-id_state"));
            if (!selectorEstado.isEmpty() && selectorEstado.get(0).isDisplayed()) {
                Select estadoSelect = new Select(selectorEstado.get(0));
                estadoSelect.selectByIndex(1);
            }

            driver.findElement(By.id("field-postcode")).sendKeys("33101");

            // Continuar direccion
            WebElement botonConfirmarDireccion = wait.until(
                    ExpectedConditions.elementToBeClickable(By.name("confirm-addresses")));
            botonConfirmarDireccion.click();
        } else {
            // Ya hay direccion registrada, hacer click en continuar direccion
            WebElement botonConfirmarDireccion = wait.until(
                    ExpectedConditions.elementToBeClickable(By.name("confirm-addresses")));
            botonConfirmarDireccion.click();
        }

        // 8.2 Metodo de Envio (Shipping Method)
        System.out.println("Confirmando metodo de envio...");
        List<WebElement> opcionesEnvio = driver.findElements(By.cssSelector(".delivery-option input[type='radio']"));
        if (!opcionesEnvio.isEmpty() && !opcionesEnvio.get(0).isSelected()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", opcionesEnvio.get(0));
        }

        WebElement botonConfirmarEnvio = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("confirmDeliveryOption")));
        botonConfirmarEnvio.click();

        // 8.3 Metodo de Pago (Payment)
        System.out.println("Seleccionando metodo de pago...");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("checkout-payment-step")));

        // Seleccionar opcion de pago (Pay by Check)
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
        // 9. Continuar hasta la pantalla final de confirmacion
        // 10. Confirmar la orden
        // =========================================================================
        System.out.println("Paso 9 y 10: Confirmando la orden...");
        WebElement botonConfirmarOrden = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("#payment-confirmation button")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botonConfirmarOrden);

        // =========================================================================
        // VALIDACIÓN 3: Verificar que se muestre el mensaje "Your order is confirmed"
        // =========================================================================
        System.out.println("Validacion 3: Verificando mensaje de orden confirmada...");
        WebElement mensajeConfirmacion = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#content-hook_order_confirmation h3")));

        String textoConfirmacion = mensajeConfirmacion.getText().trim();
        System.out.println("Texto obtenido de la confirmacion: " + textoConfirmacion);

        Assertions.assertTrue(textoConfirmacion.toLowerCase().contains("your order is confirmed"),
                "Error: No se mostro el mensaje de confirmacion esperado. Se obtuvo: " + textoConfirmacion);

        System.out.println("Prueba EX001 ejecutada y validada con EXITO.");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            System.out.println("Cerrando navegador...");
            driver.quit();
        }
    }
}
