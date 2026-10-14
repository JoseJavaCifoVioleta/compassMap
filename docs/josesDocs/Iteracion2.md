🗄️ Iteración 2: El Almacén de Datos y Persistencia JPA (Hito 15/10/26)

De acuerdo con nuestro plan de acción, vamos a blindar la capa de persistencia. Refactorizaremos estos dos archivos clave

1\. Modificación de **`**CustomerRepository.java**`**

Cambiamos la interfaz para que extienda de `JpaRepository` en lugar de `CrudRepository`. Esto nos abrirá las puertas a operaciones avanzadas de ordenación, paginación y un mejor manejo transaccional de datos.

2\. Modificación de **`**CustomerService.java**`**

Limpiamos el código, mejoramos la robustez del método de búsqueda devolviendo un control seguro en lugar de un `.get()` crudo (que podría fallar estrepitosamente si el ID no existe), y delegamos la gestión del guardado a las capacidades nativas del repositorio.

🧪 Prueba Unitaria Específica (Hito de control de Persistencia)

Para comprobar de forma aislada que esta capa de persistencia se comunica perfectamente con la base de datos H2 sin depender del menú por consola, utilizaremos el test `findCustomerById` que reparamos en la fase anterior dentro de `DemoApplicationTests.java`.

Al ejecutar el laboratorio de pruebas en IntelliJ, **la prueba de guardado y búsqueda debe pasar limpiamente en verde.**