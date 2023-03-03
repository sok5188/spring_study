package com.example.guess_music.repository;

import com.example.guess_music.domain.game.Answers;
import com.example.guess_music.domain.game.Game;
import com.example.guess_music.domain.manage.Music;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
@DataJpaTest
class AnswerRepositoryTest {
    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    GameRepository gameRepository;
    @Autowired
    MusicRepository musicRepository;
    private Answers getAnswers(){
        Answers answers = new Answers();
        answers.setAnswer("testAnswer");
        answers.setInitial("testInitial");
        answers.setSinger("testSinger");
        answers.setSeq(99L);
        return answers;
    }
    private Game getGame(){
        Game game = new Game();
        game.setGameIndex(999L);
        game.setSongNum(0L);
        game.setTitle("testTitle");
        return game;
    }
    private Music getMusic(){
        Music music=new Music();
        music.setName("testMusic");

        return music;
    }

    @Test
    void save(){
        //given
        Answers answers = getAnswers();
        //when
        Answers save = answerRepository.save(answers);
        //then
        assertThat(save.getAnswer()).isEqualTo(answers.getAnswer());
    }
    @Test
    void loadAll(){
        Answers answers = getAnswers();
        Answers answers1= getAnswers();
        Answers answers2= getAnswers();
        answers1.setAnswer("test22");
        answers2.setAnswer("test33");
        answers2.setSeq(100L);
        answers1.setSeq(101L);
        answerRepository.save(answers);
        answerRepository.save(answers1);
        answerRepository.save(answers2);

        List<Answers> all = answerRepository.findAll();
        all.forEach(a->{
            System.out.println(a.getAnswer());
        });
    }
    @Test
    void delete(){
        //given
        Answers answers = getAnswers();
        answerRepository.save(answers);
        //when
        answerRepository.delete(answers);
        //then
        Optional<Answers> opt = answerRepository.findById(answers.getId());
        assertThat(opt.isPresent()).isFalse();
    }
    @Test
    void findByIdxSeq() {
        //given
        Game game=getGame();
        Answers answers=getAnswers();
        gameRepository.save(game);
        answers.setGameIndex(game);
        answerRepository.save(answers);
        //when
        Optional<Answers> opt = answerRepository.findByIdxSeq(game.getGameIndex(), answers.getSeq()).stream().findAny();
        //then
        assertThat(opt.isPresent()).isTrue();
        assertThat(opt.get()).isEqualTo(answers);
        assertThat(opt.get().getAnswer()).isEqualTo(answers.getAnswer());
    }

    @Test
    void findByGameIndex() {
        //given
        Game game=getGame();
        Answers answers=getAnswers();
        gameRepository.save(game);
        answers.setGameIndex(game);
        answerRepository.save(answers);
        //when
        List<Answers> list = answerRepository.findByGameIndex(game.getGameIndex());
        //then
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getAnswer()).isEqualTo(answers.getAnswer());
    }

    @Test
    void findByMusicIndex() {
        //given
        Music music=getMusic();
        Game game = getGame();
        Game savedGame = gameRepository.save(game);
        music.setGame(savedGame);
        Music save = musicRepository.save(music);

        Answers answers=getAnswers();
        answers.setMusic(save);
        answers.setGameIndex(savedGame);
        answerRepository.save(answers);

        //when
        Optional<Answers> byMusicIndex = answerRepository.findByMusicIndex(save.getId()).stream().findAny();
        //then
        assertThat(byMusicIndex.isPresent()).isTrue();
        assertThat(byMusicIndex.get().getAnswer()).isEqualTo(answers.getAnswer());
    }

    @Test
    void findAnswerBySeq() {
        //given
        Game game=getGame();
        Answers answers=getAnswers();
        gameRepository.save(game);
        answers.setGameIndex(game);
        answerRepository.save(answers);
        //when
        Optional<List<AnswerListMapping>> opt = answerRepository.findAnswerListByGameIndexAndSeq(game.getGameIndex(), answers.getSeq());
        //then
        assertThat(opt.isPresent()).isTrue();
        assertThat(opt.get().size()).isEqualTo(1);
        assertThat(opt.get().get(0).getAnswer()).isEqualTo(answers.getAnswer());
    }

    @Test
    void findSingerListByAnswer() {
        //given
        Game game=getGame();
        gameRepository.save(game);
        Answers answers=getAnswers();
        answers.setGameIndex(game);
        answerRepository.save(answers);
        Answers answers2=getAnswers();
        answers2.setSinger("testSinger2");
        answers2.setSeq(100L);
        answers2.setGameIndex(game);
        answerRepository.save(answers2);

        //when
        Optional<List<SingerListMapping>> opt = Optional.ofNullable(answerRepository.findAllSingerByAnswer(answers.getAnswer(), game.getGameIndex()));
        //then
        assertThat(opt.isPresent()).isTrue();
        assertThat(opt.get().size()).isEqualTo(2);
        assertThat(opt.get().get(0).getSinger()).isEqualTo(answers.getSinger());
    }

    @Test
    void updateAnswer() {
        //given
        Answers answers=getAnswers();
        answerRepository.save(answers);
        //when
        answerRepository.updateAnswer(answers.getId(), "newTestAnswer");
        //then
        Optional<Answers> opt = answerRepository.findById(answers.getId());
        assertThat(opt.isPresent()).isTrue();
        assertThat(opt.get().getAnswer()).isEqualTo("newTestAnswer");
    }
}