package com.javarush.khmelov.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameTo {
    Long id;
    Long questId;
    Long userId;
    Long currentQuestionId;
    GameState gameState;
}
