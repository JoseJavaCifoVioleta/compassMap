package com.example.demo.service;

import com.example.demo.model.Answer;
import com.example.demo.repository.AnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AnswerService {

    // we are using here DEPENDENCE INJECTION
    @Autowired
    private AnswerRepository answerRepository;

    public Iterable<Answer> getAllAnswers() {
        return answerRepository.findAll();
    }

    public Answer createAnswer(Answer answer) {
        // SET ID answer to UUID (id is not sent from client)
        answer.setId(UUID.randomUUID().toString());
        Answer createdAnswer = answerRepository.save(answer);
        System.out.println("Service created answer: " + createdAnswer);
        return createdAnswer;
    }

    public void deleteAnswer(String id) {
        answerRepository.deleteById(id);
    }

    public Answer getAnswerById(String id) {

        Optional<Answer> foundAnswer = answerRepository.findById(id);

        return foundAnswer.orElse(null);
    }

    public Iterable<Answer> saveAll(Iterable<Answer> answers) {
        for (Answer answer : answers) {
            // SET ID answer to UUID (id is not sent from client)
            answer.setId(UUID.randomUUID().toString());
        }
        return answerRepository.saveAll(answers);
    }

    public void deleteAllAnswers() {
        answerRepository.deleteAll();
    }

    public long countAnswers() {
        return answerRepository.count();
    }

    public Answer updateAnswer(Answer incoming) {
        if (incoming.getId() == null) {
            return null;
        }
        Answer existing = getAnswerById(incoming.getId());
        if (existing == null) {
            return null;
        }
        // merge only non-null fields from incoming (partial update semantics)
        if (incoming.getValue() != null) {
            existing.setValue(incoming.getValue());
        }
        return answerRepository.save(existing);
    }
}
