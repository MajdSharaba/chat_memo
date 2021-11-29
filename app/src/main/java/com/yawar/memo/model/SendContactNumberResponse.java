package com.yawar.memo.model;

public class SendContactNumberResponse {
    String name;
    String number;
    String image;
    String state;

    public SendContactNumberResponse(String name, String number, String image, String state) {
        this.name = name;
        this.number = number;
        this.image = image;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
