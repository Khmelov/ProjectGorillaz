package com.javarush.khmelov.cmd;

import com.javarush.khmelov.dto.UserTo;
import com.javarush.khmelov.util.Go;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@SuppressWarnings("unused")
public class Profile implements Command {

    @Override
    public String doPost(HttpServletRequest request) {
        if (request.getParameter("logout") == null) {
            HttpSession session = request.getSession();
            UserTo user = (UserTo) session.getAttribute("user");
            return Go.EDIT_USER + "?id=" + user.getId();
        } else {
            return Go.LOGOUT;
        }
    }
}
