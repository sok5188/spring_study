package com.example.guess_music.controller;

import com.example.guess_music.service.GameService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class GameController {
    private final GameService gameService;
    private int gameIndex,seq,score;
    public GameController(GameService gameService) {
        this.gameService = gameService;
        gameIndex=seq=1;
        score=0;
    }

    @GetMapping("/testGame")
    public String createtestGame(Model model){
        System.out.println("entered testGame");
        String music=gameIndex+"-"+seq;
        System.out.println("now target music is : "+music+"and game index,seq : "+gameIndex+" / "+seq);
        model.addAttribute("music",music);
        return "/game/testGame";
    }

    @PostMapping("/testGame")
    public String testGame(AnswerForm form){
        //form에서 얻은 값이 이 게임의 정답인 경우 game의 현재 노래의 정답 list를 받아온다
        System.out.println("entered check answer : "+form.answer);
        if(gameService.checkAnswers(form.answer,gameIndex,seq)){
            System.out.println("it is right answer");
            score++;
            seq++;
            //마지막곡 인지 판별 필요..
            //만약 노래가 마지막 곡이라면(판단 필요) 다시 게임 선택 창으로 이동(일단은 홈으로 리다이렉트?)
            //some function to redirect select?
            if(seq>2)
                return "redirect:/";
            return "redirect:/testGame";
        }else{
            //오답이면 ? 근데 리다이렉트하면 새로시작될텐데.. 이것도 찾아봐야 할듯
            System.out.println("it is NOT right answer");
            return "/game/testGame";
        }

    }
}
