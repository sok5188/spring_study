package com.example.guess_music.domain.game;

import com.example.guess_music.domain.manage.Music;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class Answers implements Comparable<Answers> {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    public Game getGameIndex() {
        return gameIndex;
    }

    public void setGameIndex(Game gameIndex) {
        this.gameIndex = gameIndex;
    }

    @ManyToOne
    @JoinColumn(name = "gameIndex")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Game gameIndex;

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Music music;


    private Long seq;
    private String answer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
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

    @Override
    public int compareTo(Answers o) {
        return (int) (seq-o.seq);
    }

}
