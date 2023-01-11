package com.example.guess_music.service;

import com.example.guess_music.domain.Answers;
import com.example.guess_music.domain.Game;
import com.example.guess_music.domain.Result;
import com.example.guess_music.repository.AnswerRepository;
import com.example.guess_music.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class GameServiceTest {
    @Autowired
    GameService gameService;

    @Autowired
    ManagerService managerService;
    @Test
    void 정답확인() {
        Game game=new Game();
        game.setTitle("forTestGame");
        Long gameIndex = managerService.join(game);
        List<String> ans=new ArrayList<>();
        ans.add("ForTest2");
        int i = managerService.storeFile(ans, "ForTest2", "ForTest2", gameIndex);

        Result result = gameService.getResult("ForTest2", gameIndex, i);
        assertThat(result.getResult()).isEqualTo("Right");
    }

    @Test
    void 힌트확인() {
        Game game=new Game();
        game.setTitle("forTestGame");
        Long gameIndex = managerService.join(game);

        List<String> ans=new ArrayList<>();
        ans.add("테스트게임");
        int seq=managerService.storeFile(ans,"sirong","ㅌㅅㅌㄱㅇ",gameIndex);

        assertThat(gameService.getHint("singer",gameIndex,seq)).isEqualTo("sirong");
        assertThat(gameService.getHint("initial",gameIndex,seq)).isEqualTo("ㅌㅅㅌㄱㅇ");

    }

}