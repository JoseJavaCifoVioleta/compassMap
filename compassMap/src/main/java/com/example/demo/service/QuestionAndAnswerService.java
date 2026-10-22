package com.example.demo.service;

import com.example.demo.model.QuestionAndAnswer;
import java.util.List;

public interface QuestionAndAnswerService {
    List<QuestionAndAnswer> saveAll(List<QuestionAndAnswer> qaList);
}
