package com.example.guess_music.service;

import com.example.guess_music.domain.Answers;
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


    public String getAnswers(String target,int gameIndex,int seq){
        Optional<List<String>> opt = gameRepository.findAnswerBySeq(gameIndex, seq);
        if(opt.isPresent()){
            // gameIndex,seq에 맞는 answers가 존재하는 경우 해당 list의 nullable을 푼다
            List<String> answers=opt.get();
            // 사용자가 입력한 target과 db에 존재하는 정답들을 비교하여 정답 여부를 리턴한다.
            Optional<String> answer= answers.stream().filter(ans -> ans.equals(target)).findAny();
            if(answer.isPresent()){
                return answer.get();
            }
        }
       //어쩌면 예외 핸들링 해야 할 부분..

        return "X";
    }
    public Long getGameSize(int gameIndex){
        //db에서 해당 게임의 인덱스를 가지고 게임 내의 노래 수를 가져와서 return하는 함수
        List<Long> result = gameRepository.findNumGameByGameIndex(gameIndex);
        return result.get(0);
    }
}
