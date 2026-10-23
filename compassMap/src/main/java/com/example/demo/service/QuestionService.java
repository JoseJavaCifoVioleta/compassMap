package com.example.demo.service;

import com.example.demo.model.Question;
import java.util.List;

public interface QuestionService {
    List<Question> saveAll(List<Question> questions);
}
