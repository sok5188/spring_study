package com.example.guess_music.repository;

import com.example.guess_music.domain.game.Game;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
@DataJpaTest
class GameRepositoryTest {
    @Autowired
    GameRepository gameRepository;
    private Game getGame(){
        Game game = new Game();
        game.setGameIndex(999L);
        game.setSongNum(0L);
        game.setTitle("testTitle");
        return game;
    }
    @Test
    void save(){
        //given
        Game game=getGame();
        //when
        Game save = gameRepository.save(game);
        //then
        assertThat(save.getGameIndex()).isEqualTo(999L);
    }
    @Test
    void findMaxGameIndex() {
        //given
        Game game=getGame();
        Game save = gameRepository.save(game);
        //when
        Optional<Long> opt = gameRepository.findMaxGameIndex();
        //then
        assertThat(opt.isPresent()).isTrue();
        assertThat(opt.get()).isEqualTo(999L);

    }

    @Test
    void addSongToGame() {
        //given
        Game game=getGame();
        Game save = gameRepository.save(game);
        //when
        gameRepository.addSongToGame(save.getGameIndex());
        //then
        Optional<Game> opt = gameRepository.findById(save.getGameIndex());
        assertThat(opt.isPresent()).isTrue();
        assertThat(opt.get().getSongNum()).isEqualTo(save.getSongNum()+1);
    }

    @Test
    void deleteSongToGame() {
        //given
        Game game=getGame();
        Game save = gameRepository.save(game);
        //when
        gameRepository.deleteSongToGame(save.getGameIndex());
        //then
        Optional<Game> opt = gameRepository.findById(save.getGameIndex());
        assertThat(opt.isPresent()).isTrue();
        assertThat(opt.get().getSongNum()).isEqualTo(save.getSongNum()-1);
    }

    @Test
    void updateGameTitle() {
        //given
        Game game=getGame();
        Game save = gameRepository.save(game);
        //when
        gameRepository.updateGameTitle(save.getGameIndex(), "NewTestTitle");
        //then
        Optional<Game> opt = gameRepository.findById(save.getGameIndex());
        assertThat(opt.isPresent()).isTrue();
        assertThat(opt.get().getTitle()).isEqualTo("NewTestTitle");
    }

    @Test
    void findSongNum() {
        //given
        Game game=getGame();
        Game save = gameRepository.save(game);
        gameRepository.addSongToGame(save.getGameIndex());
        //when
        Long songNum = gameRepository.findSongNum(save.getGameIndex());
        //then
        assertThat(songNum).isEqualTo(1L);
    }
}