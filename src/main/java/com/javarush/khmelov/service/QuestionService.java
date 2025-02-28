package com.javarush.khmelov.service;

import com.javarush.khmelov.dto.QuestionTo;
import com.javarush.khmelov.entity.Question;
import com.javarush.khmelov.mapping.Dto;
import com.javarush.khmelov.repository.Repository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.util.Optional;

@AllArgsConstructor
public class QuestionService {


    private final Repository<Question> questionRepository;
    private final Dto dto;

    public Optional<QuestionTo> get(long id) {
        return Optional
                .of(questionRepository.get(id))
                .map(dto::from);
    }

    @SneakyThrows
    @Transactional
    public Optional<QuestionTo> update(Long questionId, String text) {
        Question question = questionRepository
                .get(questionId);
        question.setText(text);
        questionRepository.update(question);
        return Optional
                .of(question)
                .map(dto::from);
    }
}
