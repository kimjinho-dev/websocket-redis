package com.websocket.chat.model;

import lombok.Builder;

public class Guiltys {
    private String name;
    private boolean Vote;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getVote() {
        return Vote;
    }

    public void setVote(boolean vote) {
        Vote = vote;
    }

    @Builder
    public Guiltys() {
        this.name = "";
        this.Vote = true;
    }
}
