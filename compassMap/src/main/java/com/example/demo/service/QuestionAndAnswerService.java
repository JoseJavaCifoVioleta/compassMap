package com.example.demo.service;

import com.example.demo.dto.QuestionAndAnswerRequest;
import com.example.demo.model.Answer;
import com.example.demo.model.Question;
import com.example.demo.model.QuestionAndAnswer;
import com.example.demo.repository.AnswerRepository;
import com.example.demo.repository.QuestionAndAnswerRepository;
import com.example.demo.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class QuestionAndAnswerService {

    // we are using here DEPENDENCE INJECTION
    @Autowired
    private QuestionAndAnswerRepository questionAndAnswerRepository;

    // Needed to turn the ids sent by the client into managed entities.
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    public Iterable<QuestionAndAnswer> getAllQuestionAndAnswers() {
        return questionAndAnswerRepository.findAll();
    }

    public QuestionAndAnswer createQuestionAndAnswer(QuestionAndAnswerRequest request) {
        // Resolve the two @ManyToOne references BEFORE saving:
        // the client only sends questionId / answerId and those rows must
        // already exist (FK question_id / answer_id).
        QuestionAndAnswer questionAndAnswer = QuestionAndAnswer.builder()
                .id(UUID.randomUUID().toString())
                .question(requireQuestion(request.questionId()))
                .answer(requireAnswer(request.answerId()))
                .scoreContribution(request.scoreContribution())
                .build();

        QuestionAndAnswer createdQuestionAndAnswer = questionAndAnswerRepository.save(questionAndAnswer);
        System.out.println("Service created questionAndAnswer: " + createdQuestionAndAnswer);
        return createdQuestionAndAnswer;
    }

    public void deleteQuestionAndAnswer(String id) {
        questionAndAnswerRepository.deleteById(id);
    }

    public QuestionAndAnswer getQuestionAndAnswerById(String id) {

        return questionAndAnswerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "QuestionAndAnswer not found: " + id));
    }

    public Iterable<QuestionAndAnswer> saveAll(Iterable<QuestionAndAnswerRequest> requests) {
        List<QuestionAndAnswer> toSave = new ArrayList<>();
        for (QuestionAndAnswerRequest request : requests) {
            toSave.add(QuestionAndAnswer.builder()
                    .id(UUID.randomUUID().toString())
                    .question(requireQuestion(request.questionId()))
                    .answer(requireAnswer(request.answerId()))
                    .scoreContribution(request.scoreContribution())
                    .build());
        }
        return questionAndAnswerRepository.saveAll(toSave);
    }

    public void deleteAllQuestionAndAnswers() {
        questionAndAnswerRepository.deleteAll();
    }

    public long countQuestionAndAnswers() {
        return questionAndAnswerRepository.count();
    }

    public List<QuestionAndAnswer> getQuestionAndAnswersByQuestionId(String questionId) {
        return questionAndAnswerRepository.findByQuestionId(questionId);
    }

    public List<QuestionAndAnswer> getQuestionAndAnswersByAnswerId(String answerId) {
        return questionAndAnswerRepository.findByAnswerId(answerId);
    }

    public QuestionAndAnswer updateQuestionAndAnswer(QuestionAndAnswerRequest request) {
        if (request.id() == null) {
            throw new IllegalArgumentException("id is required for a partial update");
        }
        QuestionAndAnswer existing = getQuestionAndAnswerById(request.id());
        // merge only non-null fields from the request (partial update semantics)
        if (request.questionId() != null) {
            existing.setQuestion(requireQuestion(request.questionId()));
        }
        if (request.answerId() != null) {
            existing.setAnswer(requireAnswer(request.answerId()));
        }
        if (request.scoreContribution() != null) {
            existing.setScoreContribution(request.scoreContribution());
        }
        return questionAndAnswerRepository.save(existing);
    }

    private Question requireQuestion(String questionId) {
        if (questionId == null || questionId.isBlank()) {
            throw new IllegalArgumentException(
                    "questionId is required: a QuestionAndAnswer must reference an existing Question");
        }
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Question not found: " + questionId));
    }

    private Answer requireAnswer(String answerId) {
        if (answerId == null || answerId.isBlank()) {
            throw new IllegalArgumentException(
                    "answerId is required: a QuestionAndAnswer must reference an existing Answer");
        }
        return answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Answer not found: " + answerId));
    }
}
