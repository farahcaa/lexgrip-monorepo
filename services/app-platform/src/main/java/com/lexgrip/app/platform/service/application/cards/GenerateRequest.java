package com.lexgrip.app.platform.service.application.cards;

public class GenerateRequest {
    private String userMessage;

    public String getUserMessage(){
        return userMessage;
    }

    public GenerateRequest setUserMessage(String userMessage){
        this.userMessage = userMessage;
        return this;
    }
}
