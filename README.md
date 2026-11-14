# Evaluación Final - QA Automation

Automatización de pruebas web para la plataforma de comercio electrónico [My Store](https://teststore.automationtesting.co.uk/index.php) utilizando **Java**, **Selenium WebDriver**, **JUnit 5** y **Maven**.

## Estructura del Proyecto

```text
segundoExamenFinal/
├── src/
│   └── test/
│       └── java/
│           └── examen/
│               ├── EX001.java   # RF-001: Compra con usuario autenticado
│               └── EX002.java   # RF-002: Compra como invitado (Guest Checkout)
├── pom.xml
├── .gitignore
└── README.md
```

## Tecnologías Utilizadas

- **Java 17**
- **Maven**
- **Selenium WebDriver 4.35.0**
- **JUnit 5 (Jupiter 5.12.2)**
- **WebDriverManager 6.1.0**

## Estructura de Ramas Git

- `main`: Rama principal de producción / entrega.
- `develop`: Rama base de desarrollo.
- `feature/examenFinal`: Rama de desarrollo de los scripts de automatización.

## Ejecución de Pruebas

Para ejecutar todas las pruebas automatizadas:

```bash
mvn clean test
```

Para ejecutar una prueba específica:

```bash
mvn test -Dtest=EX001
mvn test -Dtest=EX002
```
