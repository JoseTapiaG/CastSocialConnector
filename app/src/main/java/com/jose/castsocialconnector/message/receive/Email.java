package com.jose.castsocialconnector.message.receive;

/**
 * Created by Jose Manuel on 29/04/2016.
 */
public class Email {

    public String user;
    public String message;

    public Email(String user, String message) {
        this.user = user;
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
