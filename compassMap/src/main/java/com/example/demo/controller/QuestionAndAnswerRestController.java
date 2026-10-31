package com.example.demo.controller;

import com.example.demo.dto.QuestionAndAnswerRequest;
import com.example.demo.model.QuestionAndAnswer;
import com.example.demo.service.QuestionAndAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// REST Controller is a annotation that marks the class as a controller
// that is a component that handles HTTP requests
@RestController
// RequestMapping is a annotation that maps HTTP requests to handler methods
// it is used to map the HTTP requests to the methods in the controller
@RequestMapping("/api/v1/questionAndAnswers")
public class QuestionAndAnswerRestController {
    // Autowired is an annotation that marks the field as a dependency
    @Autowired
    private QuestionAndAnswerService questionAndAnswerService;

    //----------------------------- CREATE -----------------------------
    // Body: { "questionId": "...", "answerId": "...", "scoreContribution": 2.5 }
    @PostMapping("/create")
    public QuestionAndAnswer createQuestionAndAnswer(@RequestBody QuestionAndAnswerRequest request) {
        QuestionAndAnswer newQuestionAndAnswer = questionAndAnswerService.createQuestionAndAnswer(request);
        System.out.println("Service createQuestionAndAnswer: " + newQuestionAndAnswer);
        return newQuestionAndAnswer;
    }

    @PostMapping("/createAll")
    public Iterable<QuestionAndAnswer> createAllQuestionAndAnswers(@RequestBody Iterable<QuestionAndAnswerRequest> requests) {
        Iterable<QuestionAndAnswer> newQuestionAndAnswers = questionAndAnswerService.saveAll(requests);
        System.out.println("Service createAllQuestionAndAnswers: " + newQuestionAndAnswers);
        return newQuestionAndAnswers;
    }

    //----------------------------- UPDATE -----------------------------
    // Body: { "id": "...", "scoreContribution": 4.5 }  (partial update)
    @PutMapping("/update")
    public QuestionAndAnswer updateQuestionAndAnswer(@RequestBody QuestionAndAnswerRequest request) {
        QuestionAndAnswer updatedQuestionAndAnswer = questionAndAnswerService.updateQuestionAndAnswer(request);
        System.out.println("Service updateQuestionAndAnswer: " + updatedQuestionAndAnswer);
        return updatedQuestionAndAnswer;
    }

    //----------------------------- GET -----------------------------
    @GetMapping("/all")
    public Iterable<QuestionAndAnswer> getAllQuestionAndAnswers() {
        // call service to get all questionAndAnswers and return them
        Iterable<QuestionAndAnswer> questionAndAnswers = questionAndAnswerService.getAllQuestionAndAnswers();
        System.out.println("Service getAllQuestionAndAnswers: " + questionAndAnswers);
        return questionAndAnswers;
    }

    @GetMapping("/byId")
    public QuestionAndAnswer getQuestionAndAnswerById(@RequestParam String id) {
        QuestionAndAnswer questionAndAnswer = questionAndAnswerService.getQuestionAndAnswerById(id);
        System.out.println("Service getQuestionAndAnswerById: " + questionAndAnswer);
        return questionAndAnswer;
    }

    // Traverses the @ManyToOne question association (question_id FK).
    @GetMapping("/byQuestionId")
    public List<QuestionAndAnswer> getQuestionAndAnswersByQuestionId(@RequestParam String questionId) {
        List<QuestionAndAnswer> questionAndAnswers = questionAndAnswerService.getQuestionAndAnswersByQuestionId(questionId);
        System.out.println("Service getQuestionAndAnswersByQuestionId: " + questionAndAnswers);
        return questionAndAnswers;
    }

    // Traverses the @ManyToOne answer association (answer_id FK).
    @GetMapping("/byAnswerId")
    public List<QuestionAndAnswer> getQuestionAndAnswersByAnswerId(@RequestParam String answerId) {
        List<QuestionAndAnswer> questionAndAnswers = questionAndAnswerService.getQuestionAndAnswersByAnswerId(answerId);
        System.out.println("Service getQuestionAndAnswersByAnswerId: " + questionAndAnswers);
        return questionAndAnswers;
    }

    @GetMapping("/count")
    public long countQuestionAndAnswers() {
        long count = questionAndAnswerService.countQuestionAndAnswers();
        System.out.println("Service countQuestionAndAnswers: " + count);
        return count;
    }

    //----------------------------- DELETE -----------------------------
    @DeleteMapping("/byId")
    public QuestionAndAnswer deleteById(@RequestParam String id) {
        // getQuestionAndAnswerById throws NoSuchElementException -> 404
        QuestionAndAnswer foundQuestionAndAnswer = questionAndAnswerService.getQuestionAndAnswerById(id);
        questionAndAnswerService.deleteQuestionAndAnswer(id);
        return foundQuestionAndAnswer;
    }

    @DeleteMapping("/all")
    public void deleteAllQuestionAndAnswers() {
        questionAndAnswerService.deleteAllQuestionAndAnswers();
    }
}
