package com.example.guess_music.repository;

import com.example.guess_music.domain.Answers;
import com.example.guess_music.domain.Game;

import java.util.List;
import java.util.Optional;

public interface GameRepository {
    Answers save(Answers answers);
    List<Answers> findAnswer(Long gameIndex);
    List<Long> findNumGameByGameIndex(Long gameIndex);
    Optional<List<String>> findAnswerBySeq(Long gameIndex,int seq);

    Optional<String> findSingerBySeq(Long gameIndex,int seq);
    Optional<String> findInitialBySeq(Long gameIndex,int seq);

    Optional<List<Game>> findGameList();
}
