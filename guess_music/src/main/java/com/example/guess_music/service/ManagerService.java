package com.example.guess_music.service;

import com.example.guess_music.domain.Answers;
import com.example.guess_music.domain.Game;
import com.example.guess_music.repository.AnswerRepository;
import com.example.guess_music.repository.GameRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
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
        if(opt.isPresent())
            return opt.get()+1;
        else return 1L;
    }
    public boolean delete(Long gameIndex){
        return gameRepository.delete(gameIndex);
    }
    public boolean delete(Long gameIndex,int seq){
        //audio에서 노래 삭제
        String folder="/Users/sin-wongyun/Desktop/guess_music/src/main/resources/static/audio/";
        String filename=gameIndex+"-"+seq+".mp3";
        File file=new File(folder+filename);
        if(file.exists()){
            if(file.delete())
                System.out.println("file deleted");
            else System.out.println("file delete fail");
        }else System.out.println("???");
        //game repo에 노래 수 감소
        gameRepository.deleteSongInGame(gameIndex);
        return answerRepository.delete(gameIndex,seq);
    }

    public List<Answers> getAnswerList(Long gameIndex){
        Optional<List<Answers>> opt = answerRepository.findAnswers(gameIndex);
        if(opt.isPresent())
            return opt.get();
        else return new ArrayList<>();
    }
    public int storeFile(List<String> answer,String singer, String initial,Long gameIndex){
        //가수 초성힌트 저장
        if(singer==null||initial==null)
            return -1;
        //게임 인덱스 설정
        Optional<Game> gameOpt = gameRepository.findGameByGameIndex(gameIndex);
        Game game;
        if(gameOpt.isPresent())
            game=gameOpt.get();
        else return -1;
        //노래 번호 설정
        int maxSeq=answerRepository.findMaxSeq(gameIndex);
        maxSeq=Math.max(++maxSeq,1);

        int saveCount=0;

        for(String ans: answer){
            System.out.println("now target answer is : "+ans);
            if(checkValidSong(ans,singer,gameIndex)){
                //유효한 정답일 경우
                System.out.println("and it is valid answer");

                Answers answers=new Answers();
                answers.setSinger(singer);
                answers.setInitial(initial);
                answers.setGameIndex(game);
                answers.setSeq(maxSeq);
                answers.setAnswer(ans);

                answerRepository.save(answers);
                saveCount++;
            }
        }
        gameRepository.addSongToGame(gameIndex);
        if(saveCount==0)
            return -1;
        return maxSeq;
    }
    private boolean checkValidSong(String answer,String singer,Long gameIndex){
        //gameIndex에 맞는 게임에서 정답이 같고 가수도 같으면 중복으로 처리 false return
        Optional<List<String>> opt = answerRepository.findSingerByAnswer(answer);
        if(!opt.isEmpty()){
            List<String> singers = opt.get();
            Optional<String> result = singers.stream().filter(s -> s.equals(singer)).findAny();
            //singer가 존재하는 경우 노래정답과 가수가 같다는 것이고, 그렇다면 중복된 노래이다.
            if(result.isPresent())
                return false;
        }
        return true;
    }
    public boolean updateAnswer(Long id,String answer){
        answerRepository.updateAnswer(id,answer);
        return true;
    }

}
