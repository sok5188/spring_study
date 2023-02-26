package com.example.guess_music.repository;

import com.example.guess_music.domain.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface GameRepository extends JpaRepository<Game,Long> {
    //Optional<Game> findByGameIndex(Long gameIndex);
    //Optional<List<Game>> findGameList();
    @Query("select max(g.gameIndex) from Game g")
    Optional<Long> findMaxGameIndex();
    @Modifying(clearAutomatically = true)
    @Query("update Game g set g.songnum=g.songnum+1 where g.gameIndex=?1")
    int addSongToGame(Long gameIndex);
    @Modifying(clearAutomatically = true)
    @Query("update Game g set g.songnum=g.songnum-1 where g.gameIndex=?1")
    void deleteSongToGame(Long gameIndex);
    @Modifying(clearAutomatically = true)
    @Query("update Game g set g.title=?2 where g.gameIndex=?1")
    void updateGameTitle(Long gameIndex, String title);

    @Query("select g.songnum from Game g where g.gameIndex=?1")
    Long findSongNum(Long gameIndex);
}
