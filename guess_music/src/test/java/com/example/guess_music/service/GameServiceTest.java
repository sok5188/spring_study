package com.example.guess_music.service;

import com.example.guess_music.repository.AnswerRepository;
import com.example.guess_music.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@Transactional
class GameServiceTest {
    public GameServiceTest(AnswerRepository answerRepository, GameRepository gameRepository) {
        this.answerRepository = answerRepository;
        this.gameRepository = gameRepository;
    }
    @Autowired
    private final AnswerRepository answerRepository;
    @Autowired
    private final GameRepository gameRepository;
    @Autowired
    GameService gameService;
    @Test
    void 정답확인() {

    }

    @Test
    void getGameSize() {
    }

    @Test
    void getHint() {
    }

    @Test
    void getGameList() {
    }
}