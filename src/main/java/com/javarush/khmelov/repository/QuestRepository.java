package com.javarush.khmelov.repository;

import com.javarush.khmelov.config.SessionCreator;
import com.javarush.khmelov.entity.Quest;
import jakarta.transaction.Transactional;

@Transactional
public class QuestRepository extends BaseRepository<Quest> {
    public QuestRepository(SessionCreator sessionCreator) {
        super(sessionCreator, Quest.class);
    }
}
