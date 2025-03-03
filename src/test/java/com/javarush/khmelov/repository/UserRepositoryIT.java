package com.javarush.khmelov.repository;

import com.javarush.khmelov.ContainerIT;
import com.javarush.khmelov.config.NanoSpring;
import com.javarush.khmelov.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class UserRepositoryIT extends ContainerIT {

    private final UserRepository userRepository = NanoSpring.find(UserRepository.class);
    private User admin;

    @BeforeEach
    void createAdmin() {
        admin = userRepository.get(1L);
    }

    @Test
    void get() {
        User user = userRepository.get(admin.getId());
        Assertions.assertEquals(admin, user);
    }


    @Test
    void find() {
        User pattern = User.builder().login("Carl").build();
        var userStream = userRepository.find(pattern);
        Assertions.assertEquals(admin, userStream.findFirst().orElseThrow());
    }

    @Test
    void update() {
        admin.setLogin("newLogin");
        userRepository.update(admin);
        User user = userRepository.get(admin.getId());
        Assertions.assertEquals(admin, user);
    }

}