package com.javarush.lesson10;

import com.javarush.khmelov.config.NanoSpring;
import com.javarush.khmelov.config.SessionCreator;
import com.javarush.khmelov.dto.Role;
import com.javarush.khmelov.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRepoFullTest {

    private SessionCreator sessionCreator;
    private UserRepoFull userRepo;

    public static Stream<Arguments> getSamplePatternForSearch() {
        //several users with different nullable fields (need skipped)
        return Stream.of(
                Arguments.of(User.builder().login("Carl").password("admin").build(), 1),
                Arguments.of(User.builder().login("Carl").password("badpass").build(), 0),
                Arguments.of(User.builder().login("Carl").build(), 1),

                Arguments.of(User.builder().login("Bob").build(), 1),
                Arguments.of(User.builder().password("123").build(), 1),
                Arguments.of(User.builder().role(Role.GUEST).build(), 1),

                Arguments.of(User.builder().login("Alisa").password("qwerty").build(), 1),
                Arguments.of(User.builder().login("Alisa").password("qwerty").role(Role.USER).build(), 1),

                Arguments.of(User.builder().id(0L).build(), 0)
        );
    }

    @ParameterizedTest
    @MethodSource("getSamplePatternForSearch")
    @DisplayName("Check find by not null fields")
    public void find(User user, int count) {
        long actualCount = userRepo.find(user).count();
        assertEquals(count, actualCount);
    }


    @BeforeEach
    void setUp() {
        sessionCreator = NanoSpring.find(SessionCreator.class);
        userRepo = new UserRepoFull(sessionCreator);
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

}