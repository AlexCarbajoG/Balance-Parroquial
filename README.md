# Balance Parroquial

Aplicación Android desarrollada en Kotlin para la gestión de ingresos, egresos, gastos mensuales y balances de una parroquia.

## Objetivo del proyecto

Balance Parroquial busca facilitar el control financiero mensual de una parroquia, permitiendo registrar movimientos económicos, consultar balances y generar reportes en formato PDF.

## Funcionalidades principales

- Registro de ingresos.
- Registro de egresos.
- Registro de gastos mensuales.
- Cálculo de balances mensuales.
- Consulta de historial de balances.
- Autenticación con Google.
- Almacenamiento de datos en Firebase Realtime Database.
- Generación de reportes PDF.
- Vista previa de información antes de generar reportes.
- Gestión de horarios.
- Compartir documentos PDF.

## Tecnologías utilizadas

- Kotlin
- Android Studio
- Firebase Authentication
- Firebase Realtime Database
- Google Sign-In
- Material Design Components
- RecyclerView
- ConstraintLayout
- CardView
- Gradle
- JUnit
- Espresso

## Estructura general del proyecto

```text
app/
└── src/main/java/com/carbajo/checking/
    ├── activity/
    ├── adaptador/
    ├── data/
    ├── modelos/
    └── pdf/