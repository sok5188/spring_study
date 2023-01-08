package com.example.guess_music.domain;

import jakarta.persistence.*;

@Entity
public class Answers {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    public Game getGameIndex() {
        return gameIndex;
    }

    public void setGameIndex(Game gameIndex) {
        this.gameIndex = gameIndex;
    }

    @ManyToOne@JoinColumn(name = "gameIndex")
    private Game gameIndex;



    private int seq;
    private String answer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    private String singer;

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    private String initial;

}
