package com.javarush.lesson08;

import com.javarush.khmelov.dto.Role;
import com.javarush.khmelov.entity.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDaoTest {

    private UserDao userDao;

    @BeforeEach
    public void setUp() {
        userDao = new UserDao();
    }

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

                Arguments.of(User.builder().build(), 3),
                Arguments.of(User.builder().id(0L).build(), 0)
        );
    }

    @ParameterizedTest
    @MethodSource("getSamplePatternForSearch")
    @DisplayName("Check find by not null fields")
    public void find(User user, int count) {
        long actualCount = userDao.find(user).count();
        assertEquals(count, actualCount);
    }

    @Test
    @DisplayName("When get all count=3")
    void getAll() {
        long count = userDao.getAll().size();
        assertTrue(count > 0);
    }

    @Test
    @DisplayName("When find by id then get user id=1 role=ADMIN")
    void get() {
        User user = userDao.get(1L);
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
        userDao.create(tempUser);
        System.out.println("CREATE " + tempUser);

        tempUser.setPassword("password_test_update");
        userDao.update(tempUser);
        System.out.println("UPDATE " + tempUser);

        userDao.delete(tempUser);
        System.out.println("DELETE " + tempUser);
        assertTrue(tempUser.getId() > 0);
    }

    @AfterAll
    public static void destroy() {
        //CnnPool.destroy();
    }

}