package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAndAnswer {

    @Id
    private String id;

    // OWNING SIDE of Question 1..n QuestionAndAnswer.
    // The FK column question_id lives in the question_and_answer table.
    // optional = false -> the column is NOT NULL at the JPA level.
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // OWNING SIDE of Answer 1..n QuestionAndAnswer.
    // Many Q&A rows can point to the same Answer row (true / false are
    // catalogue values), so this is MANY-to-ONE, not one-to-one.
    // No cascade: the Answer is a shared catalogue row and must exist first.
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "answer_id", nullable = false)
    private Answer answer;

    private Double scoreContribution;

    // TODO (Task 3): @ManyToOne assessmentView
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "assessment_view_id")
    // private AssessmentView assessmentView;
    // AssessmentView is not an @Entity yet, so this FK arrives with Task 3.
}
