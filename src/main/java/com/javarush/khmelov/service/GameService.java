package com.javarush.khmelov.service;

import com.javarush.khmelov.dto.GameState;
import com.javarush.khmelov.dto.GameTo;
import com.javarush.khmelov.entity.*;
import com.javarush.khmelov.mapping.Dto;
import com.javarush.khmelov.repository.Repository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.util.Comparator;
import java.util.Optional;

@AllArgsConstructor
public class GameService {

    private final Repository<User> userRepository;
    private final Repository<Game> gameRepository;
    private final Repository<Quest> questRepository;
    private final Repository<Question> questionRepository;
    private final Repository<Answer> answerRepository;

    private final Dto dto;

    @Transactional
    public Optional<GameTo> getGame(Long questId, Long userId) {
        Game gamePattern = Game.builder().questId(questId).build();
        gamePattern.setUserId(userId);
        return gameRepository
                .find(gamePattern)
                .map(dto::from)
                .max(Comparator.comparingLong(GameTo::getId))
                .or(() -> Optional.of(dto.from(getNewGame(userId, gamePattern.getQuestId()))));
    }

    @Transactional
    private Game getNewGame(Long userId, Long questId) {
        Quest quest = questRepository.get(questId);
        Long startQuestionId = quest.getStartQuestionId();
        Question firstQuestion = questionRepository.get(startQuestionId);
        Game newGame = Game.builder()
                .questId(questId)
                .currentQuestionId(startQuestionId)
                .gameState(firstQuestion.getGameState())
                .userId(userId) //from session
                .build();
        userRepository.get(userId).getGames().add(newGame);
        gameRepository.create(newGame);
        return newGame;
    }

    @Transactional
    public Optional<GameTo> processOneStep(Long gameId, Long answerId) {
        Game game = gameRepository.get(gameId);
        if (game.getGameState() == GameState.PLAY) {
            Answer answer = answerRepository.get(answerId);
            Long nextQuestionId = answer != null
                    ? answer.getNextQuestionId()
                    : game.getCurrentQuestionId();
            game.setCurrentQuestionId(nextQuestionId);
            Question question = questionRepository.get(nextQuestionId);
            game.setGameState(question.getGameState());
            gameRepository.update(game);
        } else {
            game = getNewGame(game.getUserId(), game.getQuestId());
        }
        return Optional.ofNullable(game).map(dto::from);
    }

}
