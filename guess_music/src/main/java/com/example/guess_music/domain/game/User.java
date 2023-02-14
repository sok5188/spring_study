package com.example.guess_music.domain.game;

public class User {
    String name;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    String roomId;
    Long score;

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static User create(String name, String roomId, Long score) {
        User user = new User();
        user.name = name;
        user.roomId = roomId;
        user.score = score;
        return user;
    }
}
