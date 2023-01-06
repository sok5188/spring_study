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


    public boolean checkAnswers(String target,int gameIndex,int seq){
        Optional<List<String>> opt = gameRepository.findAnswerBySeq(gameIndex, seq);
        if(opt.isPresent()){
            // gameIndex,seq에 맞는 answers가 존재하는 경우 해당 list의 nullable을 푼다
            System.out.println("not empty");
            List<String> answers=opt.get();
            for (String s :
                    answers) {
                System.out.println("right answer : "+s);
            }
            // 사용자가 입력한 target과 db에 존재하는 정답들을 비교하여 정답 여부를 리턴한다.
            return answers.stream().filter(ans -> ans.equals(target)).findAny().isPresent();
        }
        //answer 맞으면 true 아니면 false반환하자
        //List stream사용해서 반복문 돌기
        //그 뭐 findany였나 그거로 찾을 수 있나?

        return false;
    }
}
