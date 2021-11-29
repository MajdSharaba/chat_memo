package com.yawar.memo.model;

public class SearchRespone {
    String id;
    String name;
    String SecretNumber;
    String image;
    String phone;

    public SearchRespone(String id, String name, String secretNumber, String image,String phone) {
        this.id = id;
        this.name = name;
        SecretNumber = secretNumber;
        this.image = image;
        this.phone= phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecretNumber() {
        return SecretNumber;
    }

    public void setSecretNumber(String secretNumber) {
        SecretNumber = secretNumber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
