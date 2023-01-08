package com.example.guess_music.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Game {
    @Id
    private Long gameIndex;
    private String title;
    private Long songnum;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getSongNum() {
        return songnum;
    }

    public void setSongNum(Long songnum) {
        this.songnum = songnum;
    }

    public Long getGameIndex() {
        return gameIndex;
    }

    public void setGameIndex(Long gameIndex) {
        this.gameIndex = gameIndex;
    }



}
