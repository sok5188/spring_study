package com.example.guess_music.controller;

import com.example.guess_music.domain.Result;
import com.example.guess_music.service.GameService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    @GetMapping("/nextSong")
    public String nextSong(){
        return "redirect:/";
    }

    @PostMapping("/testGame")
    @ResponseBody
    public Result testGame(@RequestParam("target") String target){
        Result result=new Result();
        if(gameService.checkAnswers(target,gameIndex,seq)){
            System.out.println("it is right answer");
            score++;
            seq++;
            //마지막곡 인지 판별 필요..
            //만약 노래가 마지막 곡이라면(판단 필요) 다시 게임 선택 창으로 이동(일단은 홈으로 리다이렉트?)
            //some function to redirect select?
            if(seq>2) {
                result.setResult("Game End");
                seq=1;
            }else{
                result.setResult("Next Song");
            }
        }else{
            //오답이면 ? 근데 리다이렉트하면 새로시작될텐데.. 이것도 찾아봐야 할듯
            System.out.println("it is NOT right answer");
            result.setResult("Wrong Answer");
        }
        return result;
    }
}
