package com.javarush.cons42.cmd;

import com.javarush.cons42.entity.User;
import com.javarush.cons42.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;

@SuppressWarnings("unused")
public class ListUser implements Command {

    private final UserService userService;

    public ListUser(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String doGet(HttpServletRequest request) {
        Collection<User> users = userService.getAll();
        request.setAttribute("users", users);
        return getView();
    }


}