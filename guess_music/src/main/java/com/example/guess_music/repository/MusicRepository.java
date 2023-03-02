package com.example.guess_music.repository;

import com.example.guess_music.domain.game.Answers;
import com.example.guess_music.domain.manage.Music;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MusicRepository extends JpaRepository<Music, String> {
    @Query("select a from Music a join fetch a.game g where g.gameIndex=?1")
    List<Music> findByGameIndex(Long gameIndex);
    @Query("select count(*) from Music m where m.game.gameIndex=?1")
    Long findNumberOfMusic(Long gameIndex);
    @Modifying(clearAutomatically = true)
    @Query("delete from Music m where m.id=?1")
    void deleteManual(String id);
}
