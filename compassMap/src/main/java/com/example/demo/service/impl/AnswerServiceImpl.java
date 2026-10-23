package com.example.demo.service.impl;

import com.example.demo.model.Answer;
import com.example.demo.repository.AnswerRepository;
import com.example.demo.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AnswerServiceImpl implements AnswerService {

    @Autowired
    private AnswerRepository answerRepository;

    @Override
    public List<Answer> saveAll(List<Answer> answers) {
        return answerRepository.saveAll(answers);
    }
}
