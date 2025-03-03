package com.javarush.khmelov.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
public class QuestionTo {
    Long id;
    Long questId;
    String text;
    Collection<AnswerTo> answers;

    public String getImage() {
        return "question-" + id;
    }
}