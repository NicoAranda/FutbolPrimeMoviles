# ⚽ FutbolPrime

1. Nombre del Proyecto
FutbolPrime

2. Integrantes
- Nicolas Aranda

3. Funcionalidades
Autenticación de Usuarios
- Registro de nuevos usuarios
- Inicio de sesión con email y contraseña
- Persistencia de sesión
- Cierre de sesión

Gestión de Perfil
- Visualización de datos del usuario (ID, nombre, email, rol)

Catálogo de Productos
- Listado de productos deportivos
- Visualización de detalles
- Carga de imágenes con Coil

Carrito de Compras
- Agregar productos al carrito
- Modificar cantidades
- Eliminar productos
- Cálculo automático de totales

Proceso de Compra
- Formulario de datos de pago con validaciones
- Generación de pedidos
- Notificaciones al completar compra

4. Endpoints Utilizados

Microservicios Propios
Base URL:´http://52.203.16.208:8080/api`

Usuarios
- `POST /usuarios/login` - Login de usuario
  ```json
  Body: {"email": "juan@correo.cl", "password": "juan1234"}
  Respuesta: {"id": 2, "nombre": "Juan Pérez", "email": "juan@correo.cl", "rol": "CLIENTE"}
  ```

- `POST /usuarios/register` - Registro de usuario
  ```json
  Body: {"nombre": "string", "email": "string", "password": "string"}
  ```

Productos
- `GET /productos` - Listar productos
- `GET /productos/{id}` - Detalle de producto

Carrito
- `GET /carrito/usuario/{usuarioId}` - Obtener carrito del usuario
- `POST /carrito/agregar` - Agregar producto
  ```json
  Body: {"usuarioId": 2, "productoId": 1, "cantidad": 1}
  ```
- `PUT /carrito/actualizar/{itemId}` - Actualizar cantidad
- `DELETE /carrito/eliminar/{carritoId}/producto/{productoId}` - Eliminar producto
- 
5. Pasos para Ejecutar

Requisitos Previos
- Android Studio Hedgehog o superior
- JDK 17+
- Android SDK API 24+
- Emulador Android o dispositivo físico

Instalación

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/[tu-usuario]/futbolprime.git
   cd futbolprime
   ```

2. Abrir en Android Studio:
   - File → Open
   - Seleccionar carpeta del proyecto
   - Esperar sincronización de Gradle

3. Configurar dispositivo:
   - Crear AVD (Pixel 6 Pro, API 33) o conectar dispositivo físico
   - Habilitar depuración USB si es dispositivo físico

4. Ejecutar la aplicación:
   - Run → Run 'app' (o Shift + F10)

Credenciales de Prueba
```
Email: juan@correo.cl
Contraseña: juan1234
```

6. APK Firmado y Keystore

Información de Firma
- ubicación del APK: `app/release/app-release.apk`
- Ubicación del Keystore: `app/keystore/futbolprime.jks`
- Alias: `FutbolPrime`
- Validez: 25 años
- Esquemas: V1 (JAR) + V2 (APK)

Captura del APK Firmado
<img width="1109" height="681" alt="image" src="https://github.com/user-attachments/assets/df648071-2a5a-4483-914e-57e2665cf924" />


Captura del Keystore (.jks)
<img width="294" height="261" alt="image" src="https://github.com/user-attachments/assets/fff4c972-646e-4407-8407-16ef6383e244" />


Tecnologías Utilizadas

**Frontend:**
- Kotlin + Jetpack Compose
- Retrofit para consumo de APIs
- Coil para carga de imágenes
- Material Design 3

**Backend:**
- Spring Boot
- MySQL Driver
- JPA/Hibernate
- Lombok

**Testing:**
- JUnit 4
- MockK
