package com.example.demo.controller;

import com.example.demo.model.Answer;
import com.example.demo.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Endpoints needed by Task 1: a QuestionAndAnswer needs an existing
// Answer row before it can store the answer_id foreign key.
@RestController
@RequestMapping("/api/v1/answers")
public class AnswerRestController {

    @Autowired
    private AnswerService answerService;

    //----------------------------- CREATE -----------------------------
    @PostMapping("/create")
    public Answer createAnswer(@RequestBody Answer answer) {
        Answer newAnswer = answerService.createAnswer(answer);
        System.out.println("Service createAnswer: " + newAnswer);
        return newAnswer;
    }

    @PostMapping("/createAll")
    public Iterable<Answer> createAllAnswers(@RequestBody Iterable<Answer> answers) {
        Iterable<Answer> newAnswers = answerService.saveAll(answers);
        System.out.println("Service createAllAnswers: " + answers);
        return newAnswers;
    }

    //----------------------------- UPDATE -----------------------------
    @PutMapping("/update")
    public Answer updateAnswer(@RequestBody Answer answer) {
        if (answer.getId() == null || answerService.getAnswerById(answer.getId()) == null) {
            return null;
        }
        Answer updatedAnswer = answerService.updateAnswer(answer);
        System.out.println("Service updateAnswer: " + updatedAnswer);
        return updatedAnswer;
    }

    //----------------------------- GET -----------------------------
    @GetMapping("/all")
    public Iterable<Answer> getAllAnswers() {
        Iterable<Answer> answers = answerService.getAllAnswers();
        System.out.println("Service getAllAnswers: " + answers);
        return answers;
    }

    @GetMapping("/byId")
    public Answer getAnswerById(@RequestParam String id) {
        Answer answer = answerService.getAnswerById(id);
        System.out.println("Service getAnswerById: " + answer);
        return answer;
    }

    @GetMapping("/count")
    public long countAnswers() {
        long count = answerService.countAnswers();
        System.out.println("Service countAnswers: " + count);
        return count;
    }

    //----------------------------- DELETE -----------------------------
    @DeleteMapping("/byId")
    public Answer deleteById(@RequestParam String id) {
        Answer foundAnswer = answerService.getAnswerById(id);
        if (foundAnswer == null) return null;
        else {
            answerService.deleteAnswer(id);
            return foundAnswer;
        }
    }

    @DeleteMapping("/all")
    public void deleteAllAnswers() {
        answerService.deleteAllAnswers();
    }
}
