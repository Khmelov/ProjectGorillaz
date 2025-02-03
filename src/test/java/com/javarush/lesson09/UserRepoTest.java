package com.javarush.lesson09;

import com.javarush.khmelov.config.SessionCreator;
import com.javarush.khmelov.entity.Role;
import com.javarush.khmelov.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRepoTest {

    private SessionCreator sessionCreator;
    private UserRepo userRepo;

    @BeforeEach
    void setUp() {
        sessionCreator = new SessionCreator();
        userRepo = new UserRepo(sessionCreator);
    }



    @Test
    @DisplayName("When find by id then get user id=1 role=ADMIN")
    void get() {
        User user = userRepo.get(1L);
        assertEquals(1L, user.getId());
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    @DisplayName("When create+update+delete tempUser then no Exception")
    void createUpdateDelete() {
        User tempUser = User.builder()
                .login("login_test")
                .password("password_test")
                .role(Role.GUEST)
                .build();
        userRepo.create(tempUser);
        System.out.println("CREATE " + tempUser);

        tempUser.setPassword("password_test_update");
        userRepo.update(tempUser);
        System.out.println("UPDATE " + tempUser);

        userRepo.delete(tempUser);
        System.out.println("DELETE " + tempUser);
        assertTrue(tempUser.getId() > 0);
    }

    @AfterEach
    void tearDown() {
        sessionCreator.close();
    }
}