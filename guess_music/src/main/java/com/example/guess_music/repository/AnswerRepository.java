package com.example.guess_music.repository;

import com.example.guess_music.domain.game.Answers;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository {
    Answers save(Answers answers);
    Optional<Answers> findById(Long id);

    Optional<Answers> findByIdxSeq(Long gameIndex, Long seq);
    Optional<Answers> findByMusicIndex(String musicIndex);
    Optional<List<Answers>> findAnswers(Long gameIndex);
    Optional<List<String>> findAnswerBySeq(Long gameIndex,Long seq);

    Optional<List<String>> findSingerByAnswer(String answer,Long gameIndex);

    Long findMaxSeq(Long gameIndex);

    void updateAnswer(Long id,String answer);

    boolean delete(Long id);
}
