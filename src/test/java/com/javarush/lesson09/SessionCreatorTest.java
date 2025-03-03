package com.javarush.lesson09;

import com.javarush.khmelov.config.NanoSpring;
import com.javarush.khmelov.config.SessionCreator;
import org.hibernate.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SessionCreatorTest {

    @Test
    void getSession() {
        SessionCreator sessionCreator = NanoSpring.find(SessionCreator.class);
        Session session = sessionCreator.getSession();
        Assertions.assertNotNull(session);
    }
}