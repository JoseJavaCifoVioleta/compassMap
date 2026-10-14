🗺️ Plan de Acción  (Versión 1.1)

🛠️ Iteración 1: Configuración Limpia del **`**pom.xml**`** y Tests de Arranque

-   **Código:** Corregir el `pom.xml` para usar Java 21, Spring Boot 3.4.x, solucionar las dependencias de Thymeleaf/Web y **añadir el motor de pruebas de Spring**.
-   **Prueba Unitaria:** Crear un test que verifique simplemente que la aplicación es capaz de encenderse correctamente sin dar errores de configuración.
-   **Commit:** `git commit -m "docs: v1.1 - setup inicial de pom.xml con dependencias de test y corrección de versiones"`

🗄️ Iteración 2: El Almacén de Datos (Entidad Customer y su Repositorio)

-   **Código:** Crear el modelo `Customer` con las anotaciones JPA y comentarios explicativos conceptuales. Crear la interfaz `CustomerRepository` (nuestro "gestor automático del archivador").
-   **Prueba Unitaria:** Un test que intente guardar un cliente de prueba en la base de datos H2 y luego lo busque por su nombre para asegurar que el almacenamiento funciona.
-   **Commit:** `git commit -m "feat: v1.1 - creación de entidad Customer, repositorio y pruebas unitarias de persistencia"`

🧪 Iteración 3: Generación de Datos de Prueba (Semillado Seguro)

-   **Código:** Implementar el generador con `CommandLineRunner` para rellenar la base de datos al arrancar usando `Java Faker`, añadiendo lógica que compruebe si ya existen registros para evitar duplicados.
-   **Prueba Unitaria:** Test que verifique que, tras arrancar el sistema, el archivador contiene exactamente el número de registros mínimos esperados (ej. más de 0 registros creados automáticamente).
-   **Commit:** `git commit -m "feat: v1.1 - inicialización automática de datos sintéticos con control de duplicados y tests"`
