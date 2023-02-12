package com.example.guess_music.domain;

import java.util.UUID;

public class ChatRoom {
    private String roomId;
    private String roomName;
    private Long gameIndex;
    private Long seq;
    private String roomStatus;

    public int getRoomUserNum() {
        return roomUserNum;
    }

    public void setRoomUserNum(int roomUserNum) {
        this.roomUserNum = roomUserNum;
    }

    private int roomUserNum;

    public String getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(String roomStatus) {
        this.roomStatus = roomStatus;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    private String ownerName;

    public Long getSongNum() {
        return songNum;
    }

    public void setSongNum(Long songNum) {
        this.songNum = songNum;
    }

    private Long songNum;

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    private String gameTitle;

    public Long getGameIndex() {
        return gameIndex;
    }

    public void setGameIndex(Long gameIndex) {
        this.gameIndex = gameIndex;
    }

    public static ChatRoom create(Long gameIndex, String name,String gameTitle,Long songNum,String ownerName) {
        ChatRoom room = new ChatRoom();
        room.roomId = UUID.randomUUID().toString();
        room.roomName = name;
        room.gameIndex=gameIndex;
        room.gameTitle=gameTitle;
        room.songNum=songNum;
        room.ownerName=ownerName;

        room.seq=1L;
        room.roomStatus = "대기 중";

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
