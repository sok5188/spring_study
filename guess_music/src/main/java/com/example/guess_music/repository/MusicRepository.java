package com.example.guess_music.repository;

import com.example.guess_music.domain.manage.Music;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicRepository extends JpaRepository<Music, String> {
}
