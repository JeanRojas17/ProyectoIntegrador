<div align="center">

# **Proyecto Integrador - Transportes RBL**

**Integrantes**

Jean Paul Rojas Herrera

Daniel Sundar Bonilla Bolaños

Michael Dowglas Lenis Chagüendo

**Docentes**

Gabriel Pérez Moreno

Oscar Alberto Soto Piedrahita

Tania Isadora Mora Pedrero

- - -

**Institución Universitaria Antonio José Camacho**

Facultad de Ingenierías - Tecnología en Sistemas de Información

Cali, Colombia

2026

</div>

## Tabla de Contenidos

- [Avance actual del Proyecto Integrador](#avance-actual-del-proyecto-integrador)
- [Faltantes y próximas modificaciones](#faltantes-y-próximas-modificaciones)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Instrucciones de uso](#instrucciones-de-uso)

## **Avance actual del Proyecto Integrador**

El proyecto ya cuenta con los siguientes componentes implementados:

- Interfaz JavaFX con múltiples pantallas usando FXML y CSS.
- Inicio de sesión con validación de usuario contra la base de datos PostgreSQL.
- Dashboard con métricas de entregas, estado de flota y productos pendientes.
- Módulos de gestión de productos y camiones:
  - Formularios de alta y edición de camiones.
  - Formularios de alta y edición de productos.
- Sección de asignaciones que permite crear nuevas asignaciones desde el dashboard.
- Capa de acceso a datos (DAO) organizada por entidades.
- Conexión a PostgreSQL mediante `db.properties` y `DatabaseConnection`.
- Uso de Maven para compilación y ejecución con JavaFX.

## **Faltantes y próximas modificaciones**

Lo siguiente aún debe implementarse o terminarse para completar el sistema:

- Ampliar la gestión de asignaciones:
  - Editar y eliminar asignaciones.
  - Actualizar estados de entrega automáticamente.
- Gestión completa de clientes y proveedores desde la aplicación.
- Control de roles y permisos más robusto en la interfaz.
- Reportes e historial de entregas y asignaciones.
- Mejorar el manejo de errores y validaciones en todos los formularios.
- Pruebas unitarias e integración para servicios y DAO.
- Documentación de la estructura de la base de datos y ejemplos de datos.
- Posible mejora de la experiencia de usuario en la navegación del dashboard.

## **Estructura del proyecto**

- `src/main/java/com/transportesrbl/`:
  - `controllers/` - controladores JavaFX para las vistas.
  - `dao/` - acceso a datos para entidades.
  - `models/` - clases de dominio.
  - `services/` - lógica de negocio.
  - `config/` - configuración de conexión a la base de datos.

- `src/main/resources/com/transportesrbl/views/`:
  - `fxml/` - pantallas del UI.
  - `css/` - estilos visuales.

- `pom.xml` - configuración de Maven y dependencias.

## **Instrucciones de uso**

### Requisitos previos

1. Java JDK 21 instalado.
2. Maven instalado.
3. PostgreSQL configurado y accesible.
4. `db.properties` en la raíz del proyecto con las credenciales de conexión.

### Compilación y ejecución

Desde la terminal bash, ejecuta:

```bash
mvn clean javafx:run
```