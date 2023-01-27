package com.example.guess_music.domain;

import java.util.List;

public class Room {
    Long ownerId;
    Long gameIndex;

    List<Long> participantId;

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getGameIndex() {
        return gameIndex;
    }

    public void setGameIndex(Long gameIndex) {
        this.gameIndex = gameIndex;
    }

    public List<Long> getParticipantId() {
        return participantId;
    }

    public void setParticipantId(List<Long> participantId) {
        this.participantId = participantId;
    }
}
