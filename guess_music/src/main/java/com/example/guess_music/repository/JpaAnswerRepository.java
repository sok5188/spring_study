package com.example.guess_music.repository;

import com.example.guess_music.domain.game.Answers;
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
    public Optional<Answers> findById(Long id) {
        Answers answers = em.find(Answers.class, id);
        return Optional.ofNullable(answers);
    }

    @Override
    public Optional<List<Answers>> findAnswers(Long gameIndex) {
        return Optional.ofNullable(em.createQuery("select m from Answers m where m.gameIndex.gameIndex=:gameIndex", Answers.class).setParameter("gameIndex",gameIndex).getResultList());
    }

    @Override
    public Optional<List<String>> findAnswerBySeq(Long gameIndex, Long seq) {
        List<String> resultList = em.createQuery("select m.answer from Answers m where m.gameIndex.gameIndex= :gameIndex and m.seq=:seq", String.class).setParameter("gameIndex",gameIndex).setParameter("seq",seq).getResultList();
        return Optional.ofNullable(resultList);
    }

    @Override
    public Optional<String> findSingerBySeq(Long gameIndex, Long seq) {
        Optional<String> singleResult = em.createQuery("select m.singer from Answers m where m.gameIndex.gameIndex= :gameIndex and m.seq=:seq", String.class).setParameter("gameIndex", gameIndex).setParameter("seq", seq).getResultList().stream().findAny();
        return singleResult;
    }

    @Override
    public Optional<String> findInitialBySeq(Long gameIndex, Long seq) {
        Optional<String> singleResult = em.createQuery("select m.initial from Answers m where m.gameIndex.gameIndex= :gameIndex and m.seq=:seq", String.class).setParameter("gameIndex", gameIndex).setParameter("seq", seq).getResultList().stream().findAny();
        return singleResult;
    }

    @Override
    public Optional<List<String>> findSingerByAnswer(String answer) {
        List<String> list = em.createQuery("select a.singer from Answers a where a.answer=:answer", String.class).setParameter("answer", answer).getResultList();
        return Optional.ofNullable(list);
    }

    @Override
    public Long findMaxSeq(Long gameIndex) {
        Long result = em.createQuery("select max(a.seq) from Answers a where a.gameIndex.gameIndex=:gameIndex", Long.class).setParameter("gameIndex", gameIndex).getSingleResult();
        if(result!=null)
            return result;
        else return 0L;
    }

    @Override
    public boolean delete(Long gameIndex, Long seq) {
        try {
            List<Answers> resultList = em.createQuery("select a from Answers a where a.gameIndex.gameIndex=:gameIndex and a.seq=:seq", Answers.class).setParameter("gameIndex", gameIndex).setParameter("seq", seq).getResultList();
            for(Answers ans:resultList){
                em.remove(ans);
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public void updateAnswer(Long id,String answer) {
        em.createQuery("update Answers a set a.answer=:answer where a.id=:id").setParameter("answer",answer).setParameter("id",id).executeUpdate();
    }

    @Override
    public boolean delete(Long id) {
        try {
            Optional<Answers> byId = this.findById(id);
            if(!byId.isPresent())
                return false;
            em.remove(byId.get());
            return true;
        }catch (Exception e){
            return false;
        }
    }


}
