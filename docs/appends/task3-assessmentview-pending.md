# Trabajo pendiente — rama `task3-assessmentview-pending`

> Documento de seguimiento de los cambios realizados en la rama `task3-assessmentview-pending`.
>
> Está redactado en castellano para lectura del autor. Los nombres de clases, ficheros, rutas, tablas, commits y mensajes de commit se mantienen en inglés tal como aparecen en el código y en Git.

---

## 1. Contexto

| Dato | Valor |
|---|---|
| Rama | `task3-assessmentview-pending` |
| Commit base | `6494bf6` |
| Base de la rama | `master` local |
| `master` local | `6494bf6` = `origin/master` = `AlbertProfe/compassMap` `master` |
| Autor de los cambios | Jose Robles (`josejavacifovioleta@gmail.com`) |
| Fecha de inicio | 2026-11-08 |
| Estado | 3 pasos completados, sin `push` |

### Objetivo

Completar, paso a paso, los puntos que la documentación de las tareas deja redactados como pendientes:

- **Task 1** — `QuestionAndAnswer`
- **Task 2** — `CustomerProfile`
- **Task 3** — `AssessmentView`

### Restricciones acordadas

- Hacer **solo** lo que esté pedido literalmente en la documentación o en los comentarios `TODO` del código.
- No adelantar trabajo que un cambio de modelo o la futura implementación de React.js pueda invalidar.
- No introducir refactors, DTOs, validación con Bean Validation, paginación, `@Transactional`, `@EntityGraph` ni mejoras JPA no solicitadas.
- No cambiar las rutas REST existentes.
- No cambiar `Answer` de `@OneToOne` a `@ManyToOne`.
- No cambiar `CascadeType.ALL` ni `FetchType.EAGER` de `CustomerProfile.recommendedRoadmaps`.
- No sembrar la base de datos automáticamente.
- No tocar `master`, no hacer `push` y no commitear sin aprobación explícita.
- Usar una copia local de la base de datos H2 fuera del repositorio para no ensuciar `db/`.

### Criterio de literalidad

1. Mandan las especificaciones específicas de cada task.
2. Cuando hay contradicción, manda el documento posterior o más refinado.
3. Solo se implementan puntos redactados como pendientes (checklist, notas `Add…`, comentarios `TODO`).
4. Las recomendaciones de “buenas prácticas” que no estén pedidas quedan fuera.

---

## 2. Resumen de pasos

| Paso | Commit | Qué se hizo | Motivación | Origen | Tipo de origen |
|---|---|---|---|---|---|
| 1 | `c9af397` | Añadir `orphanRemoval = true` a `AssessmentView.questionsAndAnswers` | Completar la semántica de composición | `docs/appends/dataModel-v2.md` (nota de la línea 346 antes del cambio) | Documento |
| 2 | `75705a4` | Mapear errores del servicio de assessments a `404`/`400` | Evitar que un id inexistente devuelva `500` | `docs/appends/assessmentview-rest-v2.md` (nota de la línea 218 antes del cambio) | Documento |
| 3 | `ba08c69` | Resolver `recommendedRoadmaps` por `id` en `updateCustomerProfile` | Completar el `TODO` del servicio | `compassMap/src/main/java/com/example/demo/service/CustomerProfileService.java` (comentario `// to do: recommendedRoadmap…`, línea 91 antes del cambio) | Comentario de código |

Historial de la rama:

```text
ba08c69 feat: update recommended roadmaps in customer profile
75705a4 fix: map assessment service errors to 404/400
c9af397 fix: add orphanRemoval to AssessmentView questionsAndAnswers
6494bf6 feat: assessmentview-rest-v2.md updated with rebuild list qandas html published  ← master
```

> Los Pasos 4 y 5 (opcionales) se descartaron por decisión; el detalle está en la sección 6.2. El Paso 6 es el cierre documental recogido en este mismo documento.

---

## 3. Paso 1 — `orphanRemoval` en `AssessmentView`

**Commit:** `c9af397`  
**Mensaje:** `fix: add orphanRemoval to AssessmentView questionsAndAnswers`

### Motivación y origen

La nota de `docs/appends/dataModel-v2.md` indicaba que la relación usaba `cascade = CascadeType.ALL` pero **sin** `orphanRemoval = true`, y pedía añadirlo para completar la semántica de composición.

Es un cambio de **una línea** en el modelo.

### Cambio técnico

```diff
-    @OneToMany(mappedBy = "assessmentView", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
+    @OneToMany(mappedBy = "assessmentView", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
```

Diferencia entre las dos opciones:

- `cascade = CascadeType.ALL` propaga las operaciones del padre a los hijos. Al borrar el `AssessmentView`, se borran sus `QuestionAndAnswer` y, por la cascada de `QuestionAndAnswer.answer`, sus `Answer`.
- `orphanRemoval = true` añade además la semántica de composición: si un `QuestionAndAnswer` **se quita de la lista** del `AssessmentView` sin borrar el assessment, Hibernate lo elimina de la base de datos.

Con el cambio, la colección se comporta como una composición real: el hijo no existe fuera del padre.

### Efecto para el usuario / API

- El contrato REST **no cambia**: mismas rutas, mismos cuerpos, mismas respuestas.
- Al borrar un assessment se siguen borrando sus Q&A y answers.
- Si en el futuro se implementa una operación que quite una Q&A de la lista, esa fila ya no quedará huérfana en la base de datos.
- No se modificaron `QuestionAndAnswer` ni `Answer`.

### Archivos

- `compassMap/src/main/java/com/example/demo/model/AssessmentView.java`
- `docs/appends/dataModel-v2.md` (nota actualizada, indicando que el cambio lo hizo Jose Robles y no el autor del proyecto)

### Verificación

```powershell
C:\Users\jrobl\AppData\Local\compassMap\run-local.ps1 test "-Dtest=DemoApplicationTests#assessmentViewWithQuestionAndAnswers"
```

Resultado:

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

En el SQL del borrado se observa la eliminación explícita de las Q&A y sus answers:

```sql
delete from question_and_answer where id=?
delete from answer where id=?
delete from question_and_answer where id=?
delete from answer where id=?
delete from assessment_view where id=?
```

---

## 4. Paso 2 — Errores REST `404`/`400`

**Commit:** `75705a4`  
**Mensaje:** `fix: map assessment service errors to 404/400`

### Motivación y origen

La nota de `docs/appends/assessmentview-rest-v2.md` decía que las `IllegalArgumentException` lanzadas por el servicio no estaban mapeadas a `4xx`, por lo que un id inexistente devolvía `500`.

Criterio acordado:

- `404` cuando el recurso referenciado **no existe**.
- `400` para cualquier otro error de petición.

### Cambio técnico

Se añadieron dos clases nuevas:

- `compassMap/src/main/java/com/example/demo/exception/NotFoundException.java`
- `compassMap/src/main/java/com/example/demo/exception/RestExceptionHandler.java`

`NotFoundException` extiende `IllegalArgumentException` para conservar el contrato existente del servicio y, a la vez, ser más específica que él:

```java
public class NotFoundException extends IllegalArgumentException {

    public NotFoundException(String message) {
        super(message);
    }
}
```

`RestExceptionHandler` es un `@RestControllerAdvice` mínimo:

```java
@RestControllerAdvice(assignableTypes = {AssessmentViewRestController.class, CustomerProfileRestController.class})
public class RestExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", exception.getMessage()));
    }
}
```

> En el Paso 2 el advice se limitó a `AssessmentViewRestController`. En el Paso 3 se amplió a `CustomerProfileRestController`, porque ese servicio pasó a lanzar las mismas excepciones.

En `AssessmentViewService` se cambiaron los tres casos de “no encontrado”:

```diff
-  .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + ...)));
+  .orElseThrow(() -> new NotFoundException("Customer not found: " + ...)));

-  .orElseThrow(() -> new IllegalArgumentException("CustomerProfile not found: " + ...)));
+  .orElseThrow(() -> new NotFoundException("CustomerProfile not found: " + ...)));

-  .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId)));
+  .orElseThrow(() -> new NotFoundException("Question not found: " + questionId)));
```

El caso de una Q&A sin `question.id` sigue siendo `IllegalArgumentException` y, por tanto, `400`.

### Efecto para el usuario / API

Antes:

- `POST /api/v1/assessments/create` con un customer, profile o question inexistente devolvía `500`.
- El cliente no recibía un mensaje útil.

Ahora:

| Caso | Antes | Ahora |
|---|---|---|
| Customer inexistente | `500` | `404 {"error":"Customer not found: …"}` |
| CustomerProfile inexistente | `500` | `404 {"error":"CustomerProfile not found: …"}` |
| Question inexistente | `500` | `404 {"error":"Question not found: …"}` |
| Q&A sin `question.id` | `500` | `400 {"error":"QuestionAndAnswer requires an existing question id"}` |
| Create válido | `200` | `200` (sin cambios) |

`GET /api/v1/assessments/byId?id=…` **no se modificó**: sigue devolviendo `200` con cuerpo vacío cuando el id no existe. Es una decisión consciente para no cambiar el contrato actual.

### Archivos

- `compassMap/src/main/java/com/example/demo/exception/NotFoundException.java` (nuevo)
- `compassMap/src/main/java/com/example/demo/exception/RestExceptionHandler.java` (nuevo)
- `compassMap/src/main/java/com/example/demo/service/AssessmentViewService.java`
- `docs/appends/assessmentview-rest-v2.md` (nota actualizada)

### Verificación

Con la aplicación arrancada contra la copia local de H2:

| Caso | Resultado |
|---|---|
| Customer inexistente | `404 {"error":"Customer not found: no-existe"}` |
| CustomerProfile inexistente | `404 {"error":"CustomerProfile not found: no-existe"}` |
| Question inexistente | `404 {"error":"Question not found: no-existe"}` |
| Q&A sin question id | `400 {"error":"QuestionAndAnswer requires an existing question id"}` |
| Create válido | `200` |
| Borrado de limpieza | `200` |

También se ejecutó el test de regresión `assessmentViewWithQuestionAndAnswers`: `1 test, 0 failures`.

---

## 5. Paso 3 — `recommendedRoadmaps` en `updateCustomerProfile`

**Commit:** `ba08c69`  
**Mensaje:** `feat: update recommended roadmaps in customer profile`

### Motivación y origen

`CustomerProfileService.updateCustomerProfile` contenía este comentario:

```java
// to do: recommendedRoadmap once the relationship is enabled
```

La relación `@ManyToMany` ya estaba habilitada, así que el punto pendiente era actualizar `recommendedRoadmaps` durante el `PUT /api/v1/customerProfiles/update`.

### Decisión previa

Los roadmaps son **entidades compartidas**, no propiedad del perfil. Por tanto, la lista entrante se resuelve **por `id` contra `RoadMapRepository`**:

- Se evita confiar en objetos `RoadMap` enviados por el cliente.
- Se evita que `CascadeType.ALL` cree roadmaps nuevos sin querer.
- Se mantiene el mismo patrón que ya usaba `AssessmentViewService` para `Customer`, `CustomerProfile` y `Question`.

### Cambio técnico

Se inyecta `RoadMapRepository` y se añade el bloque:

```java
if (incoming.getRecommendedRoadmaps() != null) {
    // roadmaps are shared associations, not created here: resolve each one by id
    List<RoadMap> resolvedRoadMaps = new ArrayList<>();
    for (RoadMap roadMap : incoming.getRecommendedRoadmaps()) {
        if (roadMap == null || roadMap.getId() == null) {
            throw new IllegalArgumentException("Recommended roadmap requires an existing roadmap id");
        }
        resolvedRoadMaps.add(roadMapRepository.findById(roadMap.getId())
                .orElseThrow(() -> new NotFoundException("RoadMap not found: " + roadMap.getId())));
    }
    existing.setRecommendedRoadmaps(resolvedRoadMaps);
}
```

Semántica del update:

- Si `recommendedRoadmaps` es `null` (no viene en el JSON), **no se toca** la lista actual.
- Si viene una lista, se reemplaza por completo por las entidades resueltas.
- Una lista vacía `[]` limpia la relación.
- Un roadmap sin `id` devuelve `400`.
- Un roadmap con `id` inexistente devuelve `404`.
- Si algo falla, no se guarda nada: la excepción se lanza antes de `save`.

Además, se amplió `RestExceptionHandler` a `CustomerProfileRestController` para que estos errores no acabaran en `500`:

```diff
-@RestControllerAdvice(assignableTypes = AssessmentViewRestController.class)
+@RestControllerAdvice(assignableTypes = {AssessmentViewRestController.class, CustomerProfileRestController.class})
```

### Efecto para el usuario / API

| Caso | Antes | Ahora |
|---|---|---|
| `PUT /update` con `recommendedRoadmaps` | Se ignoraba la lista | Se actualiza la tabla `customer_profile_roadmap` |
| Roadmap sin `id` | Se ignoraba / comportamiento indefinido | `400 {"error":"Recommended roadmap requires an existing roadmap id"}` |
| Roadmap inexistente | Posible creación accidental o error | `404 {"error":"RoadMap not found: …"}` |
| Roadmap existente | No se aplicaba | Se asocia al perfil sin crear un roadmap nuevo |

### Archivos

- `compassMap/src/main/java/com/example/demo/service/CustomerProfileService.java`
- `compassMap/src/main/java/com/example/demo/exception/RestExceptionHandler.java`

### Verificación

| Caso | Resultado |
|---|---|
| Roadmap sin `id` | `400 {"error":"Recommended roadmap requires an existing roadmap id"}` |
| Roadmap inexistente | `404 {"error":"RoadMap not found: no-existe"}` |
| Update con roadmap existente | `200`; el id aparece en la respuesta y al releer el perfil |
| Roadmaps en BD antes/después | `10` / `10` → no se creó ninguno |
| Restauración del perfil de prueba | `200`; vuelve a su lista original (vacía) |

También se ejecutó el test de regresión `assessmentViewWithQuestionAndAnswers`: `1 test, 0 failures`.

---

## 6. Cobertura de los puntos pendientes

### 6.1 Puntos completados o ya presentes

| Punto | Origen | Estado | Referencia |
|---|---|---|---|
| `orphanRemoval = true` en `AssessmentView.questionsAndAnswers` | `docs/appends/dataModel-v2.md` (nota de la línea 346) | **Hecho** | `c9af397` |
| Errores del servicio de assessments mapeados a `404`/`400` | `docs/appends/assessmentview-rest-v2.md` (nota de la línea 218) | **Hecho** | `75705a4` |
| `recommendedRoadmaps` en `updateCustomerProfile` | `CustomerProfileService.java` (comentario `// to do: recommendedRoadmap…`, línea 91 antes del cambio) | **Hecho** | `ba08c69` |
| `QuestionAndAnswerRepository.findByAssessmentViewId(...)` | `docs/compassMap_v2.md` (checklist, línea 311) | **Ya existía** | Sin cambios |
| `AssessmentViewRepository` | `docs/compassMap_v2.md` (checklist, línea 313) | **Ya existía** | Sin cambios |
| `Answer` como `@Entity` | `docs/compassMap_v2.md` (nota de Task 1) | **Ya existía** | Sin cambios |

### 6.2 Puntos no realizados o aplazados

| Punto | Dónde está | Decisión |
|---|---|---|
| `findByCustomerId` devuelve `Optional` | `CustomerProfileRepository.java` (línea 10) | **No hecho**: el método del servicio que lo usa no está expuesto en ningún controller. El cambio a `List` no aporta nada hoy y podría rehacerse si cambia el modelo. Queda documentado como limitación conocida. |
| `AnswerRepository` | `docs/compassMap_v2.md` (checklist, línea 312) | **No hecho**: nadie lo usaría; los `Answer` se guardan por cascada desde `QuestionAndAnswer`. Sería código muerto. |
| “Submit questionnaire, compute profile” | `docs/compassMap_v2.md` (checklist, línea 314) | **Aplazado** hasta que el modelo y React.js estén claros. Requiere decidir dimensiones, puntuaciones, catálogo de perfiles y contrato de API. |
| Siembra automática de `PopulatorDB` | `docs/compassMap_v2.md` (checklist, línea 315) | **No hecho**: los métodos existen, pero invocarlos puede duplicar datos o interferir en tests. |
| `CascadeType.ALL` / `EAGER` en `CustomerProfile.recommendedRoadmaps` | `dataModel-v2.md` (recomendación, línea 347) | **No tocar**: la Task 2 los pide literalmente. |
| `Answer` de `@OneToOne` a `@ManyToOne` | Documentación contradictoria | **No tocar**: el documento posterior acepta `@OneToOne`. |
| Rutas REST antiguas (`GET/POST /assessments`) | Checklist antiguo vs. doc REST v2 | **No tocar**: el documento REST posterior las supera. |
| DTOs, validación, paginación, `@Transactional`, `@EntityGraph`, N+1 | Notas de “pitfalls” | **No hacer**: mejoras no solicitadas. |

### 6.3 Nota sobre los documentos del autor

Las casillas de `docs/compassMap_v2.md` (líneas 311–315) **no se han marcado** deliberadamente. Ese documento pertenece al autor del proyecto y puede cambiar en cualquier momento; modificarlo desde esta rama aumentaría el riesgo de conflicto sin aportar nada al código.

El estado real queda registrado en **este documento**, que es propio de la rama.

Las únicas excepciones son las notas de `dataModel-v2.md` y `assessmentview-rest-v2.md` actualizadas en los Pasos 1 y 2, que ya están commiteadas y se dejan como están.

---

## 7. Entorno local

Todo el entorno está **fuera del repositorio**, para que `git status` quede limpio:

| Recurso | Ruta |
|---|---|
| Copia de la BD H2 | `C:\Users\jrobl\AppData\Local\compassMap\db\compassmapdb.mv.db` |
| Variables de entorno | `C:\Users\jrobl\AppData\Local\compassMap\env.ps1` |
| Ejecutor de Maven | `C:\Users\jrobl\AppData\Local\compassMap\run-local.ps1` |
| Instrucciones | `C:\Users\jrobl\AppData\Local\compassMap\README-local.md` |

Uso:

```powershell
# arrancar la aplicación
C:\Users\jrobl\AppData\Local\compassMap\run-local.ps1

# ejecutar tests
C:\Users\jrobl\AppData\Local\compassMap\run-local.ps1 test

# ejecutar un test concreto
C:\Users\jrobl\AppData\Local\compassMap\run-local.ps1 test "-Dtest=DemoApplicationTests#assessmentViewWithQuestionAndAnswers"
```

La aplicación usa:

- Java 21 (`temurin-21.0.12.1`)
- Spring Boot 4.1.1
- H2 2.4.240
- JDBC URL local: `jdbc:h2:file:C:/Users/jrobl/AppData/Local/compassMap/db/compassmapdb`
- Usuario: `albert`
- Contraseña: `1234`

> `db/compassmapdb.mv.db` está trackeada en Git. Por eso no se ejecuta nada contra la carpeta `db/` del repositorio: se usa siempre la copia local.

---

## 8. Protocolo de trabajo

1. Se elige un paso del plan.
2. Se implementa **solo** ese paso.
3. Se muestra el diff.
4. Se verifica con la copia local de la BD (test o petición HTTP).
5. El autor revisa y aprueba.
6. Se hace un commit pequeño y separado.
7. Si aparece una ambigüedad de especificación, se para y se pregunta antes de inventar.

---

## 9. Revisión previa al push

> Revisión realizada el 2026-11-08, antes de publicar la rama en GitHub.

### 9.1. Comprobaciones

| Comprobación | Resultado |
|---|---|
| Rama | `task3-assessmentview-pending` |
| Respecto a `origin/master` | `0` detrás / `7` delante (antes de este cambio) |
| Árbol de trabajo | limpio |
| Rama remota | todavía no existe en `origin` |
| Ficheros cambiados | 5 de código + 6 de documentación |
| Compilación | correcta (33 fuentes) |
| Test de regresión | `DemoApplicationTests#assessmentViewWithQuestionAndAnswers`: 1 test, 0 fallos |
| `master` | sin tocar (`6494bf6`) |
| CI / GitHub Actions | no hay workflows |
| Secretos o `db/` | sin cambios nuevos |

### 9.2. Documentos del autor

Siguen modificados dos documentos del autor, con **una línea cambiada en cada uno**:

- `docs/appends/dataModel-v2.md`
- `docs/appends/assessmentview-rest-v2.md`

Los cambios están atribuidos explícitamente a Jose Robles (student) y limitados a las notas de `orphanRemoval` y del manejo de errores.

No son bloqueantes. Si el autor actualiza esos documentos, puede aparecer un conflicto pequeño al hacer merge o PR. Como ya están aprobados y commiteados, no se revierten.

### 9.3. Suite de tests

`DemoApplicationTests` **no es una buena puerta de calidad completa** porque es stateful y destructiva:

- `javaFakerCreateObjects` crea 1.000 clientes.
- `deleteCustomerById` borra un cliente con id fijo.
- `questionAndAnswerTentative` depende de una pregunta con id fijo.
- `roadMapAndCustomerProfile` inserta roadmaps y perfiles `P01..P04`.
- Todos escriben en la base de datos real configurada.

Por eso **no se ejecuta `mvn test` completo antes del push**. El test `assessmentViewWithQuestionAndAnswers` sí es autónomo, limpia lo que crea y ha pasado.

Mejora futura, fuera del alcance de esta rama: tests con base de datos en memoria, fixtures propios y limpieza o rollback.

### 9.4. Pendientes funcionales

Siguen fuera de esta rama, tal como se decidió en la sección 6.2:

- cálculo automático del perfil a partir de las respuestas;
- `AnswerRepository`;
- siembra automática de `PopulatorDB`;
- `findByCustomerId` devolviendo `Optional` en perfiles;
- frontend React.

Deben abordarse en otra rama cuando el modelo y React estén claros.

### 9.5. Nombre de la rama

La rama se llama `task3-assessmentview-pending`, pero contiene trabajo de Task 1, Task 2, Task 3 y documentación. No se renombra: cambiarlo ahora rompe la trazabilidad de los commits y de esta bitácora.

### 9.6. Push propuesto

```powershell
git push -u origin task3-assessmentview-pending
```

Efectos:

- crea la rama en `https://github.com/JoseJavaCifoVioleta/compassMap`;
- deja `master` intacto;
- configura el seguimiento local de la rama.

Después se puede abrir un PR contra:

- `JoseJavaCifoVioleta/compassMap:master`, o
- `AlbertProfe/compassMap:master`.

---

## 10. Estado actual

| Dato | Valor |
|---|---|
| Rama | `task3-assessmentview-pending` |
| HEAD | el commit de este cierre (consultar `git log --oneline -1`) |
| `master` | `6494bf6` (sin tocar) |
| Commits nuevos vs `master` | 8 (incluidos los commits de documentación) |
| Pasos 1–3 | completados |
| Pasos 4–5 | no realizados por decisión (ver sección 6.2) |
| Paso 6 | cierre documental en este mismo documento |
| Revisión previa al push | completada (sección 9) |
| Árbol de trabajo | limpio antes de este cambio |
| `push` | no realizado |
| Base de datos del repo | sin tocar |
| Documentos del autor | sin nuevos cambios desde el Paso 2 |
