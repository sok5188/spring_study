package com.example.guess_music.service;

import com.example.guess_music.domain.game.Answers;
import com.example.guess_music.domain.game.Game;
import com.example.guess_music.domain.manage.Music;
import com.example.guess_music.repository.AnswerRepository;
import com.example.guess_music.repository.GameRepository;
import com.example.guess_music.repository.MusicRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
@Slf4j
@Transactional
public class ManagerService {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private MusicRepository musicRepository;

    @Autowired
    private AnswerRepository answerRepository;

    public Long join(Game game){
        game.setGameIndex(getValidGameIndex());
        game.setSongNum(0L);
        Game saved=gameRepository.save(game);
        return  saved.getGameIndex();
    }
    public Long getValidGameIndex(){
        Optional<Long> opt = gameRepository.findMaxGameIndex();
        if(opt.isPresent())
            return opt.get()+1;
        else return 1L;
    }
    public boolean delete(Long gameIndex){
        gameRepository.deleteById(gameIndex);
        return true;
    }
    public boolean delete(Long gameIndex,Long seq){
        //file db에서 노래 삭제 -> cascade설정으로 answer 전부 같이 연쇄적으로 삭제 됨
        Optional<Answers> opt = answerRepository.findByIdxSeq(gameIndex, seq);
        if(!opt.isPresent()){
            //삭제할 노래가 없으면 false
            return false;
        }
        Music music = opt.get().getMusic();
        musicRepository.delete(music);

        //game repo에 노래 수 감소
        gameRepository.deleteSongToGame(gameIndex);

        return true;
    }

    public List<Answers> getAnswerList(Long gameIndex){
        Optional<List<Answers>> opt = Optional.ofNullable(answerRepository.findByGameIndex(gameIndex));
        if(opt.isPresent()){
            List<Answers> answers = opt.get();
            Collections.sort(answers);
            return answers;
        }
        else {
            log.warn("No answers found");
            return new ArrayList<>();
        }
    }
    public Long storeAnswers(List<String> answer, String singer, String initial, Long gameIndex, Music music){
        //가수 초성힌트 저장
        if(singer==null||initial==null)
        {
            log.error("singer or initial is null");
            return -1L;
        }
        //게임 인덱스 설정
        Optional<Game> gameOpt = gameRepository.findById(gameIndex);
        Game game;

        if(gameOpt.isPresent())
            game=gameOpt.get();
        else {
            log.error("game not found");
            return -1L;
        }
        //노래 번호 설정 in game Repo
        Long maxSeq=gameRepository.findSongNum(gameIndex);
        maxSeq=Math.max(++maxSeq,1);

        int saveCount=0;

        for(String ans: answer){
            if(checkValidSong(ans,singer,gameIndex)){
                //유효한 정답일 경우
                Answers answers=new Answers();
                answers.setSinger(singer);
                answers.setInitial(initial);
                answers.setGameIndex(game);
                answers.setSeq(maxSeq);
                answers.setAnswer(ans);
                answers.setMusic(music);
                answerRepository.save(answers);
                saveCount++;
            }
        }
        // 유효한 정답이 없는 경우 -1리턴
        if(saveCount==0)
        {
            log.error("No valid answer found");
            return -1L;
        }

        gameRepository.addSongToGame(gameIndex);
        return maxSeq;
    }
    private boolean checkValidSong(String answer,String singer,Long gameIndex){
        //gameIndex에 맞는 게임에서 정답이 같고 가수도 같으면 중복으로 처리 false return
        Optional<List<String>> opt = answerRepository.findSingerByAnswer(answer,gameIndex);
        if(!opt.isEmpty()){
            List<String> singers = opt.get();
            Optional<String> result = singers.stream().filter(s -> s.equals(singer)).findAny();
            //singer가 존재하는 경우 노래정답과 가수가 같다는 것이고, 그렇다면 중복된 노래이다.
            if(result.isPresent())
            {
                log.error("Song is duplicated");
                return false;
            }
        }
        return true;
    }
    public boolean updateAnswer(Long id,String answer){
        answerRepository.updateAnswer(id,answer);
        return true;
    }

    public boolean addAnswer(Long gameIndex,Long seq, String answer){
        Answers answers=new Answers();
        Optional<Answers> opt = answerRepository.findByIdxSeq(gameIndex, seq);
        Optional<Game> gameByGameIndex = gameRepository.findById(gameIndex);
        if(!opt.isPresent()||!gameByGameIndex.isPresent())
        {
            log.error("singer or initial or game not found");
            return false;
        }
        answers.setAnswer(answer);
        answers.setGameIndex(gameByGameIndex.get());
        answers.setSeq(seq);
        answers.setSinger(opt.get().getSinger());
        answers.setInitial(opt.get().getInitial());
        answers.setMusic(opt.get().getMusic());
        answerRepository.save(answers);
        return true;
    }
    public boolean deleteAnswer(Long id){
        answerRepository.deleteById(id);
        return true;
    }

    public boolean updateGameTitle(Long gameIndex, String title){
        return gameRepository.updateGameTitle(gameIndex,title);
    }

    public Music storeMusic(MultipartFile file,Long gameIndex) throws IOException {
        Optional<Game> gameOpt = gameRepository.findById(gameIndex);
        Game game;

        if(gameOpt.isPresent())
            game=gameOpt.get();
        else {
            log.error("game not found");
            return null;
        }
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Music music = new Music(fileName, file.getContentType(), file.getBytes(),game);
        Music save = musicRepository.save(music);
        return save;
    }
    public Answers findAnswerById(Long id){
        Optional<Answers> opt = answerRepository.findById(id);
        if(opt.isPresent()){
            return opt.get();
        }
        return null;
    }
    public void validateMusic(String musicId,Long gameIndex){
        //정답 삭제로 만약 노래의 정답이 없어진 경우 노래 삭제 및 게임 노래 수 감소 필요
        Optional<Answers> byMusicIndex = answerRepository.findByMusicIndex(musicId);
        if(!byMusicIndex.isPresent()){
            musicRepository.deleteById(musicId);
            gameRepository.deleteSongToGame(gameIndex);
        }
    }
}
