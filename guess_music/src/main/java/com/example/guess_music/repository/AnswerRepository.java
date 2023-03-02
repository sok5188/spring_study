package com.example.guess_music.repository;

import com.example.guess_music.domain.game.Answers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answers, Long> {
    @Query("select a from Answers a join fetch a.game g where g.gameIndex=?1 and a.seq=?2")
    List<Answers> findByIdxSeq(Long gameIndex, Long seq);
    @Query("select a from Answers a join fetch a.game g where g.gameIndex=?1")
    List<Answers> findByGameIndex(Long gameIndex);
    @Query("select a from Answers a join fetch a.music m where m.id=?1")
    Optional<Answers> findByMusicIndex(String musicIndex);
    //@Query(value = "select a.answer from Answers a join fetch a.game g where g.gameIndex=?1 and a.seq=?2",nativeQuery = true)
    @Query(value = "select a.answer as answer from Answers a where a.game.gameIndex=?1 and a.seq=?2")
    Optional<List<AnswerListMapping>> findAnswerListByGameIndexAndSeq(Long gameIndex, Long seq);
    @Query(value = "select a.singer as singer from Answers a where a.game.gameIndex=?2 and a.answer=?1")
    List<SingerListMapping> findAllSingerByAnswer(String answer, Long gameIndex);

    @Modifying(clearAutomatically = true)
    @Query("update Answers a set a.answer=?2 where a.id=?1")
    void updateAnswer(Long id,String answer);

    //for test
    @Query("select count(*) from Answers where game.gameIndex=?1 and seq=?2")
    Long countByGameIndexAndSeq(Long gameIndex, Long seq);

}
