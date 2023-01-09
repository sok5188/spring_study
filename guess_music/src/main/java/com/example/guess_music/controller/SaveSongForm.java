package com.example.guess_music.controller;

import java.io.File;

public class SaveSongForm {
    private String answer,singer,initial;

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

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public File getMp3() {
        return mp3;
    }

    public void setMp3(File mp3) {
        this.mp3 = mp3;
    }

    private File mp3;
}
