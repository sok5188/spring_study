package com.example.guess_music.service;

import com.example.guess_music.domain.auth.Member;
import com.example.guess_music.domain.auth.Role;
import com.example.guess_music.domain.game.Answers;
import com.example.guess_music.domain.game.Game;
import com.example.guess_music.domain.manage.Music;
import com.example.guess_music.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Transactional
@Service
public class ManagerService {
    @Autowired
    private MemberRepository memberRepository;

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
        System.out.println(gameIndex+"/"+seq);
        //file db에서 노래 삭제 -> cascade설정으로 answer 전부 같이 연쇄적으로 삭제 됨
        Optional<Answers> opt = answerRepository.findByIdxSeq(gameIndex, seq).stream().findAny();
        if(!opt.isPresent()){
            //삭제할 노래가 없으면 false
            return false;
        }
        Music music = opt.get().getMusic();
        //System.out.println("now delete id :"+music.getId());
        //musicRepository.delete(music);
        //!!! 지금 갑자기 노래 삭제 기능이 말을 안듣는다..
        musicRepository.deleteManual(music.getId());
        //game repo에 노래 수 감소
        gameRepository.deleteSongToGame(gameIndex);

        return true;
    }
    public Long getAnswerNum(Long gameIndex,Long seq){
        return answerRepository.countByGameIndexAndSeq(gameIndex,seq);
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
        Optional<List<SingerListMapping>> opt = Optional.ofNullable(answerRepository.findAllSingerByAnswer(answer, gameIndex));
        if(!opt.isEmpty()){

            Optional<SingerListMapping> result = opt.get().stream().filter(s -> s.getSinger().equals(singer)).findAny();
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
    public Answers addAnswer(Long gameIndex, Long seq, String answer){
        Answers answers=new Answers();
        Optional<Answers> opt = answerRepository.findByIdxSeq(gameIndex, seq).stream().findAny();
        Optional<Game> gameByGameIndex = gameRepository.findById(gameIndex);
        if(!opt.isPresent()||!gameByGameIndex.isPresent())
        {
            log.error("singer or initial or game not found");
            return new Answers();
        }
        answers.setAnswer(answer);
        answers.setGameIndex(gameByGameIndex.get());
        answers.setSeq(seq);
        answers.setSinger(opt.get().getSinger());
        answers.setInitial(opt.get().getInitial());
        answers.setMusic(opt.get().getMusic());
        Answers save = answerRepository.save(answers);
        return save;
    }
    public boolean deleteAnswer(Long id){
        answerRepository.deleteById(id);
        return true;
    }

    public boolean updateGameTitle(Long gameIndex, String title){
        gameRepository.updateGameTitle(gameIndex,title);
        return true;
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
        Optional<Answers> byMusicIndex = answerRepository.findByMusicIndex(musicId).stream().findAny();
        if(!byMusicIndex.isPresent()){
            musicRepository.deleteById(musicId);
            gameRepository.deleteSongToGame(gameIndex);
        }
    }
    public Long getSongNum(Long gameIndex){
        //for Test
        Long songNum = gameRepository.findSongNum(gameIndex);
        return songNum;
    }
    public Long getMusicNum(Long gameIndex){
        //for Test
        return musicRepository.findNumberOfMusic(gameIndex);
    }
    public List<Music> getMusicList(Long gameIndex){
        Optional<List<Music>> opt = Optional.ofNullable(musicRepository.findByGameIndex(gameIndex));
        if(opt.isPresent()){
            List<Music> musics = opt.get();
            return musics;
        }
        else {
            log.warn("No Music found");
            return new ArrayList<>();
        }
    }
    public Answers getAnswerForOnlyTest(Long gameIndex,Long seq){
        Optional<Answers> opt = answerRepository.findByIdxSeq(gameIndex, seq).stream().findAny();
        return opt.get();
    }

    public void increaseDB(String type){
        Optional<Music> optMusic = musicRepository.findById("c0c0bbac-f6ad-42cc-9adb-81d24be2a075");
        if(!optMusic.isPresent())
            System.out.println("Not exist music");
        Optional<Game> opt = gameRepository.findById(2L);
        if(!opt.isPresent())
            System.out.println("Not exist game");
        if(type.equals("member")){
            log.info("member");
            for( int i=0; i<1000;i++){
                Member member=new Member();
                member.setUsername(UUID.randomUUID().toString());
                member.setName("DELETEME");
                member.setRole(Role.ROLE_USER);
                memberRepository.save(member);
            }
        }else if (type.equals("game")){
            log.info("game");
            for( int i=0; i<1000;i++){
                Game game=new Game();
                game.setSongNum(0L);
                game.setGameIndex(getValidGameIndex());
                game.setTitle("DELETEME");
                gameRepository.save(game);
            }

        }else if (type.equals("song")){
            log.info("song");
            //톰캣 heap용량 이슈로 일단 100개씩 추가.
            for( int i=0; i<100;i++){
                Music music=new Music();
                music.setGame(opt.get());
                music.setName("DELETEME");
                music.setData(optMusic.get().getData());
                music.setType(optMusic.get().getType());
                musicRepository.save(music);
            }

        }else if (type.equals("answer")){
            log.info("answer");
            for( int i=0; i<1000;i++){

                Answers answers=new Answers();
                answers.setGameIndex(opt.get());
                answers.setAnswer("DELETEME");
                answers.setSinger("DELETEME");
                answers.setInitial("DELETEME");
                answers.setSeq(999L);
                answers.setMusic(optMusic.get());
                answerRepository.save(answers);
            }

        }
    }
    public List<Long> getSize(){
        Long mem=memberRepository.count();
        Long ans = answerRepository.count();
        Long song=musicRepository.count();
        Long game=gameRepository.count();
        return Arrays.asList(mem,ans,song,game);
    }
    public Page<Game> getPagedGames(int page){
        Page<Game> pages = gameRepository.findAll(PageRequest.of(page, 10));
        return pages;
    }
    public Page<Answers> getPagedAnswers(int page,Long gameIndex){
        Page<Answers> pages = answerRepository.findAllByGame_GameIndex(gameIndex,PageRequest.of(page, 10));
        pages.forEach(answers -> {
            System.out.println(answers.getAnswer()+"/"+answers.getGameIndex());
        });
        return pages;
    }
}
