package com.yawar.memo.model;

import java.io.Serializable;

public class SendContactNumberResponse  implements Serializable {
    String id;
    String name;
    String number;
    String image;
    String state;

    public SendContactNumberResponse(String id ,String name, String number, String image, String state) {
        this.id = id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
