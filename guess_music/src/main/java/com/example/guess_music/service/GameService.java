package com.example.guess_music.service;

import com.example.guess_music.domain.Answers;
import com.example.guess_music.domain.Result;
import com.example.guess_music.repository.GameRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class GameService {
    private Long gameIndex;
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
        gameIndex=0L;
    }

    private final GameRepository gameRepository;


    public Result getAnswers(String target, int gameIndex, int seq){
        Result result=new Result();
        Optional<List<String>> opt = gameRepository.findAnswerBySeq(gameIndex, seq);
        if(opt.isPresent()){
            // gameIndex,seq에 맞는 answers가 존재하는 경우 해당 list의 nullable을 푼다
            List<String> answers=opt.get();
            result.setAnswer(answers.get(0));
            // 사용자가 입력한 target과 db에 존재하는 정답들을 비교하여 정답 여부를 리턴한다.
            Optional<String> answer= answers.stream().filter(ans -> ans.equals(target)).findAny();
            if(answer.isPresent()){
                result.setResult("Right");
            }else{
                result.setResult("Wrong");
            }
            return result;
        }
       //어쩌면 예외 핸들링 해야 할 부분..

        return new Result();
    }
    public Long getGameSize(int gameIndex){
        //db에서 해당 게임의 인덱스를 가지고 게임 내의 노래 수를 가져와서 return하는 함수
        List<Long> result = gameRepository.findNumGameByGameIndex(gameIndex);
        return result.get(0);
    }

    public String getHint(String type,int gameIndex,int seq){
        if(type.equals("singer")){
            Optional<String> opt = gameRepository.findSingerBySeq(gameIndex, seq);
            if(opt.isPresent())
                return opt.get();
            else
                return "Nothing";
        }
        if(type.equals("initial")){
            Optional<String> opt = gameRepository.findInitialBySeq(gameIndex, seq);
            if(opt.isPresent())
                return opt.get();
            else
                return "Nothing";

        }
        return "False type";
    }
}
