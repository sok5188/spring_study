package com.example.guess_music.repository;

import com.example.guess_music.domain.Game;
import jakarta.persistence.EntityManager;

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
    public Optional<Long> findSongNumByGameIndex(Long gameIndex) {
        Game findGame=em.find(Game.class,gameIndex);
        return Optional.ofNullable(findGame.getSongNum());
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
    public boolean delete(Long gameIndex) {
        try{
            em.remove(em.find(Game.class,gameIndex));
            System.out.println("in repo delete ok");
            return true;
        } catch(Exception e){
            System.out.println("in repo delete nono");
            return false;
        }

    }
}
