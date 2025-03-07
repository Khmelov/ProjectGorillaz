package com.javarush.khmelov.cmd;

import com.javarush.khmelov.dto.UserTo;
import com.javarush.khmelov.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

import java.util.Collection;

@SuppressWarnings("unused")
@AllArgsConstructor
public class ListUser implements Command {

    private final UserService userService;

    @Override
    public String doGet(HttpServletRequest request) {
        Collection<UserTo> users = userService.getAll();
        request.setAttribute("users", users);
        return getView();
    }


}