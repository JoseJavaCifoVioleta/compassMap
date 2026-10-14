*Plan de Acción para la Capa Visual (Versión 1.2)

🗺️ Plan de Acción: Capa Visual y Plantillas Dinámicas (Thymeleaf)

🛠️ Iteración 5: El "Tablón de Anuncios" (Controlador Web y Vista de Clientes)

*Objetivo: Conseguir que el proyecto pinte una página web real con la lista de clientes traídos de la base de datos H2.*

-   **Acción 5.1 (Código):** Crearemos un nuevo controlador web usando la anotación `@Controller` (a diferencia de `@RestController`, este busca páginas HTML en lugar de devolver texto). Lo llamaremos `CustomerWebController.java`.
-   **Acción 5.2 (Código):** Borraremos los bocetos estáticos y crearemos un archivo definitivo llamado `customers.html` dentro de `templates`. Usaremos las etiquetas dinámicas de Thymeleaf (`th:each`) para recorrer la lista de clientes y pintarlos en una tabla limpia.
-   **Acción 5.3 (Prueba Unitaria):** Añadiremos un test en `DemoApplicationTests` que verifique que el nuevo controlador web asocia correctamente la lógica con el nombre de la plantilla HTML.
-   **Commit:** `feat: v1.2 - CustomerWebController y plantilla Thymeleaf para listar clientes de forma dinamica`

🎨 Iteración 6: Maquetación y Estilo Profesional (CSS e Integración del Mapa)

*Objetivo: Dejar la aplicación bonita, ordenada y preparada con un diseño moderno (estilo Bootstrap o similar) que encaje con la temática de "CompassMap".*

-   **Acción 6.1 (Código):** Añadiremos un archivo de estilos global (`style.css`) en la carpeta `resources/static/css/` para darle un aspecto corporativo y limpio a la tabla y los botones.
-   **Acción 6.2 (Código):** Diseñaremos una cabecera (*Navbar*) común para que el usuario pueda navegar entre la lista de clientes y la futura sección de mapas (`RoadMaps`).
-   **Acción 6.3 (Prueba Unitaria):** Comprobaremos que los recursos estáticos se cargan en el contexto del laboratorio sin lanzar errores de ruta.
-   **Commit:** `feat: v1.2 - Estilos CSS globales y maquetacion visual de la interfaz web`

🎛️ Iteración 7: Panel de Control de Datos (Formularios y Operaciones Web)

*Objetivo: Permitir que tus compañeros puedan interactuar con la aplicación directamente desde la pantalla (añadir un cliente manual o borrarlo haciendo un clic).*

-   **Acción 7.1 (Código):** Crearemos un formulario HTML dinámico en la web para dar de alta nuevos clientes sin usar herramientas externas como Postman.
-   **Acción 7.2 (Código):** Conectaremos los botones de la interfaz web con los métodos de eliminación y semillado seguro que ya validamos en las fases anteriores.
-   **Acción 7.3 (Prueba Unitaria):** Testearemos el comportamiento del envío de datos del formulario hacia el servicio.
-   **Commit:** `feat: v1.2 - Formularios web interactivos para gestion de clientes en tiempo real`