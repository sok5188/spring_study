package com.example.guess_music.repository;

import com.example.guess_music.domain.Answers;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class JpaGameRepository implements GameRepository{
    private final EntityManager em;

    public JpaGameRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Answers save(Answers answers) {
        em.persist(answers);
        return answers;
    }

    @Override
    public List<Answers> findAnswer(int gameIndex) {
        //나중
        return null;
    }

    @Override
    public Optional<List<Answers>> findByGameIndex(int gameIndex) {
        //나중
        return Optional.empty();
    }

    @Override
    public Optional<List<String>> findAnswerBySeq(int gameIndex, int seq) {
        List<String> resultList = em.createQuery("select m.answer from Answers m where m.gameIndex= :gameIndex and m.seq=:seq", String.class).setParameter("gameIndex",gameIndex).setParameter("seq",seq).getResultList();
        return Optional.ofNullable(resultList);
    }
}
