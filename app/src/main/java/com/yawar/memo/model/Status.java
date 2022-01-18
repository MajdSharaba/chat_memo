package com.yawar.memo.model;

public class Status {
    private boolean isSeen;
    private  String url;
    private String type;
    

    public Status(boolean isSeen, String url,String type ) {
        this.isSeen = isSeen;
        this.url =url;
        this.type=type;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }




    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        System.out.println("public void setSeen");
        isSeen = seen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
