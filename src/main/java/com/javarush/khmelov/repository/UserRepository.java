package com.javarush.khmelov.repository;

import com.javarush.khmelov.config.SessionCreator;
import com.javarush.khmelov.entity.User;
import jakarta.transaction.Transactional;

@Transactional
public class UserRepository extends BaseRepository<User> {
    public UserRepository(SessionCreator sessionCreator) {
        super(sessionCreator, User.class);
    }
}
