package com.javarush.khmelov.util;

import com.javarush.khmelov.dto.UserTo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class RequestHelpers {

    public Long getId(HttpServletRequest req) {
        return getId(req, Key.ID);
    }

    public Long getId(HttpServletRequest req, String key) {
        String id = req.getParameter(key);
        return id != null && !id.isBlank()
                ? Long.parseLong(id)
                : 0L;
    }

    public Long getId(HttpSession session) {
        Object user = session.getAttribute(Key.USER);
        return user != null
                ? ((UserTo) user).getId()
                : 0L;
    }

    public Optional<UserTo> getUser(HttpSession session) {
        return Optional
                .ofNullable(session.getAttribute(Key.USER))
                .map(UserTo.class::cast); // equivalent to .map(u -> (User) u);
    }

    public static void createError(HttpServletRequest request, String errorMessage) {
        request.getSession().setAttribute(Key.ERROR_MESSAGE, errorMessage);
    }
}
