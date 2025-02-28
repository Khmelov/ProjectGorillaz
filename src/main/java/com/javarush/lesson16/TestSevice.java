package com.javarush.lesson16;

import com.javarush.khmelov.dto.UserTo;
import com.javarush.khmelov.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Transactional
@AllArgsConstructor
public class TestSevice {

    private final UserService userService;

    public UserTo get(Long id) {
        return userService.get(id).orElseThrow();
    }
}
