package com.javarush.khmelov.repository;

import com.javarush.khmelov.entity.User;
import com.javarush.khmelov.config.SessionCreator;

public class UserRepository extends BaseRepository<User> {


    public UserRepository(SessionCreator sessionCreator) {
        super(sessionCreator, User.class);
    }
}
