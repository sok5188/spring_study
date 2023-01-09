package com.example.guess_music.repository;

import com.example.guess_music.domain.Answers;
import com.example.guess_music.domain.Game;
import com.example.guess_music.domain.QAnswers;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class JpaAnswerRepository implements AnswerRepository {
    private final EntityManager em;

    public JpaAnswerRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Answers save(Answers answers) {
        em.persist(answers);
        return answers;
    }

    @Override
    public Optional<List<Answers>> findAnswers(Long gameIndex) {
        return Optional.ofNullable(em.createQuery("select m from Answers m where m.gameIndex.gameIndex=:gameIndex", Answers.class).setParameter("gameIndex",gameIndex).getResultList());
    }

    @Override
    public Optional<List<String>> findAnswerBySeq(Long gameIndex, int seq) {
        List<String> resultList = em.createQuery("select m.answer from Answers m where m.gameIndex.gameIndex= :gameIndex and m.seq=:seq", String.class).setParameter("gameIndex",gameIndex).setParameter("seq",seq).getResultList();
        return Optional.ofNullable(resultList);
    }

    @Override
    public Optional<String> findSingerBySeq(Long gameIndex, int seq) {
        String singleResult = em.createQuery("select m.singer from Answers m where m.gameIndex.gameIndex= :gameIndex and m.seq=:seq", String.class).setParameter("gameIndex", gameIndex).setParameter("seq", seq).getSingleResult();
        System.out.println("find singer : "+ singleResult);
        return Optional.ofNullable(singleResult);
    }

    @Override
    public Optional<String> findInitialBySeq(Long gameIndex, int seq) {
        String singleResult = em.createQuery("select m.initial from Answers m where m.gameIndex.gameIndex= :gameIndex and m.seq=:seq", String.class).setParameter("gameIndex", gameIndex).setParameter("seq", seq).getSingleResult();
        System.out.println("find initial : "+ singleResult);
        return Optional.ofNullable(singleResult);
    }

}
