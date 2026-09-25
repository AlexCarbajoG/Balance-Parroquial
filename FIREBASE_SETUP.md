# Configuración de acceso

1. En Firebase Console, proyecto `checking-46fb2`, abre Authentication > Sign-in method y habilita **Correo electrónico/contraseña** y **Google**.
2. Crea las cuentas de correo autorizadas desde Authentication > Users. La aplicación solo inicia sesión; no ofrece registro público.
3. Agrega las huellas SHA-1 y SHA-256 de las firmas debug y release en la configuración de la aplicación Android `com.carbajo.checking`.
4. Descarga de nuevo `google-services.json` y reemplaza `app/google-services.json`. El archivo actual no contiene clientes OAuth; el acceso con Google requiere el `default_web_client_id` generado tras esta configuración.
5. En Realtime Database > Data, crea `authorizedUsers/<UID> = true` para cada persona autorizada. Copia cada UID desde Authentication > Users. Solo un administrador con acceso a Firebase Console debe editar esta lista.
6. En Realtime Database > Rules, publica el contenido de `database.rules.json`. Estas reglas exigen una sesión y un UID autorizado para leer o escribir datos.

Las reglas en este repositorio son una plantilla. No se aplican al servidor hasta publicarlas en Firebase Console.
