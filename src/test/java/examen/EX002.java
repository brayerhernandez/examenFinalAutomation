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

                System.out.println("Paso 1: Ingresando al sitio web...");
                driver.get(BASE_URL);

                System.out.println("Paso 2: Navegando al catalogo de productos (Accessories)...");
                WebElement categoriaAccesorios = wait.until(
                                ExpectedConditions.elementToBeClickable(By.cssSelector("#category-6 > a")));
                categoriaAccesorios.click();

                System.out.println("Paso 3: Seleccionando un producto del catalogo...");
                WebElement primerProducto = wait.until(
                                ExpectedConditions.elementToBeClickable(By.cssSelector(".product-description a")));
                String nombreProductoCatalogo = primerProducto.getText().trim();
                System.out.println("Producto seleccionado: " + nombreProductoCatalogo);
                primerProducto.click();

                System.out.println("Paso 4: Agregando el producto al carrito...");
                WebElement botonAgregarCarrito = wait.until(
                                ExpectedConditions.elementToBeClickable(By.cssSelector("button.add-to-cart")));
                botonAgregarCarrito.click();

                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("blockcart-modal")));

                System.out.println("Paso 5: Accediendo al carrito de compras...");
                WebElement botonIrAlCarrito = wait.until(
                                ExpectedConditions.elementToBeClickable(
                                                By.cssSelector("#blockcart-modal div.cart-content-btn a.btn-primary")));
                botonIrAlCarrito.click();

                System.out.println(
                                "Validacion: Verificando que el producto seleccionado se encuentre en el carrito...");
                List<WebElement> productosEnCarrito = wait.until(
                                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                                                By.cssSelector(".cart-item .product-line-info a")));

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

                System.out.println("Paso 6: Iniciando checkout sin autenticarse...");
                WebElement botonProceedCheckout = wait.until(
                                ExpectedConditions.elementToBeClickable(
                                                By.cssSelector(".cart-detailed-actions a.btn-primary")));
                botonProceedCheckout.click();

                System.out.println("Paso 7: Completando datos personales ficticios como invitado...");

                List<WebElement> radiosGenero = driver.findElements(By.id("field-id_gender-1"));
                if (!radiosGenero.isEmpty()) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radiosGenero.get(0));
                }

                WebElement inputNombre = wait.until(
                                ExpectedConditions.visibilityOfElementLocated(By.id("field-firstname")));
                inputNombre.clear();
                inputNombre.sendKeys("Juan");

                WebElement inputApellido = driver.findElement(By.id("field-lastname"));
                inputApellido.clear();
                inputApellido.sendKeys("Perez");

                String emailFicticio = "invitado." + System.currentTimeMillis() + "@correo-prueba.com";
                WebElement inputEmail = driver.findElement(By.id("field-email"));
                inputEmail.clear();
                inputEmail.sendKeys(emailFicticio);
                System.out.println("Correo ficticio utilizado: " + emailFicticio);

                List<WebElement> checkPrivacidad = driver.findElements(By.name("customer_privacy"));
                if (!checkPrivacidad.isEmpty() && !checkPrivacidad.get(0).isSelected()) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkPrivacidad.get(0));
                }

                List<WebElement> checkGdpr = driver.findElements(By.name("psgdpr"));
                if (!checkGdpr.isEmpty() && !checkGdpr.get(0).isSelected()) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkGdpr.get(0));
                }

                WebElement botonContinuarDatos = wait.until(
                                ExpectedConditions.elementToBeClickable(By.name("continue")));
                botonContinuarDatos.click();
                System.out.println("Completando formulario de direccion ficticia...");
                WebElement inputDireccion = wait.until(
                                ExpectedConditions.visibilityOfElementLocated(By.id("field-address1")));
                inputDireccion.sendKeys("Armenia, Sonsonate, El Salvador");

                driver.findElement(By.id("field-city")).sendKeys("Sonsonate");

                List<WebElement> selectorEstado = driver.findElements(By.id("field-id_state"));
                if (!selectorEstado.isEmpty() && selectorEstado.get(0).isDisplayed()) {
                        Select estadoSelect = new Select(selectorEstado.get(0));
                        estadoSelect.selectByIndex(1);
                }

                driver.findElement(By.id("field-postcode")).sendKeys("33101");

                WebElement botonConfirmarDireccion = wait.until(
                                ExpectedConditions.elementToBeClickable(By.name("confirm-addresses")));
                botonConfirmarDireccion.click();
                System.out.println("Confirmando metodo de envio...");
                List<WebElement> opcionesEnvio = driver
                                .findElements(By.cssSelector(".delivery-option input[type='radio']"));
                if (!opcionesEnvio.isEmpty() && !opcionesEnvio.get(0).isSelected()) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", opcionesEnvio.get(0));
                }

                WebElement botonConfirmarEnvio = wait.until(
                                ExpectedConditions.elementToBeClickable(By.name("confirmDeliveryOption")));
                botonConfirmarEnvio.click();

                System.out.println("Seleccionando metodo de pago...");
                wait.until(ExpectedConditions.presenceOfElementLocated(By.id("checkout-payment-step")));

                WebElement opcionPago = wait.until(
                                ExpectedConditions.presenceOfElementLocated(By.id("payment-option-1")));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", opcionPago);

                WebElement checkTerminos = wait.until(
                                ExpectedConditions.presenceOfElementLocated(
                                                By.cssSelector("input[id*='conditions_to_approve']")));
                if (!checkTerminos.isSelected()) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkTerminos);
                }

                System.out.println("Paso 8 y 9: Confirmando la orden...");
                WebElement botonConfirmarOrden = wait.until(
                                ExpectedConditions.presenceOfElementLocated(
                                                By.cssSelector("#payment-confirmation button")));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botonConfirmarOrden);

                System.out.println("Validacion Final: Verificando mensaje de confirmacion de orden...");
                WebElement mensajeConfirmacion = wait.until(
                                ExpectedConditions.visibilityOfElementLocated(
                                                By.cssSelector("#content-hook_order_confirmation h3")));

                String textoConfirmacion = mensajeConfirmacion.getText().trim();
                System.out.println("Texto obtenido de la confirmación: " + textoConfirmacion);

                Assertions.assertTrue(textoConfirmacion.toLowerCase().contains("your order is confirmed"),
                                "Error: No se mostró el mensaje de confirmación esperado. Se obtuvo: "
                                                + textoConfirmacion);

                System.out.println("Prueba EX002 (Checkout como invitado) ejecutada y validada con EXITO.");
        }

        @AfterEach
        public void tearDown() {
                if (driver != null) {
                        System.out.println("Cerrando navegador...");
                        driver.quit();
                }
        }
}
