package com.example.guess_music.repository;

import com.example.guess_music.domain.Answers;
import com.example.guess_music.domain.Game;

import java.util.List;
import java.util.Optional;

public interface GameRepository {
    Game save(Game game);
    Optional<Long> findSongNumByGameIndex(Long gameIndex);
    Optional<List<Game>> findGameList();
    Optional<Long> findMaxGameIndex();

    boolean delete(Long gameIndex);
}
