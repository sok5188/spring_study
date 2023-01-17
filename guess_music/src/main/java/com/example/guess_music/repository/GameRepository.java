package com.example.guess_music.repository;

import com.example.guess_music.domain.Game;

import java.util.List;
import java.util.Optional;

public interface GameRepository {
    Game save(Game game);
    Optional<Game> findGameByGameIndex(Long gameIndex);
    Optional<List<Game>> findGameList();
    Optional<Long> findMaxGameIndex();

    void addSongToGame(Long gameIndex);
    void deleteSongInGame(Long gameIndex);

    boolean delete(Long gameIndex);

    boolean updateGameTitle(Long gameIndex, String title);
}
