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
    public Optional<Answers> findByIdxSeq(Long gameIndex, Long seq) {
        return em.createQuery("select m from Answers m where m.gameIndex.gameIndex= :gameIndex and m.seq=:seq", Answers.class).setParameter("gameIndex", gameIndex).setParameter("seq", seq).getResultList().stream().findAny();
    }

    @Override
    public Optional<Answers> findByMusicIndex(String musicIndex) {
        return em.createQuery("select m from Answers m where m.music.id=:musicIndex", Answers.class).setParameter("musicIndex",musicIndex).getResultList().stream().findAny();
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
    public Optional<List<String>> findSingerByAnswer(String answer,Long gameIndex) {
        List<String> list = em.createQuery("select a.singer from Answers a where a.answer=:answer and a.gameIndex.gameIndex=:gameIndex", String.class).setParameter("answer", answer).setParameter("gameIndex",gameIndex).getResultList();
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
