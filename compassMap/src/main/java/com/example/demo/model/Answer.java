package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "answers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Answer {

    @Id
    private String id;

    private Boolean value;

    // Equivalente a +getValue(): Boolean provisto por @Data

    public boolean isCorrect(Boolean expected) {
        if (this.value == null || expected == null) {
            return false;
        }
        return this.value.equals(expected);
    }
}
