package com.javarush.lesson16;

import com.javarush.khmelov.entity.User;
import com.javarush.khmelov.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Transactional
@AllArgsConstructor
public class TestSevice {

    private final UserService userService;

    public User get(Long id) {
        return userService.get(id).orElseThrow();
    }
}
