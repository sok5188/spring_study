package com.example.guess_music.domain;

import jakarta.persistence.*;
@Entity
public class Member {


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memNo;
    private String id;
    private String password;
    private String name;

    public Long getMemNo() {
        return memNo;
    }

    public void setMemNo(Long memNo) {
        this.memNo = memNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
