package com.javarush.khmelov.cmd;

import com.javarush.khmelov.dto.Role;
import com.javarush.khmelov.dto.UserTo;
import com.javarush.khmelov.service.ImageService;
import com.javarush.khmelov.service.UserService;
import com.javarush.khmelov.util.Go;
import com.javarush.khmelov.util.Key;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;


@SuppressWarnings("unused")
@AllArgsConstructor
public class EditUser implements Command {

    private final UserService userService;
    private final ImageService imageService;

    @Override
    public String doGet(HttpServletRequest req) {
        String stringId = req.getParameter(Key.ID);
        if (stringId != null) {
            long id = Long.parseLong(stringId);
            userService.get(id)
                    .ifPresent(user -> req.setAttribute(Key.USER, user));
        }
        return getView();
    }

    @Override
    @SneakyThrows
    public String doPost(HttpServletRequest req) {
        long id = Long.parseLong(req.getParameter(Key.ID));
        UserTo user = UserTo.builder()
                .id(id)
                .login(req.getParameter(Key.LOGIN))
                .password(req.getParameter(Key.PASSWORD))
                .role(Role.valueOf(req.getParameter(Key.ROLE)))
                .build();
        userService.update(user);
        imageService.uploadImage(req, user.getImage());
        return Go.EDIT_USER + "?id=" + user.getId();
    }
}