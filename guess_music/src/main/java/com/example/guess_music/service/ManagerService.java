package com.example.guess_music.service;

import com.example.guess_music.domain.Answers;
import com.example.guess_music.domain.Game;
import com.example.guess_music.repository.AnswerRepository;
import com.example.guess_music.repository.GameRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Transactional
public class ManagerService {
    private final AnswerRepository answerRepository;
    private final GameRepository gameRepository;

    public ManagerService(AnswerRepository answerRepository, GameRepository gameRepository) {
        this.answerRepository = answerRepository;
        this.gameRepository = gameRepository;
    }

    public Long join(Game game){
        game.setGameIndex(getValidGameIndex());
        game.setSongNum(0L);
        gameRepository.save(game);
        return  game.getGameIndex();
    }
    public Long getValidGameIndex(){
        Optional<Long> opt = gameRepository.findMaxGameIndex();
        System.out.println("max idx is : "+opt.get());
        if(opt.isPresent())
            return opt.get()+1;
        else return 1L;
    }
    public boolean delete(Long gameIndex){
        return gameRepository.delete(gameIndex);
    }

    public List<Answers> getAnswerList(Long gameIndex){
        Optional<List<Answers>> opt = answerRepository.findAnswers(gameIndex);
        if(opt.isPresent())
            return opt.get();
        else return new ArrayList<>();
    }
}
