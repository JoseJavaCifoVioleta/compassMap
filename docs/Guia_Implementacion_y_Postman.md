# Guía de Implementación del Sistema: CustomerProfile, Q&A y Pruebas en Postman

Este documento describe de manera clara las refactorizaciones y unificaciones de código realizadas en el backend con **Spring Boot**, orientadas al sistema de asignación de perfiles (**CustomerProfile**) y el motor de preguntas y respuestas (**Q&A**), así como las instrucciones paso a paso para verificar y testear cada endpoint utilizando **Postman**.

---

## 🛠️ Resumen de lo Realizado

### 1. Refactorización y Unificación de Modelos a JPA
Se transformaron las clases nativas del diagrama de clases UML en Entidades gestionadas por Hibernate/JPA:
* **`Answer`**: Se descomentaron las anotaciones `@Entity` e `@Id` y se configuró el método lógico `isCorrect(Boolean expected)` adaptándose al uso de Lombok.
* **`Question`**: Se resolvió un error crítico de base de datos renombrando la columna conflictiva `order` (palabra reservada en SQL) a `@Column(name = "sort_order")` sin alterar la variable de Java del profesor.
* **`QuestionAndAnswer`**: Se mapeó la relación de los objetos mediante anotaciones relacionales `@ManyToOne` y sus respectivas claves foráneas `@JoinColumn(name = "...")`.

### 2. Unificación del Servicio y Controlador de Perfiles
Se acopló el código propuesto por el profesor en `CustomerProfileService` y `CustomerProfileRestController` con las siguientes mejoras:
* Se mantuvieron intactas las semánticas de actualización parcial (`updateCustomerProfile`), generación automática de identificadores mediante `UUID.randomUUID()` e impresiones por consola.
* **Corrección Crítica Masiva (`saveAll`)**: Se interceptó el guardado en bloque dentro del servicio para inyectar IDs únicos a cada elemento recibido desde Postman de forma iterativa, solucionando el error `500 Internal Server Error` provocado por identificadores ausentes en Hibernate.
* Se limpiaron todos los errores en rojo y las advertencias de compilación por el uso de colecciones parametrizadas sin tipo (*Raw Types*), garantizando el cumplimiento de genéricos estrictos en Java.

### 3. Complementación y Robustecimiento de Datos de Prueba (`PopulatorDB`)
Se amplió el cargador de bases de datos para dar soporte completo al nuevo motor analítico de 16 perfiles, derivado del mapa de capacidades tecnológicas en **Codi (Programación)** y **Machine Learning (ML)**:
* Se agregaron preguntas y respuestas base utilizando códigos lógicos correlativos (`q-001`, `a-001`, `qa-001`).
* Se registraron los **16 perfiles e intenciones reales** del negocio en formato bilingüe (Catalán / Castellano) para su posterior indexación lógica en la matriz relacional.

---

## 🚀 Guía de Pruebas en Postman

Asegúrate de que tu servidor local esté corriendo en el puerto predeterminado (`http://localhost:8080`).

### 📌 Fase Inicial: Poblado Automático de Datos de Prueba
Antes de lanzar consultas de lectura, puedes invocar al Populator maestro para rellenar las tablas H2 al instante.
* **Método**: `POST`
* **URL**: `http://localhost:8080/api/db/populate`
* **Resultado Esperado**: Código de estado `200 OK` con un cuerpo JSON de confirmación:
  ```json
  {
    "status": "success",
    "message": "Base de datos poblada con éxito con los 16 perfiles y el sistema Q&A"
  }
  ```

---

### 📌 Bloque de Pruebas CRUD para `CustomerProfile`

#### 1. Crear Perfil Individual (`POST /create`)
* **URL**: `http://localhost:8080/api/v1/customerprofiles/create`
* **Body** (`raw` -> `JSON`):
  ```json
  {
    "codeProfile": "ARCHITECT",
    "name": "Expert full-stack + AI",
    "description": "Ja domina les dues disciplines i vol aprofundir en ambdues",
    "goal": "Potenciar la integración de sistemas complejos con Inteligencia Artificial"
  }
  ```

#### 2. Crear Múltiples Perfiles en Bloque (`POST /createAll`)
* **URL**: `http://localhost:8080/api/v1/customerprofiles/createAll`
* **Body** (`raw` -> `JSON`): *Notar el inicio estricto con corchetes `[...]` para listas.*
  ```json
  [
    {
      "codeProfile": "ARCHITECT",
      "name": "Expert full-stack + AI",
      "description": "Ja domina les dues disciplines i vol aprofundir en ambdues",
      "goal": "Potenciar la integración de sistemas complejos con Inteligencia Artificial"
    },
    {
      "codeProfile": "BEGINNER",
      "name": "Programador que vol entrar en ML",
      "description": "Sòlid en codi i vol aprendre Machine Learning de debò",
      "goal": "Adquirir bases matemáticas y algorítmicas de aprendizaje automático"
    }
  ]
  ```

#### 3. Obtener Todos los Perfiles (`GET /all`)
* **URL**: `http://localhost:8080/api/v1/customerprofiles/all`
* **Resultado Esperado**: Lista completa de perfiles en formato JSON con sus correspondientes IDs autogenerados.

#### 4. Obtener Perfil Específico por ID (`GET /byId`)
* **URL**: `http://localhost:8080/api/v1/customerprofiles/byId?id=p-001`
* **Parámetros**: Utiliza Query Params introduciendo la clave `id` y el valor del registro (ejemplo: `p-001`).

#### 5. Contar Número de Perfiles Existentes (`GET /count`)
* **URL**: `http://localhost:8080/api/v1/customerprofiles/count`

#### 6. Actualizar un Perfil Existente (`PUT /update`)
* **URL**: `http://localhost:8080/api/v1/customerprofiles/update`
* **Body** (`raw` -> `JSON`): *Requiere incluir de forma obligatoria el ID del elemento que deseas pisar.*
  ```json
  {
    "id": "p-001",
    "name": "Expert full-stack + IA Avanzada",
    "codeProfile": "ARCHITECT"
  }
  ```

#### 7. Eliminar un Perfil por ID (`DELETE /byId`)
* **URL**: `http://localhost:8080/api/v1/customerprofiles/byId?id=p-001`

#### 8. Vaciar Todos los Perfiles (`DELETE /all`)
* **URL**: `http://localhost:8080/api/v1/customerprofiles/all`
