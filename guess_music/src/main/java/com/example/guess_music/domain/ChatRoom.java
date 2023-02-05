package com.example.guess_music.domain;

import java.util.UUID;

public class ChatRoom {
    private String roomId;
    private String roomName;
    private Long gameIndex;

    public static ChatRoom create(Long gameIndex,String name) {
        ChatRoom room = new ChatRoom();
        room.roomId = UUID.randomUUID().toString();
        room.roomName = name;
        room.gameIndex=gameIndex;
        return room;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
