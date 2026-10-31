package com.example.demo.dto;

// Request DTO for Task 1.
//
// Why not receive a QuestionAndAnswer entity directly?
// The client only knows the ids of the Question and the Answer, so sending
// { "question": { "id": "..." } } would force Jackson to build a partial
// Question. Question has primitive fields (order, weight) and, with
// Spring Boot 4 / Jackson 3, the all-args constructor is selected and the
// request fails with "Cannot map null into type int".
//
// A flat DTO keeps the HTTP contract explicit and the entity untouched:
//   { "questionId": "...", "answerId": "...", "scoreContribution": 2.5 }
public record QuestionAndAnswerRequest(
        String id,              // only used by PUT /update (partial update)
        String questionId,      // FK -> question.id
        String answerId,        // FK -> answer.id
        Double scoreContribution
) {
}
