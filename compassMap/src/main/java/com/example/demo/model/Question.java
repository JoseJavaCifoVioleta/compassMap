package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "questions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    private String id;

    private String text;

    @Enumerated(EnumType.STRING)
    private Dimension dimension;

    // Mantenemos el nombre 'order' en Java pero cambiamos el nombre de la columna en SQL
    // para evitar conflictos con la palabra reservada 'ORDER BY'
    @Column(name = "sort_order")
    private int order;

    private double weight;
}
