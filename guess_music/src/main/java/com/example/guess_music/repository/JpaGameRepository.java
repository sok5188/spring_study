package com.example.guess_music.repository;

import com.example.guess_music.domain.Game;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public class JpaGameRepository implements GameRepository{
    private final EntityManager em;

    public JpaGameRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Game save(Game game) {
        em.persist(game);
        return game;
    }

    @Override
    public Optional<Game> findGameByGameIndex(Long gameIndex) {
        Game findGame=em.find(Game.class,gameIndex);
        return Optional.ofNullable(findGame);
    }

    @Override
    public Optional<List<Game>> findGameList() {
        return Optional.ofNullable(em.createQuery("select g from Game g", Game.class).getResultList());
    }

    @Override
    public Optional<Long> findMaxGameIndex() {
        Long result = em.createQuery("select max(g.gameIndex) from Game g", Long.class).getSingleResult();
        return Optional.ofNullable(result);
    }

    @Override
    public void addSongToGame(@Param("gameIndex") Long gameIndex){
        em.createQuery("update Game g set g.songnum=g.songnum+1 where g.gameIndex=:gameIndex").setParameter("gameIndex",gameIndex).executeUpdate();
    }

    @Override
    public void deleteSongInGame(Long gameIndex) {
        em.createQuery("update Game g set g.songnum=g.songnum-1 where g.gameIndex=:gameIndex").setParameter("gameIndex",gameIndex).executeUpdate();
    }

    ;


    @Override
    public boolean delete(Long gameIndex) {
        try{
            em.remove(em.find(Game.class,gameIndex));
            return true;
        } catch(Exception e){
            return false;
        }

    }
}
