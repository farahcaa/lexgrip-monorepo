package com.lexgrip.app.platform.service.application.account;

import java.math.BigInteger;

public class AccountPageDTO {

    private String name;
    private String email;
    private int cardsLeft;

    public String getName(){
        return name;
    }

    public AccountPageDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail (){
        return email;
    }

    public AccountPageDTO setEmail (String email) {
        this.email = email;
        return this;
    }

    public int getCardsLeft(){
        return cardsLeft;
    }

    public AccountPageDTO setCardsLeft(int cardsLeft){
        this.cardsLeft = cardsLeft;
        return this;
    }
}
