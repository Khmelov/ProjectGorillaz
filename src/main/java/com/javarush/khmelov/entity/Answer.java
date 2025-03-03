package com.javarush.khmelov.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@BatchSize(size = 2)
@ToString
public class Answer implements AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_id")
    private Long questionId;

    private String text;

    private Long nextQuestionId;
}
