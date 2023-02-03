package com.websocket.chat.model;

import lombok.Builder;

public class Agrees {
    private String name;
    private boolean Vote;

    @Builder
    public Agrees() {
        this.name = "";
        this.Vote = true;
    }

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
}
