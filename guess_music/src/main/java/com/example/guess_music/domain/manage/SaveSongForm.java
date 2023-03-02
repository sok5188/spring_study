package com.example.guess_music.domain.manage;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
@AllArgsConstructor
public class SaveSongForm {
    private String singer,initial;
    private List<String> answer;

    public List<String> getAnswer() {
        return answer;
    }

    public void setAnswer(List<String> answer) {
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


    public MultipartFile getMp3() {
        return mp3;
    }

    public void setMp3(MultipartFile mp3) {
        this.mp3 = mp3;
    }

    private MultipartFile mp3;

    public Long getGameIndex() {
        return gameIndex;
    }

    public void setGameIndex(Long gameIndex) {
        this.gameIndex = gameIndex;
    }

    private Long gameIndex;
}
