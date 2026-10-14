🗺️ Plan de Acción: El CRUD Total y Motor de Búsqueda Avanzado (v1.3)

Para implementar esto de forma quirúrgica (manteniendo nuestra regla de oro: código, test y commit en verde), estructuraremos el trabajo en tres nuevas características (*features*):

🛠️ Iteración 8: Completar el CRUD (Botones de Borrar y Editar)

-   **Acción:** Añadiremos un botón de "Eliminar" con un icono al lado de cada cliente en la tabla para borrarlo con un clic. Crearemos una nueva pantalla con un formulario (`edit-customer.html`) para poder modificar los datos de cualquier cliente existente de forma interactiva.
-   **Prueba Unitaria:** Validaremos en el laboratorio que el servicio procesa las actualizaciones de datos modificados sin duplicar la ficha.
-   **Commit:** `feat: v1.3 - completado el CRUD con operaciones visuales de borrado y edicion de clientes`

🔍 Iteración 9: Buscador Multicampo (Filtrar por cualquier columna)

-   **Acción:** Añadiremos un desplegable (*Select*) en la caja de búsqueda web para que el usuario elija si quiere buscar por *Nombre*, *Apellido*, *Email*, *Teléfono* o *Dirección*. En el repositorio usaremos la potencia de Spring Data JPA para activar estas consultas dinámicas.
-   **Prueba Unitaria:** Testearemos que el motor es capaz de aislar y encontrar registros buscando por criterios distintos al nombre.
-   **Commit:** `feat: v1.3 - buscador avanzado multicampo integrado en la vista de clientes`

📐 Iteración 10: Ordenación Dinámica (Ascendente / Descendente)

-   **Acción:** Convertiremos los encabezados de la tabla (ID, Nombre, Apellido...) en enlaces interactivos. Al pulsar sobre "Nombre", la lista se ordenará alfabéticamente. Si se pulsa otra vez, cambiará el sentido (Ascendente / Descendente), usando la herramienta nativa `Sort` de Spring Data.
-   **Prueba Unitaria:** Certificaremos que el archivador devuelve los bloques de páginas ordenados según el criterio solicitado.
-   **Commit:** `feat: v1.3 - sistema de ordenacion dinamica ascendente y descendente por columnas`