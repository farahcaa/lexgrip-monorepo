package com.lexgrip.app.platform.service.application.account;

import java.math.BigInteger;

public class AccountPageDTO {

    private String fullName;
    private String email;
    private int cardsLeft;

    public String getFullName(){
        return fullName;
    }

    public AccountPageDTO setFullName(String fullName) {
        this.fullName = fullName;
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
