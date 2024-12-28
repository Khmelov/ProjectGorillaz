package com.javarush.khmelov.controller;

import com.javarush.khmelov.cmd.Command;
import com.javarush.khmelov.config.Config;
import com.javarush.khmelov.config.NanoSpring;
import com.javarush.khmelov.entity.Role;
import com.javarush.khmelov.exception.AppException;
import com.javarush.khmelov.util.Go;
import com.javarush.khmelov.util.RequestHelpers;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@MultipartConfig(fileSizeThreshold = 1 << 20)
@WebServlet({
        Go.INDEX, Go.HOME,
        Go.SIGNUP, Go.LOGIN, Go.LOGOUT,
        Go.LIST_USER, Go.PROFILE, Go.EDIT_USER,
        Go.CREATE_QUEST, Go.QUEST,
        Go.PLAY_GAME,
        Go.STATISTICS
})
public class FrontController extends HttpServlet {

    private final HttpResolver httpResolver = NanoSpring.find(HttpResolver.class);

    @Override
    public void init(ServletConfig servletConfig) {
        Config config = NanoSpring.find(Config.class);
        config.fillEmptyRepository();

        ServletContext servletContext = servletConfig.getServletContext();
        servletContext.setAttribute("roles", Role.values());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Command command = httpResolver.resolve(req);
        String view = command.doGet(req);
        String jsp = getJsp(view);
        req.getRequestDispatcher(jsp).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Command command = httpResolver.resolve(req);
            String redirect = command.doPost(req);
            resp.sendRedirect(req.getContextPath()+redirect);
        } catch (AppException e) {
            log.warn("Error: {}", e.getMessage());
            RequestHelpers.createError(req, e.getMessage());
            resp.sendRedirect(req.getContextPath()+req.getRequestURI());
        }
    }

    private static String getJsp(String view) {
        return "/WEB-INF/" + view + ".jsp";
    }
}
