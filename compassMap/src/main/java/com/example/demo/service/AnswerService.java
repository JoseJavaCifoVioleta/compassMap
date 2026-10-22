package com.example.demo.service;

import com.example.demo.model.Answer;
import java.util.List;

public interface AnswerService {
    List<Answer> saveAll(List<Answer> answers);
}
