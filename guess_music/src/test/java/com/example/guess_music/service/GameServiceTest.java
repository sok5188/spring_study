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
//    @Test
//    void 정답및오답확인() {
//        Game game=new Game();
//        game.setTitle("forTestGame");
//        Long gameIndex = managerService.join(game);
//
//        List<String> ans=new ArrayList<>();
//        ans.add("ForTest1");
//        Long i = managerService.storeFile(ans, "ForTest1", "ForTest1", gameIndex);
//        List<String> ans2=new ArrayList<>();
//        ans2.add("ForTest2");
//        Long i2 = managerService.storeFile(ans2, "ForTest2", "ForTest2", gameIndex);
//        System.out.println("i is "+i+"/"+i2);
//
//        //정답
//        Result result = gameService.getResult("ForTest1", gameIndex, i);
//        assertThat(result.getResult()).isEqualTo("Right");
//        //오답
//        Result result2 = gameService.getResult("ForTest2", gameIndex, i);
//        assertThat(result2.getResult()).isEqualTo("Wrong");
//        //게임크기는 이거 transactional한 특성 때문인지 크기 변경하고 조회하면 제대로 사이즈가 증가되는데 그거 이후에 사이즈가 초기화 됨..
//    }

//    @Test
//    void 힌트확인() {
//        Game game=new Game();
//        game.setTitle("forTestGame");
//        Long gameIndex = managerService.join(game);
//
//        List<String> ans=new ArrayList<>();
//        ans.add("테스트게임");
//        Long seq=managerService.storeFile(ans,"sirong","ㅌㅅㅌㄱㅇ",gameIndex);
//
//        assertThat(gameService.getHint("singer",gameIndex,seq)).isEqualTo("sirong");
//        assertThat(gameService.getHint("initial",gameIndex,seq)).isEqualTo("ㅌㅅㅌㄱㅇ");
//
//    }
}