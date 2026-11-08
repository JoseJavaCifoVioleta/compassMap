# Manual de usuario técnico — compassMap

> **Este documento es un manual para usuario técnico.**
>
> No es un manual de usuario final. Está pensado para alguien que va a arrancar la aplicación, llamar a la API REST, inspeccionar la base de datos H2 o usar el backoffice de consola.
>
> Hoy **no existe una interfaz web de usuario final**: solo hay dos vistas Thymeleaf de bienvenida (`/home1` y `/home2`) y una API REST. El frontend React todavía no está implementado.
>
> Ámbito: rama `task3-assessmentview-pending`, fecha 2026-11-08.
>
> Los nombres de clases, rutas, tablas, ficheros y campos se mantienen en inglés tal como aparecen en el código.

---

## 1. Qué es compassMap

compassMap es un backend Spring Boot para hacer un **autodiagnóstico técnico**:

1. Un `Customer` (cliente) responde una serie de `Question`.
2. Cada respuesta se guarda como un `QuestionAndAnswer` con su `Answer` booleana y una `scoreContribution`.
3. Las respuestas de una ejecución se agrupan en un `AssessmentView`.
4. El resultado se representa con un `CustomerProfile`, que tiene un `CodeProfile` y una lista de `RoadMap` recomendados.

En corto:

```text
Customer
   └── AssessmentView
          └── QuestionAndAnswer
                 ├── Question
                 └── Answer
   └── CustomerProfile
          └── RoadMap (recomendados)
```

---

## 2. Requisitos

| Requisito | Detalle |
|---|---|
| Java | JDK 21 (`java -version`) |
| Maven | No hace falta instalarlo: el proyecto trae `mvnw` / `mvnw.cmd` |
| IDE | IntelliJ IDEA (recomendado), VS Code o Eclipse/STS |
| Lombok | Soporte de anotaciones activado en el IDE |
| Base de datos | H2 en fichero (incluida como dependencia) |
| Cliente HTTP | `curl`, Postman, Insomnia o el navegador para GET |

---

## 3. Puesta en marcha

### 3.1. Abrir el proyecto

El repositorio tiene esta forma:

```text
compassMap/                  ← raíz del repositorio
├── compassMap/              ← proyecto Spring Boot (pom.xml, src/)
├── db/                      ← ficheros H2 versionados
└── docs/                    ← documentación, mockups y diagramas
```

En IntelliJ: **File > Open** y selecciona `compassMap/compassMap/pom.xml`.

En VS Code: abre la carpeta `compassMap/compassMap`.

### 3.2. Configurar la base de datos

`compassMap/src/main/resources/application.properties` trae por defecto una ruta de Linux del autor:

```properties
spring.datasource.url=jdbc:h2:/home/albert/MyProjects/SpringProjects/compassMap/db/compassmapdb
spring.datasource.username=albert
spring.datasource.password=1234
```

Tienes dos opciones:

**Opción A (recomendada): no tocar el fichero y sobrescribir la URL por variable de entorno.**

PowerShell:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:h2:file:C:/Users/tu-usuario/AppData/Local/compassMap/db/compassmapdb"
```

Bash:

```bash
export SPRING_DATASOURCE_URL="jdbc:h2:file:/home/tu-usuario/compassMap/db/compassmapdb"
```

**Opción B: editar `application.properties`** y poner una ruta absoluta de tu máquina.

> La carpeta de la base de datos debe existir. H2 crea el fichero `.mv.db`, pero no siempre crea los directorios intermedios.

> La carpeta `db/` del repositorio está versionada. Si ejecutas la aplicación contra ella, Git verá cambios en `db/compassmapdb.mv.db`. Para trabajar sin ensuciar el repositorio, usa una copia local fuera del proyecto.

### 3.3. Arrancar la aplicación

Desde el IDE: ejecuta `com.example.demo.DemoApplication`.

Desde terminal:

```powershell
cd compassMap
.\mvnw.cmd spring-boot:run
```

En Linux/macOS:

```bash
cd compassMap
./mvnw spring-boot:run
```

La API queda en:

```text
http://localhost:8080/api/v1
```

Las dos vistas Thymeleaf existentes son:

```text
http://localhost:8080/home1
http://localhost:8080/home2
```

> No hay mapping para `/`: si abres `http://localhost:8080/` obtendrás un `404`.

### 3.4. Consola H2

Si la consola está habilitada, está en:

```text
http://localhost:8080/h2-console
```

Datos de conexión:

| Campo | Valor |
|---|---|
| JDBC URL | la misma de `spring.datasource.url` |
| User Name | `albert` |
| Password | `1234` |

> El proyecto no fija `spring.h2.console.enabled`. Si `/h2-console` no responde, arranca con:
>
> ```powershell
> .\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--spring.h2.console.enabled=true"
> ```
>
> o añade `spring.h2.console.enabled=true` a `application.properties`.

### 3.5. Sembrar datos

**La aplicación no siembra datos automáticamente al arrancar.** `PopulatorDB` existe, pero no se invoca desde `DemoApplication.run(...)`.

Tienes tres formas de crear datos:

1. Llamando a la API REST (sección 5).
2. Activando el backoffice de consola (sección 4.2).
3. Insertando manualmente en H2 (no recomendado para pruebas de API).

---

## 4. Las dos formas de usar la aplicación

### 4.1. API REST

Es la forma principal. Todas las rutas empiezan por `/api/v1`.

| Recurso | Base path |
|---|---|
| Customers | `/api/v1/customers` |
| Questions | `/api/v1/questions` |
| Question & Answers | `/api/v1/questionAndAnswers` |
| Customer profiles | `/api/v1/customerProfiles` |
| Roadmaps | `/api/v1/roadmaps` |
| Assessments | `/api/v1/assessments` |

### 4.2. BackOffice de consola

`BackOffice` es una herramienta de desarrollo con menús por consola. **No arranca por defecto**: la llamada está comentada en `DemoApplication.run(...)`.

Para usarlo, descomenta esta línea en `DemoApplication.java`:

```java
BackOffice.startBackOffice(populatorDB);
```

y vuelve a arrancar. El menú principal es:

```text
1. Customer
2. Roadmap
3. Customer Profile
4. Question
5. Question and Answer
6. Assessment View
7. Quit
```

Cada entidad tiene un submenú con estas operaciones:

```text
1. Poblar / cargar datos
2. Get all
3. Get by Id
4. Delete by Id
5. Delete all
6. Count
7. Back
8. (solo perfiles y assessments) consultas adicionales
```

Detalles útiles:

- **Roadmap**: carga `docs/mockup/roadmaps.json` conservando ids `RM01..RM20`.
- **Customer Profile**: carga `docs/mockup/profiles.json` conservando ids `P01..P04` y asigna los roadmaps cuyo nombre termina en `(P01)`, `(P02)`, etc.
- **Question**: genera preguntas aleatorias con JavaFaker.
- **Question and Answer**: crea una respuesta aleatoria por cada pregunta existente.
- **Assessment View**: crea una ejecución de ejemplo con un cliente, un perfil y una respuesta por pregunta.
- El backoffice **bloquea la consola** mientras está activo; no lo uses a la vez que esperas peticiones interactivas en la misma terminal.

> El backoffice busca los mockups en `docs/mockup/` o en `../docs/mockup/`, así que funciona tanto desde la raíz del repositorio como desde `compassMap/compassMap`.

---

## 5. Flujo típico de extremo a extremo

Los ejemplos usan `curl`. En PowerShell, `curl` es un alias de `Invoke-WebRequest`; usa **`curl.exe`** para el curl real.

```powershell
$BASE = "http://localhost:8080/api/v1"
```

### Paso 1 — Crear un cliente

```powershell
curl.exe -s -X POST "$BASE/customers/create" `
  -H "Content-Type: application/json" `
  -d '{
    "firstName": "Ada",
    "lastName": "Lovelace",
    "email": "ada@example.com",
    "phone": "600000000",
    "address": "Calle Mayor 1"
  }'
```

Respuesta aproximada:

```json
{
  "id": "4f4c0d1e-...",
  "firstName": "Ada",
  "lastName": "Lovelace",
  "email": "ada@example.com",
  "phone": "600000000",
  "address": "Calle Mayor 1"
}
```

> Envía el `id` que quieras: el servicio lo sobrescribe con un UUID. Guarda el `id` devuelto.

### Paso 2 — Crear preguntas

```powershell
curl.exe -s -X POST "$BASE/questions/create" `
  -H "Content-Type: application/json" `
  -d '{
    "text": "¿Sabes programar?",
    "dimension": "KNOWS_CODE",
    "order": 1,
    "weight": 1.0
  }'
```

Notas:

- `dimension` debe ser uno de: `KNOWS_CODE`, `WANTS_TO_LEARN`, `KNOWS_ML`, `WANTS_TO_LEARN_ML`, `KNOWS_GIT`.
- `name` se genera automáticamente como `q-001`, `q-002`, etc. a partir de `order`.
- `id`, si lo envías, se sobrescribe con un UUID.
- `order` se guarda en la columna `question_order` porque `order` es palabra reservada en H2.

### Paso 3 — Crear roadmaps

```powershell
curl.exe -s -X POST "$BASE/roadmaps/create" `
  -H "Content-Type: application/json" `
  -d '{
    "name": "Backend Advanced",
    "description": "Arquitectura, patrones y testing",
    "stepsItemsSummary": {
      "1": "Design patterns",
      "2": "Clean architecture",
      "3": "Testing"
    },
    "steps": 3,
    "completed": false
  }'
```

> El `id` se genera como UUID. La API no permite conservar ids legibles tipo `RM01`; eso solo lo hace el backoffice al cargar el JSON.

### Paso 4 — Crear un perfil

```powershell
curl.exe -s -X POST "$BASE/customerProfiles/create" `
  -H "Content-Type: application/json" `
  -d '{
    "codeProfile": "ARCHITECT",
    "name": "Perfil de Ada",
    "description": "Desarrolladora que quiere profundizar",
    "goal": "Mejorar arquitectura",
    "customer": { "id": "<CUSTOMER_ID>" },
    "profileAnswers": {
      "KNOWS_CODE": true,
      "WANTS_TO_LEARN": true,
      "KNOWS_ML": false,
      "WANTS_TO_LEARN_ML": false,
      "KNOWS_GIT": true
    }
  }'
```

Notas:

- `codeProfile` debe ser `BEGINNER`, `ARCHITECT` o `NON_DEVELOPER`.
- `customer` se referencia por `id`; no se crea aquí.
- **El alta no resuelve `recommendedRoadmaps` por id.** Para asociar roadmaps de forma segura, crea primero el perfil y usa `PUT /update` (paso 6).
- En la base de datos, las claves de `profileAnswers` se guardan como ordinal del enum, no como texto. No reordenes `Dimension`.

### Paso 5 — Crear un assessment

```powershell
curl.exe -s -X POST "$BASE/assessments/create" `
  -H "Content-Type: application/json" `
  -d '{
    "customer": { "id": "<CUSTOMER_ID>" },
    "profile": { "id": "<PROFILE_ID>" },
    "questionsAndAnswers": [
      {
        "question": { "id": "<QUESTION_ID_1>" },
        "answer": { "value": true },
        "scoreContribution": 1.0
      },
      {
        "question": { "id": "<QUESTION_ID_2>" },
        "answer": { "value": false },
        "scoreContribution": 0.0
      }
    ]
  }'
```

Qué hace el servicio:

- Genera el `id` del `AssessmentView`.
- Rellena `timestamp` con `Instant.now()` si no lo envías.
- Resuelve `customer`, `profile` y cada `question` por `id` contra la base de datos.
- Genera un `id` para cada `QuestionAndAnswer`.
- Genera un `id` para cada `Answer` si no lo envías.
- Sincroniza ambos lados de la relación `AssessmentView ↔ QuestionAndAnswer`.
- Calcula `qtyQuestions` a partir del tamaño de la lista.
- Guarda todo en cascada.

> No envíes `answer.id`: la columna `QUESTION_AND_ANSWER.ANSWER_ID` es `UNIQUE`. Deja que el servicio lo genere.

> En la respuesta **no verás `questionsAndAnswers`**: el campo es `WRITE_ONLY` para evitar bucles JSON. Consúltalo con el endpoint de Q&A (paso 7).

### Paso 6 — Actualizar el perfil con roadmaps recomendados

```powershell
curl.exe -s -X PUT "$BASE/customerProfiles/update" `
  -H "Content-Type: application/json" `
  -d '{
    "id": "<PROFILE_ID>",
    "recommendedRoadmaps": [
      { "id": "<ROADMAP_ID>" }
    ]
  }'
```

Semántica del update:

- Solo se actualizan los campos no nulos.
- Si `recommendedRoadmaps` es `null` (no viene en el JSON), no se toca la lista actual.
- Si viene una lista, **se reemplaza completa** por los roadmaps resueltos por `id`.
- Una lista vacía `[]` limpia la relación.
- Un roadmap sin `id` devuelve `400`.
- Un roadmap con `id` inexistente devuelve `404`.
- No se crean roadmaps nuevos.

### Paso 7 — Consultar el assessment

```powershell
# Todas las Q&A de un assessment
curl.exe -s "$BASE/assessments/questionsAndAnswers?id=<ASSESSMENT_ID>"

# Puntuación total: suma de scoreContribution
curl.exe -s "$BASE/assessments/score?id=<ASSESSMENT_ID>"

# Assessments de un cliente
curl.exe -s "$BASE/assessments/byCustomerId?customerId=<CUSTOMER_ID>"
```

> `score` **no calcula** la puntuación a partir de las respuestas. Solo suma los `scoreContribution` que se guardaron. Si envías `0.0`, suma `0.0`.

### Paso 8 — Limpiar los datos de prueba

El orden importa por las claves foráneas:

```powershell
curl.exe -s -X DELETE "$BASE/assessments/all"
curl.exe -s -X DELETE "$BASE/customerProfiles/all"
curl.exe -s -X DELETE "$BASE/questionAndAnswers/all"
curl.exe -s -X DELETE "$BASE/roadmaps/all"
curl.exe -s -X DELETE "$BASE/questions/all"
curl.exe -s -X DELETE "$BASE/customers/all"
```

> **Cuidado:** borrar un `CustomerProfile` arrastra sus `recommendedRoadmaps` por `CascadeType.ALL`. En una relación muchos-a-muchos eso puede borrar roadmaps compartidos con otros perfiles. Es un riesgo conocido del modelo actual, pedido literalmente por la Task 2.

---

## 6. Referencia de la API

### 6.1. Customers — `/api/v1/customers`

| Método | Ruta | Entrada | Respuesta |
|---|---|---|---|
| POST | `/create` | body `Customer` | `Customer` creado |
| POST | `/createAll` | array de `Customer` | array creado |
| PUT | `/update` | body `Customer` con `id` | `Customer` actualizado o vacío |
| GET | `/all` | — | lista de `Customer` |
| GET | `/byId?id=...` | query `id` | `Customer` o vacío |
| GET | `/count` | — | número |
| DELETE | `/byId?id=...` | query `id` | `Customer` borrado o vacío |
| DELETE | `/all` | — | sin cuerpo |

Campos de `Customer`: `id`, `firstName`, `lastName`, `email`, `phone`, `address`.

### 6.2. Questions — `/api/v1/questions`

| Método | Ruta | Entrada | Respuesta |
|---|---|---|---|
| POST | `/create` | body `Question` | `Question` creada |
| POST | `/createAll` | array de `Question` | array creado |
| PUT | `/update` | body `Question` con `id` | `Question` actualizada o vacío |
| GET | `/all` | — | lista |
| GET | `/byId?id=...` | query `id` | `Question` o vacío |
| GET | `/count` | — | número |
| DELETE | `/byId?id=...` | query `id` | `Question` borrada o vacío |
| DELETE | `/all` | — | sin cuerpo |

Campos de `Question`: `id`, `name`, `text`, `dimension`, `order`, `weight`.

Comportamiento del alta:

- `id` se sobrescribe con UUID.
- `name` se sobrescribe con `q-%03d` a partir de `order`.
- `dimension` usa `@Enumerated(STRING)`.

### 6.3. Question & Answers — `/api/v1/questionAndAnswers`

| Método | Ruta | Entrada | Respuesta |
|---|---|---|---|
| POST | `/create` | body `QuestionAndAnswer` | Q&A creada |
| POST | `/createAll` | array | array creado |
| PUT | `/update` | body con `id` | Q&A actualizada o vacío |
| GET | `/all` | — | lista |
| GET | `/byId?id=...` | query `id` | Q&A o vacío |
| GET | `/count` | — | número |
| DELETE | `/byId?id=...` | query `id` | Q&A borrada o vacío |
| DELETE | `/all` | — | sin cuerpo |

Ejemplo de alta independiente:

```json
{
  "question": { "id": "<QUESTION_ID>" },
  "answer": { "value": true },
  "scoreContribution": 1.0
}
```

Notas:

- `question` debe existir.
- `answer` es opcional; si viene sin `id`, se genera.
- `assessmentView` no se envía: es el lado propietario de la relación con `AssessmentView` y se gestiona desde el assessment.
- Los errores de esta ruta **no** están mapeados a `4xx`: una pregunta inexistente puede devolver `500`.

### 6.4. Customer profiles — `/api/v1/customerProfiles`

| Método | Ruta | Entrada | Respuesta |
|---|---|---|---|
| POST | `/create` | body `CustomerProfile` | perfil creado |
| POST | `/createAll` | array | array creado |
| PUT | `/update` | body con `id` | perfil actualizado o vacío |
| GET | `/all` | — | lista |
| GET | `/byId?id=...` | query `id` | perfil o vacío |
| GET | `/count` | — | número |
| DELETE | `/byId?id=...` | query `id` | perfil borrado o vacío |
| DELETE | `/all` | — | sin cuerpo |

Campos de `CustomerProfile`: `id`, `codeProfile`, `name`, `description`, `goal`, `profileAnswers`, `customer`, `recommendedRoadmaps`.

- `customer` se referencia por `id`.
- `recommendedRoadmaps` solo se resuelve en `PUT /update`.
- `profileAnswers` es un objeto `Dimension → Boolean`.

### 6.5. Roadmaps — `/api/v1/roadmaps`

| Método | Ruta | Entrada | Respuesta |
|---|---|---|---|
| POST | `/create` | body `RoadMap` | roadmap creado |
| POST | `/createAll` | array | array creado |
| PUT | `/update` | body con `id` | roadmap actualizado o vacío |
| GET | `/all` | — | lista |
| GET | `/byId?id=...` | query `id` | roadmap o vacío |
| GET | `/count` | — | número |
| DELETE | `/byId?id=...` | query `id` | roadmap borrado o vacío |
| DELETE | `/all` | — | sin cuerpo |

Campos de `RoadMap`: `id`, `name`, `description`, `stepsItemsSummary`, `steps`, `completed`.

- `stepsItemsSummary` es un objeto `String → String`.
- La respuesta no incluye `profiles` (`@JsonIgnore`).
- El `id` se genera como UUID en el alta REST.

### 6.6. Assessments — `/api/v1/assessments`

| Método | Ruta | Entrada | Respuesta |
|---|---|---|---|
| POST | `/create` | body `AssessmentView` | assessment creado |
| GET | `/all` | — | lista |
| GET | `/byId?id=...` | query `id` | assessment o vacío |
| GET | `/byCustomerId?customerId=...` | query `customerId` | lista |
| GET | `/questionsAndAnswers?id=...` | query `id` | lista de Q&A |
| GET | `/score?id=...` | query `id` | `double` |
| GET | `/count` | — | número |
| DELETE | `/byId?id=...` | query `id` | assessment borrado o vacío |
| DELETE | `/all` | — | sin cuerpo |

Campos de `AssessmentView`: `id`, `customer`, `timestamp`, `qtyQuestions`, `profile`.

- `questionsAndAnswers` se acepta en el POST, pero no se serializa en la respuesta.
- `qtyQuestions` se calcula en el servidor.
- `timestamp` se rellena en el servidor si falta.

---

## 7. Manejo de errores

El proyecto tiene un `@RestControllerAdvice` limitado a dos controladores:

```java
@RestControllerAdvice(assignableTypes = {
    AssessmentViewRestController.class,
    CustomerProfileRestController.class
})
```

Por tanto, el formato `{"error": "..."}` solo está garantizado en:

- `/api/v1/assessments/**`
- `/api/v1/customerProfiles/**`

Casos mapeados:

| Caso | Código | Cuerpo |
|---|---|---|
| Customer inexistente en un assessment | `404` | `{"error":"Customer not found: ..."}` |
| CustomerProfile inexistente en un assessment | `404` | `{"error":"CustomerProfile not found: ..."}` |
| Question inexistente en un assessment | `404` | `{"error":"Question not found: ..."}` |
| Q&A sin `question.id` | `400` | `{"error":"QuestionAndAnswer requires an existing question id"}` |
| Roadmap sin `id` en update de perfil | `400` | `{"error":"Recommended roadmap requires an existing roadmap id"}` |
| Roadmap inexistente en update de perfil | `404` | `{"error":"RoadMap not found: ..."}` |

Comportamientos que conviene conocer:

- **Los GET por id no lanzan 404**: si el id no existe, el servicio devuelve `null` y el controlador responde `200` con cuerpo vacío.
- **Los POST/PUT correctos devuelven `200`**, no `201`.
- **Los DELETE devuelven `200`**; si el id no existía, devuelven vacío.
- En los demás controladores (`customers`, `questions`, `questionAndAnswers`, `roadmaps`) los errores de validación o de integridad pueden acabar en `500`.

---

## 8. Qué no está implementado todavía

| Tema | Estado |
|---|---|
| Cálculo automático del perfil a partir de las respuestas | No implementado; se aplaza hasta estabilizar modelo y React |
| Frontend React | No existe todavía |
| Interfaz de usuario final | Solo `/home1` y `/home2` de bienvenida |
| Siembra automática al arrancar | No implementada; `PopulatorDB` no se invoca |
| Paginación | No implementada |
| Validación con Bean Validation | No implementada |
| DTOs | No hay; se exponen las entidades JPA |
| Autenticación / autorización | No implementada |
| `GET /customerProfiles/byCustomerId` | No expuesto en REST |
| `AnswerRepository` | No existe; los `Answer` se guardan por cascada |
| Cálculo real de `score` | No existe; solo suma `scoreContribution` |

---

## 9. Problemas frecuentes

### La aplicación no arranca: `Database may be already in use`

H2 en fichero solo admite una conexión de escritura a la vez. Cierra:

- otra instancia de la aplicación,
- una consola H2 abierta contra la misma URL,
- o el `Shell` de H2.

### `Table "..." not found` o la base de datos parece vacía

Comprueba que la `SPRING_DATASOURCE_URL` apunta a la misma ruta que esperas. Si cambias de ruta, H2 crea una base nueva y vacía.

### El puerto 8080 está ocupado

Arranca en otro puerto:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"
```

### `LazyInitializationException` al leer un assessment

`AssessmentView.questionsAndAnswers` es `LAZY`. No navegues la colección desde fuera de una transacción. Usa:

```text
GET /api/v1/assessments/questionsAndAnswers?id=<ASSESSMENT_ID>
```

### `POST /questionAndAnswers/create` devuelve `500`

Ese controlador no tiene el `@RestControllerAdvice`. Si la pregunta no existe, el servicio lanza `IllegalArgumentException` y Spring lo trata como error interno. Verifica el `question.id` o crea las Q&A dentro de un assessment, donde sí se devuelve `404`.

### Un `POST` con `int`, `double` o `boolean` nulos falla

Los primitivos no aceptan `null` por defecto. El proyecto lo mitiga con:

```properties
spring.jackson.deserialization.fail-on-null-for-primitives=false
```

Aun así, es mejor enviar siempre `order`, `weight`, `steps` y `completed`.

### El alta de un perfil con `recommendedRoadmaps` falla o crea roadmaps raros

`POST /customerProfiles/create` no resuelve roadmaps por id. Crea el perfil sin roadmaps y asígnalos después con `PUT /customerProfiles/update`.

### `git status` muestra cambios en `db/compassmapdb.mv.db`

Estás ejecutando la aplicación contra la base de datos versionada. Usa una copia local fuera del repositorio y la variable `SPRING_DATASOURCE_URL`.

### `/` devuelve 404

Es normal. Solo existen `/home1` y `/home2`.

### `/h2-console` devuelve 404

La consola no está habilitada. Añade `spring.h2.console.enabled=true` o pásalo como argumento al arrancar.

---

## 10. Glosario

| Término | Significado |
|---|---|
| `Customer` | Cliente que realiza el autodiagnóstico |
| `Question` | Pregunta del catálogo, asociada a una `Dimension` |
| `Answer` | Valor booleano de una respuesta |
| `QuestionAndAnswer` | Une una pregunta con su respuesta y su `scoreContribution` |
| `AssessmentView` | Una ejecución completa del cuestionario |
| `CustomerProfile` | Perfil resultante de un cliente |
| `CodeProfile` | Tipo de perfil: `BEGINNER`, `ARCHITECT`, `NON_DEVELOPER` |
| `RoadMap` | Hoja de ruta de aprendizaje recomendada |
| `Dimension` | Dimensión evaluada: `KNOWS_CODE`, `WANTS_TO_LEARN`, `KNOWS_ML`, `WANTS_TO_LEARN_ML`, `KNOWS_GIT` |
| `scoreContribution` | Puntos que aporta una respuesta al total |
| `qtyQuestions` | Número de preguntas de un assessment |
| Owning side | Lado de la relación que tiene la FK y decide su valor |
| `mappedBy` | Lado inverso de una relación bidireccional |

---

## 11. Dónde seguir leyendo

| Documento | Para qué |
|---|---|
| `README.md` | Visión general, stack y arranque |
| `docs/compassMap_v2.md` | Documento principal del proyecto |
| `docs/appends/dataModel-v2.md` | Modelo de datos y diagrama de clases |
| `docs/appends/assessmentview-rest-v2.md` | Contrato REST de assessments |
| `docs/appends/jpa-model-current.md` | Guía didáctica del modelo JPA actual |
| `docs/appends/jpa-model-current.mmd` | Diagrama Mermaid reutilizable |
| `docs/appends/task3-assessmentview-pending.md` | Bitácora de la rama actual |
| `docs/mockup/*.json` | Datos de ejemplo para el backoffice |

---

## 12. Chuleta rápida

```text
Arrancar:        .\mvnw.cmd spring-boot:run
API:             http://localhost:8080/api/v1
Vistas:          /home1  /home2
H2:              http://localhost:8080/h2-console
Usuario H2:      albert / 1234

Crear cliente:   POST /customers/create
Crear pregunta:  POST /questions/create
Crear roadmap:   POST /roadmaps/create
Crear perfil:    POST /customerProfiles/create
Asociar roads:   PUT  /customerProfiles/update
Crear encuesta:  POST /assessments/create
Ver Q&A:         GET  /assessments/questionsAndAnswers?id=...
Ver score:       GET  /assessments/score?id=...
Borrar todo:     assessments → profiles → Q&A → roadmaps → questions → customers
```
