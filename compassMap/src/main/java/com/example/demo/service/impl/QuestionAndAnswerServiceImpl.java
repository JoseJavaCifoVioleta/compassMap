package com.example.demo.service.impl;

import com.example.demo.model.QuestionAndAnswer;
import com.example.demo.repository.QuestionAndAnswerRepository;
import com.example.demo.service.QuestionAndAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QuestionAndAnswerServiceImpl implements QuestionAndAnswerService {

    @Autowired
    private QuestionAndAnswerRepository qaRepository;

    @Override
    public List<QuestionAndAnswer> saveAll(List<QuestionAndAnswer> qaList) {
        return qaRepository.saveAll(qaList);
    }
}
