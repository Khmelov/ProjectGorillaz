package com.javarush.khmelov.repository;

import com.javarush.khmelov.config.SessionCreator;
import com.javarush.khmelov.entity.Question;
import jakarta.transaction.Transactional;

@Transactional
public class QuestionRepository extends BaseRepository<Question> {
    public QuestionRepository(SessionCreator sessionCreator) {
        super(sessionCreator, Question.class);
    }
}
