package com.javarush.khmelov.cmd;

import com.javarush.khmelov.dto.QuestTo;
import com.javarush.khmelov.dto.QuestionTo;
import com.javarush.khmelov.dto.Role;
import com.javarush.khmelov.dto.UserTo;
import com.javarush.khmelov.service.ImageService;
import com.javarush.khmelov.service.QuestService;
import com.javarush.khmelov.service.QuestionService;
import com.javarush.khmelov.util.Go;
import com.javarush.khmelov.util.Key;
import com.javarush.khmelov.util.RequestHelpers;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.util.Optional;

import static com.javarush.khmelov.util.Key.QUEST;


@AllArgsConstructor
public class Quest implements Command {

    private final QuestService questService;
    private final QuestionService questionService;
    private final ImageService imageService;


    @Override
    public String doGet(HttpServletRequest req) {
        long id = RequestHelpers.getId(req);
        Optional<QuestTo> quest = questService.get(id);
        req.setAttribute(QUEST, quest.orElseThrow());
        return getView();
    }

    @Override
    @SneakyThrows
    public String doPost(HttpServletRequest req) {
        Optional<UserTo> editor = RequestHelpers.getUser(req.getSession());
        if (editor.isPresent() && editor.get().getRole() == Role.ADMIN) {
            Long id = RequestHelpers.getId(req);
            Long questionId = RequestHelpers.getId(req, "questionId");
            String text = req.getParameter(Key.TEXT);
            Optional<QuestionTo> question = questionService.update(questionId, text);
            if (question.isPresent()) {
                imageService.uploadImage(req, question.get().getImage());
            }
            return "%s?id=%d#bookmark%d".formatted(Go.QUEST, id, questionId);
        } else {
            return Go.QUEST; //TODO добавить ошибку, что "Недостаточно прав для редактирования";
        }
    }
}
