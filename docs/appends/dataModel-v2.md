# Data Model v2 — CompassMap (JPA)

## Summary

`v1` was a **pre-JPA** model: plain Lombok POJOs, no foreign keys, and
`QuestionAndAnswer` / `AssessmentView` were not even entities.

`v2` turns the real relationships into JPA associations. Work is split into three
tasks, one branch each:

| Task | Scope | Status |
|---|---|---|
| Task 1 | `QuestionAndAnswer` → `Question` and `Answer` (`@ManyToOne`) | **implemented on `task1-qanda`** |
| Task 2 | `CustomerProfile` → `Customer` (`@OneToOne`) and `RoadMap` (`@ManyToOne`) | pending |
| Task 3 | `AssessmentView` → `Customer`, `List<QuestionAndAnswer>` (`@OneToMany`), `CustomerProfile` | pending |

> Deep dive for Task 1 (implemented code, JPA relationship walkthrough, Postman
> collection): [task1-qanda.md](./task1-qanda.md).

---

## AssessmentView section

`AssessmentView` is the **aggregate of one assessment run**: customer, timestamp,
number of questions, the `List<QuestionAndAnswer>` and the resulting
`CustomerProfile`.

- `@Entity` / `@Id` are still commented out → **Task 3**.
- It will be the **inverse side** of `QuestionAndAnswer.assessmentView`:
  `@OneToMany(mappedBy = "assessmentView", cascade = ALL, fetch = LAZY)`.
- The FK `assessment_view_id` will live in `question_and_answer` (the “many” side),
  so there is **no join table**.

---

## Question / Answer section (Q&A)

Task 1 is complete on branch `task1-qanda`:

- `QuestionAndAnswer` is an `@Entity` (`question_and_answer` table).
- `question` and `answer` are **`@ManyToOne`, unidirectional, owning side**:
  many Q&A rows can reference the same catalogue `Question` or `Answer`.
- `Answer` is an `@Entity` too, so it can be the target of the FK.
- No cascade: `Question` and `Answer` have their own lifecycle.
- `Question 1 ── * QuestionAndAnswer` and `Answer 1 ── * QuestionAndAnswer` are
  **logical** inverse sides; there is no `List<QuestionAndAnswer>` in either class
  yet, therefore **no `mappedBy`** in Task 1.

```mermaid
classDiagram
    direction LR

    class Question {
        <<Entity>>
        -String id
        -String name
        -String text
        -Dimension dimension
        -int order
        -double weight
    }

    class Answer {
        <<Entity>>
        -String id
        -Boolean value
    }

    class QuestionAndAnswer {
        <<Entity>>
        -String id
        -Question question
        -Answer answer
        -Double scoreContribution
    }

    QuestionAndAnswer "0..*" --> "1" Question : question (FK question_id)
    QuestionAndAnswer "0..*" --> "1" Answer : answer (FK answer_id)

    note for QuestionAndAnswer "OWNING SIDE<br/>question_and_answer stores<br/>question_id and answer_id"
    note for Question "No inverse collection yet"
    note for Answer "No inverse collection yet"

    classDef implemented fill:#d4edda,stroke:#28a745,stroke-width:2px
    class Question,Answer,QuestionAndAnswer implemented
```

---

## Customer / CustomerProfile section

Task 2 (pending) will add:

```java
// CustomerProfile — owning side
@OneToOne
@JoinColumn(name = "customer_id")
private Customer customer;

@ManyToOne
@JoinColumn(name = "roadmap_id")
private RoadMap recommendedRoadmap;
```

- **`@OneToOne` `customer`**: one profile belongs to one customer; the FK
  `customer_id` lives in `customer_profile`.
- **`@ManyToOne` `recommendedRoadmap`**: many profiles can recommend the same
  roadmap template; the FK `roadmap_id` lives in `customer_profile`.
- **Inverse sides (optional)**:

  ```java
  // Customer
  @OneToOne(mappedBy = "customer")
  private CustomerProfile profile;

  // RoadMap
  @OneToMany(mappedBy = "recommendedRoadmap", fetch = FetchType.LAZY)
  private List<CustomerProfile> profiles;
  ```

- `answers` stays as an `@ElementCollection Map<Dimension, Boolean>`: it is a map of
  basic types, **not** an entity relationship.

---

## Entities

| Entity | Role | Relationships (v2) |
|---|---|---|
| `Customer` | person taking the assessment | `1..n` `AssessmentView`, `1..1` `CustomerProfile` |
| `Question` | question catalogue (per `Dimension`) | `1..n` `QuestionAndAnswer` (logical) |
| `Answer` | a boolean answer value | `1..n` `QuestionAndAnswer` (logical) |
| `QuestionAndAnswer` | question + answer + `scoreContribution` | `n..1` `Question`, `n..1` `Answer`, `n..1` `AssessmentView` |
| `CustomerProfile` | resulting profile (`CodeProfile`) + answers map | `1..1` `Customer`, `n..1` `RoadMap` |
| `RoadMap` | learning path (`stepsItemsSummary`) | `1..n` `CustomerProfile` (inverse) |
| `AssessmentView` | one assessment run | `n..1` `Customer`, `1..n` `QuestionAndAnswer`, `n..1` `CustomerProfile` |

`Dimension` and `CodeProfile` are enums, not entities.

---

## Class diagram (mermaid)

```mermaid
classDiagram
    direction TB

    class Customer {
        -String id
        -String firstName
        -String lastName
        -String email
        -String phone
        -String address
    }

    class AssessmentView {
        -String id
        -Customer customer
        -Instant timestamp
        -int qtyQuestions
        -List~QuestionAndAnswer~ questionsAndAnswers
        -CustomerProfile profile
    }

    class QuestionAndAnswer {
        -String id
        -Question question
        -Answer answer
        -Double scoreContribution
    }

    class Question {
        -String id
        -String name
        -String text
        -Dimension dimension
        -int order
        -double weight
    }

    class Answer {
        -String id
        -Boolean value
    }

    class CustomerProfile {
        -String id
        -CodeProfile codeProfile
        -String name
        -String description
        -String goal
        -Map~Dimension, Boolean~ answers
        -Customer customer
        -RoadMap recommendedRoadmap
    }

    class RoadMap {
        -String id
        -String name
        -String description
        -Map~Integer, String~ stepsItemsSummary
        -int steps
        -boolean completed
    }

    Customer "1" --> "0..*" AssessmentView : assessments
    AssessmentView "0..*" --> "1" Customer : customer
    AssessmentView "1" --> "0..*" QuestionAndAnswer : questionsAndAnswers
    QuestionAndAnswer "0..*" --> "1" Question : question
    QuestionAndAnswer "0..*" --> "1" Answer : answer
    AssessmentView "0..*" --> "1" CustomerProfile : profile
    Customer "1" --> "0..1" CustomerProfile : profile
    CustomerProfile "0..*" --> "1" RoadMap : recommendedRoadmap

    note for AssessmentView "Task 3 (not an @Entity yet)"
    note for CustomerProfile "Task 2 (relations not mapped yet)"

    classDef task1 fill:#d4edda,stroke:#28a745,stroke-width:2px
    classDef pending fill:#f8f9fa,stroke:#adb5bd,stroke-dasharray:5 5
    class Question,Answer,QuestionAndAnswer task1
    class Customer,AssessmentView,CustomerProfile,RoadMap pending
```

Solid arrows show navigable associations; the cardinality is on the side of the
class it belongs to. `mappedBy` appears only on inverse sides (`@OneToMany` /
inverse `@OneToOne`), never on `@ManyToOne`.

---

## Notes

- **FK always lives on the “many” side** for `1..n` associations: here,
  `question_and_answer.question_id` and `question_and_answer.answer_id`.
- `@ManyToOne` is the **owning side**; `@OneToMany` is the **inverse side** and needs
  `mappedBy`.
- `@ManyToOne` defaults to `EAGER`; collections (`@OneToMany`) default to `LAZY`.
- In Task 1 there is **no cascade** to the catalogue. Cascading belongs to
  `AssessmentView → QuestionAndAnswer` (Task 3), a composition.
- `AssessmentView.id` / `@Entity` are commented out until Task 3, so
  `QuestionAndAnswer.assessmentView` is intentionally **not mapped** in this branch.
