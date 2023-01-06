package com.example.guess_music.repository;

import com.example.guess_music.domain.Answers;

import java.util.List;
import java.util.Optional;

public interface GameRepository {
    Answers save(Answers answers);
    List<Answers> findAnswer(int gameIndex);
    Optional<List<Answers>> findByGameIndex(int gameIndex);
    Optional<List<String>> findAnswerBySeq(int gameIndex,int seq);
}
