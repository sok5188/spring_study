package com.example.guess_music.repository;

import com.example.guess_music.domain.game.Answers;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository {
    Answers save(Answers answers);
    Optional<Answers> findById(Long id);
    Optional<List<Answers>> findAnswers(Long gameIndex);
    Optional<List<String>> findAnswerBySeq(Long gameIndex,Long seq);
    Optional<String> findSingerBySeq(Long gameIndex,Long seq);
    Optional<String> findInitialBySeq(Long gameIndex,Long seq);
    Optional<List<String>> findSingerByAnswer(String answer);

    Long findMaxSeq(Long gameIndex);
    boolean delete(Long gameIndex,Long seq);

    void updateAnswer(Long id,String answer);

    boolean delete(Long id);
}
