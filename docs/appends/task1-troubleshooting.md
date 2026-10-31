# Task 1 — Correcciones y troubleshooting

> Registro de los fallos encontrados al implementar **Task 1 — `QuestionAndAnswer`
> como `@Entity`**, con la causa, la solución aplicada y cómo se verificó.
>
> Rama: `task1-qanda` · Documento principal: [task1-qanda.md](./task1-qanda.md) ·
> Colección Postman:
> [`docs/postman/compassMap-task1-questionAndAnswer.postman_collection.json`](../postman/compassMap-task1-questionAndAnswer.postman_collection.json)

---

## 1. Resumen de incidencias

| ID | Incidencia | Dónde apareció | Gravedad | Estado |
|---|---|---|---|---|
| FIX-01 | `Answer` mapeada como `@OneToOne` con `cascade = ALL` + `orphanRemoval` | `QuestionAndAnswer` de la rama | Alta: modelo incorrecto y riesgo de borrar catálogo compartido | Corregido |
| FIX-02 | Jackson 3 no puede deserializar una `Question` parcial (`{"question":{"id"}}`) | `POST /questionAndAnswers/create` | Alta: `400` con error de parseo | Corregido con DTO |
| FIX-03 | Servicio de Q&A con `TODO`: devolvía `null` / `count = 0` | `QuestionAndAnswerService` | Alta: la API “respondía” pero no persistía | Corregido |
| FIX-04 | Repositorio de Q&A vacío | `QuestionAndAnswerRepository` | Media: sin queries por asociación | Corregido |
| FIX-05 | `Answer` era `@Entity` pero sin repositorio/servicio/controlador | capa REST | Alta: no se podía crear la FK `answer_id` desde Postman | Corregido |
| FIX-06 | `GET /byId` devolvía `200` con body vacío cuando no existía | `QuestionAndAnswerRestController` | Media: contrato HTTP pobre | Corregido: `404` |
| FIX-07 | Sin validación de las FK: id inexistente llegaba a H2 | `QuestionAndAnswerService` | Alta: `500` + violación de FK | Corregido: `400` con mensaje |
| FIX-08 | Test `questionAndAnswerTentative` usaba una pregunta hardcodeada que podía no existir y una `Answer` transient | `DemoApplicationTests` | Alta: test frágil/falso positivo | Reescrito |
| FIX-09 | El snippet del masterDoc incluye `assessmentView`, pero `AssessmentView` no es `@Entity` | masterDoc v2 | Media: el contexto no arrancaría | Diferido a Task 3 |
| FIX-10 | `JAVA_HOME` apuntaba a JDK 11 y el proyecto exige Java 21 | entorno local | Bloqueante | Corregido en IntelliJ + comandos |
| FIX-11 | La app y los tests no pueden usar a la vez la misma BD H2 de fichero | entorno local | Operativa | Documentado |
| FIX-12 | El body de Postman cambió de anidado a plano al introducir el DTO | colección Postman | Media: ejemplos antiguos dan `400` | Colección actualizada |

> **No son bugs del código, pero se documentan como prevención:** `order` es palabra
> reservada en H2, `@Data` + relaciones bidireccionales puede crear bucles, y
> `mappedBy` no se pone nunca en el lado `@ManyToOne`. Ver sección 4.

---

## 2. Detalle de cada corrección

### FIX-01 — `@OneToOne` en `Answer` (modelo incorrecto)

**Síntoma / código encontrado en la rama:**

```java
@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
@JoinColumn(name = "answer_id")
private Answer answer;
```

**Causa:** se confundió “una Q&A tiene una respuesta” con una relación `1..1`. En el
dominio, `Answer` es un **catálogo** (`true` / `false`) y muchas Q&A pueden apuntar a
la misma fila:

| `question_and_answer.id` | `question_id` | `answer_id` |
|---|---|---|
| `qa-1` | `q-1` | `answer-true` |
| `qa-2` | `q-2` | `answer-true` |

Con `@OneToOne`, Hibernate genera una **restricción de unicidad** sobre `answer_id`:
la segunda fila falla. Además:

- `cascade = ALL` incluye `REMOVE`: borrar `qa-1` intentaría borrar `answer-true`,
  que `qa-2` sigue usando.
- `orphanRemoval = true` está pensado para hijos **propiedad** de un padre, no para
  valores compartidos de un catálogo.
- `PERSIST` en cascada puede intentar insertar otra vez la misma `Answer`
  (`primary key violation`).

**Solución aplicada:**

```java
@ManyToOne(optional = false, fetch = FetchType.EAGER)
@JoinColumn(name = "answer_id", nullable = false)
private Answer answer;
```

Sin cascade: `Answer` se crea antes y se comparte.

**Verificación:**

- Test JUnit: dos `QuestionAndAnswer` guardadas con la **misma** `Answer`;
  `findByAnswerId(...)` devuelve **2**.
- Postman request **10**: crea una segunda Q&A reutilizando `answerTrueId` y ambas
  conviven.

**Archivo:** `compassMap/src/main/java/com/example/demo/model/QuestionAndAnswer.java`

---

### FIX-02 — Jackson 3 y la entidad parcial (`Cannot map null into type int`)

**Síntoma real al probar la API:**

```text
JSON parse error: Cannot map `null` into type `int`
(set `DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES` to 'false' to allow)
... through reference chain:
QuestionAndAnswer["question"]->Question["order"]
```

El body enviado era el del masterDoc:

```json
{
  "question": { "id": "uuid-de-la-pregunta" },
  "answer":   { "id": "uuid-de-la-respuesta" },
  "scoreContribution": 2.5
}
```

**Causa:** Spring Boot 4 usa Jackson 3. `Question` tiene `@Builder` +
`@AllArgsConstructor` y campos primitivos (`int order`, `double weight`). Al
deserializar el objeto anidado, Jackson elige el constructor de todos los argumentos;
como el cliente solo envía `id`, intenta asignar `null` a `order` y falla.

**Solución aplicada:** no recibir entidades parciales. Se añadió un DTO plano:

```java
public record QuestionAndAnswerRequest(
        String id,                  // solo para PUT /update
        String questionId,          // FK -> question.id
        String answerId,            // FK -> answer.id
        Double scoreContribution
) {}
```

El body pasa a ser:

```json
{
  "questionId": "uuid-de-la-pregunta",
  "answerId":   "uuid-de-la-respuesta",
  "scoreContribution": 2.5
}
```

El servicio resuelve los ids con `QuestionRepository` / `AnswerRepository` y
construye la entidad con referencias gestionadas.

**Por qué esta solución y no otra:**

| Alternativa | Problema |
|---|---|
| Enviar la `Question` completa anidada | duplica datos y acopla el JSON a la entidad |
| `FAIL_ON_NULL_FOR_PRIMITIVES=false` global | oculta el problema y deja `order=0` silenciosamente |
| Cambiar `order`/`weight` a `Integer`/`Double` | cambia el modelo por una limitación del transporte |
| **DTO plano** | contrato explícito, entidad intacta, validación clara |

**Verificación:** `POST /create` con body plano → `200` y FK correctas; sin `answerId`
→ `400` con `answerId is required`.

**Archivos:**

- `compassMap/src/main/java/com/example/demo/dto/QuestionAndAnswerRequest.java`
- `compassMap/src/main/java/com/example/demo/controller/QuestionAndAnswerRestController.java`
- `compassMap/src/main/java/com/example/demo/service/QuestionAndAnswerService.java`

---

### FIX-03 — Servicio con `TODO` (no persistía)

**Síntoma / código encontrado:**

```java
public Iterable<QuestionAndAnswer> getAllQuestionAndAnswers() {
    // to do: return questionAndAnswerRepository.findAll();
    return null;
}

public long countQuestionAndAnswers() {
    // to do: return questionAndAnswerRepository.count();
    return 0;
}
```

`POST /create` generaba un UUID y devolvía el objeto, pero **no llamaba al
repositorio**. Desde fuera parecía funcionar; al consultar, no había filas.

**Solución:** inyectar el repositorio y usarlo en todas las operaciones:

```java
@Autowired
private QuestionAndAnswerRepository questionAndAnswerRepository;

public Iterable<QuestionAndAnswer> getAllQuestionAndAnswers() {
    return questionAndAnswerRepository.findAll();
}

public long countQuestionAndAnswers() {
    return questionAndAnswerRepository.count();
}
```

**Verificación:** `GET /all` devuelve la fila creada y `GET /count` aumenta.

**Archivo:** `compassMap/src/main/java/com/example/demo/service/QuestionAndAnswerService.java`

---

### FIX-04 — Repositorio vacío

**Síntoma:** `QuestionAndAnswerRepository` solo extendía `JpaRepository`; no había
forma de consultar “las Q&A de esta pregunta” ni “las Q&A con esta respuesta”.

**Solución:** queries derivadas que atraviesan la asociación:

```java
List<QuestionAndAnswer> findByQuestionId(String questionId);
List<QuestionAndAnswer> findByAnswerId(String answerId);
```

Spring Data genera `where question_id = ?` / `where answer_id = ?`. **No** busca una
columna `questionId` en la entidad: navega `question.id`.

**Verificación:** `GET /byQuestionId` y `GET /byAnswerId` devuelven las filas
esperadas; en el test, `findByAnswerId` devuelve 2 cuando dos Q&A comparten respuesta.

**Archivo:** `compassMap/src/main/java/com/example/demo/repository/QuestionAndAnswerRepository.java`

---

### FIX-05 — `Answer` sin capa REST

**Síntoma:** `Answer` ya era `@Entity`, pero no existía forma de crear una respuesta
desde Postman. Sin una fila en `answer`, la FK `answer_id` no se puede satisfacer.

**Solución:** se añadieron las tres capas siguiendo el patrón de `Question`:

- `repository/AnswerRepository.java`
- `service/AnswerService.java` (id UUID, CRUD, actualización parcial de `value`)
- `controller/AnswerRestController.java` (`/api/v1/answers`)

**Verificación:** `POST /api/v1/answers/create` con `{"value": true}` devuelve `id`.

---

### FIX-06 — `GET /byId` devolvía `200` con body vacío

**Síntoma / código encontrado:**

```java
Optional<QuestionAndAnswer> found = Optional.empty();
return found.orElse(null);   // -> 200 con body vacío
```

**Causa:** el controlador usaba `null` como “no encontrado”; HTTP no distingue eso de
un éxito sin contenido.

**Solución:** el servicio lanza `NoSuchElementException` y un
`@RestControllerAdvice` la traduce a `404`:

```java
return questionAndAnswerRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException(
                "QuestionAndAnswer not found: " + id));
```

```json
{
  "timestamp": "2026-10-31T16:34:04.267Z",
  "status": 404,
  "error": "Not Found",
  "message": "QuestionAndAnswer not found: 3cb7..."
}
```

**Verificación:** Postman request **14** (leer después de borrar) → `404`.

**Archivos:**

- `service/QuestionAndAnswerService.java`
- `exception/ApiExceptionHandler.java`

---

### FIX-07 — Sin validar las FK antes de guardar (`500`)

**Síntoma:** si el cliente enviaba un `questionId` inexistente, el servicio construía
la entidad y Hibernate/H2 fallaba al insertar:

```text
Referential integrity constraint violation:
FK... FOREIGN KEY (QUESTION_ID) REFERENCES QUESTION(ID)
```

Eso llegaba al cliente como `500`, sin explicar qué id estaba mal.

**Solución:** resolver cada referencia antes de construir la entidad y lanzar
`IllegalArgumentException` con un mensaje accionable:

```java
private Question requireQuestion(String questionId) {
    if (questionId == null || questionId.isBlank()) {
        throw new IllegalArgumentException(
                "questionId is required: a QuestionAndAnswer must reference an existing Question");
    }
    return questionRepository.findById(questionId)
            .orElseThrow(() -> new IllegalArgumentException(
                    "Question not found: " + questionId));
}
```

`ApiExceptionHandler` convierte `IllegalArgumentException` en `400`.

**Verificación (Postman 11 y 12):**

```json
{"status":400,"error":"Bad Request","message":"Question not found: does-not-exist"}
```

```json
{"status":400,"error":"Bad Request","message":"answerId is required: a QuestionAndAnswer must reference an existing Answer"}
```

---

### FIX-08 — Test `questionAndAnswerTentative` frágil

**Código encontrado (resumen):**

```java
Question question = repositoryQuestion
        .findById("7504a93c-a16c-4438-a140-4ae7dd326631")  // id hardcodeado
        .orElse(null);

QuestionAndAnswer qa1 = new QuestionAndAnswer();
qa1.setId("qa-001");
qa1.setQuestion(question);       // puede ser null
Answer answer = new Answer();
answer.setId("a-001");
qa1.setAnswer(answer);            // Answer transient, sin cascade
repositoryQuestionAndAnswer.save(qa1);
```

**Por qué falla o puede fallar:**

1. El id de la pregunta está hardcodeado: en una BD limpia no existe → `question`
   queda `null` y `question_id` es `NOT NULL`.
2. `Answer` no se ha guardado y la relación **no tiene cascade** → Hibernate lanza
   `TransientObjectException` / `object references an unsaved transient instance`.
3. El id `qa-001` es fijo: si el test se ejecuta dos veces sin limpiar, colisiona.
4. No hay assertions: aunque guardara, el test no comprobaría nada.

**Solución:** test determinista, con ids UUID, entidades persistidas primero y
verificación real de las FK:

```java
@Test
@Transactional
void questionAndAnswerManyToOneSharesAnswerAndPersistsForeignKeys() {
    Question question = Question.builder()
            .id(UUID.randomUUID().toString())
            .name("q-test")
            .text("Have you written code in any language before?")
            .dimension(Dimension.KNOWS_CODE)
            .order(999)
            .weight(1.0)
            .build();
    repositoryQuestion.save(question);

    Answer answer = Answer.builder()
            .id(UUID.randomUUID().toString())
            .value(true)
            .build();
    repositoryAnswer.save(answer);

    // dos Q&A comparten la misma Answer -> demuestra @ManyToOne
    ...
    assertEquals(question.getId(), found.getQuestion().getId());
    assertEquals(answer.getId(), found.getAnswer().getId());
    assertEquals(2, repositoryQuestionAndAnswer.findByAnswerId(answer.getId()).size());
}
```

`@Transactional` hace rollback al terminar y no deja datos de prueba.

**Verificación:**

```powershell
.\mvnw.cmd test -Dtest=DemoApplicationTests#questionAndAnswerManyToOneSharesAnswerAndPersistsForeignKeys
```

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
```

Y la suite completa:

```text
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
```

**Archivo:** `compassMap/src/test/java/com/example/demo/DemoApplicationTests.java`

---

### FIX-09 — `assessmentView` no se puede mapear en esta rama

**Síntoma:** el snippet de Task 1 del masterDoc incluye:

```java
@ManyToOne
@JoinColumn(name = "assessment_view_id")
private AssessmentView assessmentView;
```

Pero `AssessmentView` sigue con `@Entity` y `@Id` comentados.

**Causa:** `@ManyToOne` necesita una entidad gestionada por JPA. Mapear una clase que
no es `@Entity` rompe el arranque de Hibernate con:

```text
org.hibernate.AnnotationException:
@OneToOne or @ManyToOne on ... references an unknown entity: AssessmentView
```

Además, `assessment_view` todavía no existe como tabla.

**Solución:** diferir el campo a **Task 3**, dejando el `TODO` explícito:

```java
// TODO (Task 3): @ManyToOne assessmentView
// @ManyToOne(fetch = FetchType.LAZY)
// @JoinColumn(name = "assessment_view_id")
// private AssessmentView assessmentView;
// AssessmentView is not an @Entity yet, so this FK arrives with Task 3.
```

**Importante:** la FK seguirá viviendo en `question_and_answer` (lado “muchos”); lo
único que cambia en Task 3 es añadir la columna y el `@OneToMany(mappedBy =
"assessmentView")` en `AssessmentView`.

---

### FIX-10 — Java 21 vs `JAVA_HOME` con JDK 11

**Síntoma:** el proyecto declara `<java.version>21</java.version>`, pero el entorno
tenía:

```text
JAVA_HOME = C:\Program Files\Eclipse Adoptium\jdk-11.0.32.101-hotspot
```

Maven usa `JAVA_HOME`, así que compilar/arrancar con 11 falla (el proyecto no
soporta esa versión).

**Solución en IntelliJ:** ya se cambió el SDK del proyecto a 21 (correcto). También
conviene cambiar el **Project SDK** y el **Runner JRE**.

**Solución por terminal (PowerShell):**

```powershell
$env:JAVA_HOME = 'C:\Users\jrobl\.jdks\temurin-21.0.12.1'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version   # openjdk 21...
```

**Verificación:** `mvnw.cmd -DskipTests compile` termina sin errores y los tests
arrancan con `Java 21.0.12.1`.

---

### FIX-11 — Bloqueo de la BD H2 de fichero

**Síntoma posible:**

```text
Database may be already in use:
jdbc:h2:/.../compassmapdb
```

**Causa:** la aplicación arrancada (`spring-boot:run`) y los tests (`mvn test`) usan
la misma URL de `application.properties`, que es una BD H2 **en fichero**. H2 bloquea
el fichero para un solo proceso.

**Solución:** parar la app antes de lanzar los tests. En Windows:

```powershell
$c = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
if ($c) { $c.OwningProcess | Select-Object -Unique | ForEach-Object { Stop-Process -Id $_ -Force } }
```

Alternativa para tests aislados: usar H2 en memoria.

```properties
spring.datasource.url=jdbc:h2:mem:testdb
```

**No requiere cambio de código**, solo operativa.

---

### FIX-12 — Cambio de contrato en Postman (anidado → plano)

**Síntoma:** los ejemplos del masterDoc envían la entidad anidada; tras FIX-02 el
endpoint espera ids planos. Un ejemplo antiguo devuelve `400`.

| Antes (fallaba) | Después (correcto) |
|---|---|
| `{"question":{"id":"..."},"answer":{"id":"..."}}` | `{"questionId":"...","answerId":"..."}` |
| `PUT {"id":"...","question":{"id":"..."}}` | `PUT {"id":"...","questionId":"..."}` |

**Solución:** la colección ya está actualizada:

```text
docs/postman/compassMap-task1-questionAndAnswer.postman_collection.json
```

**Verificación:** las 15 requests se ejecutan en orden; las que comprueban FK leen
`body.question.id` / `body.answer.id` de la **respuesta** (la salida sigue siendo la
entidad completa anidada).

---

## 3. Verificación final

### 3.1 Compilación y tests

```powershell
cd compassMap
$env:JAVA_HOME = 'C:\Users\jrobl\.jdks\temurin-21.0.12.1'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

.\mvnw.cmd -DskipTests compile
.\mvnw.cmd test
```

Resultado observado:

```text
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
```

### 3.2 Pruebas manuales de la API (equivalentes a Postman)

| Operación | Resultado observado |
|---|---|
| `POST /answers/create` | `200`, id UUID |
| `POST /questions/create` | `200`, `name = q-101` |
| `POST /questionAndAnswers/create` | `200`, `question.id` y `answer.id` correctos |
| `GET /byId` | `200`, FK cargadas |
| `PUT /update` con solo `scoreContribution` | `200`, score `4.5`, relaciones intactas |
| Segunda Q&A con el mismo `answerId` | `200`, ids distintos, misma `Answer` |
| `GET /byQuestionId` | 2 filas |
| `GET /byAnswerId` | 2 filas |
| `POST` con `questionId` inexistente | `400`, `Question not found` |
| `POST` sin `answerId` | `400`, `answerId is required` |
| `DELETE /byId` | `200`, devuelve la fila |
| `GET /byId` tras borrar | `404`, `QuestionAndAnswer not found` |

### 3.3 Comprobación en H2

```sql
select id, question_id, answer_id, score_contribution
from question_and_answer;
```

Las dos filas de la prueba comparten el mismo `answer_id`: eso demuestra que la
relación es `n..1` y no `1..1`.

---

## 4. Prevenciones (no eran bugs en esta rama)

| Tema | Regla |
|---|---|
| `order` en H2 | es palabra reservada → `@Column(name = "question_order")` |
| `@Data` + relación bidireccional | `toString()`/`equals()` pueden entrar en bucle o forzar cargas LAZY → relación unidireccional o `@ToString.Exclude` / `@EqualsAndHashCode.Exclude` |
| `mappedBy` | solo en el **inverse side** (`@OneToMany`, `@OneToOne` inverse); **nunca** en `@ManyToOne` |
| `cascade` | solo cuando el hijo no existe sin el padre (composición); no en catálogos |
| `orphanRemoval` | solo para hijos propiedad del padre |
| `FetchType` | `@ManyToOne` es EAGER por defecto; `@OneToMany` es LAZY. Para listas grandes, `JOIN FETCH` / `@EntityGraph` |
| N+1 | con EAGER en ManyToOne y muchas filas, planificar `JOIN FETCH` |
| Entidades en `@RequestBody` | preferir DTOs: evita constructores parciales y acoplamiento JSON↔JPA |

---

## 5. Fuera de alcance (pendiente, no “roto”)

- `assessmentView` y `assessment_view_id` → **Task 3**.
- `@OneToMany` inverso en `Question` / `Answer` → opcional, no necesario.
- `CustomerProfile → Customer` y `→ RoadMap` → **Task 2**.
- `findByAssessmentViewId(...)` → **Task 3**.
- `PopulatorDB` con un assessment de ejemplo → cuando exista `AssessmentView`.
- Serialización con DTOs de respuesta → mejora futura; ahora se devuelve la entidad.

---

## 6. Enlaces

- Documento principal de Task 1: [task1-qanda.md](./task1-qanda.md)
- Modelo v2: [dataModel-v2.md](./dataModel-v2.md)
- MasterDoc v2: [compassMap_v2.md](../compassMap_v2.md)
- Colección Postman:
  [compassMap-task1-questionAndAnswer.postman_collection.json](../postman/compassMap-task1-questionAndAnswer.postman_collection.json)
