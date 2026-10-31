package com.example.demo.repository;

import com.example.demo.model.QuestionAndAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionAndAnswerRepository extends JpaRepository<QuestionAndAnswer, String> {

    // Derived query: traverses the question association and compares question.id.
    List<QuestionAndAnswer> findByQuestionId(String questionId);

    // Derived query: traverses the answer association and compares answer.id.
    List<QuestionAndAnswer> findByAnswerId(String answerId);

    // TODO (Task 3): List<QuestionAndAnswer> findByAssessmentViewId(String assessmentViewId);
}
