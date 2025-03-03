package com.javarush.khmelov.entity;

import com.javarush.khmelov.dto.GameState;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "answers")
public class Question implements AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quest_id")
    private Long questId;

    private String text;

    @Enumerated(EnumType.STRING)
    private GameState gameState;

    @OneToMany(mappedBy = "questionId")
    //@Fetch(value = FetchMode.SUBSELECT)
    private final Collection<Answer> answers = new ArrayList<>();
}
