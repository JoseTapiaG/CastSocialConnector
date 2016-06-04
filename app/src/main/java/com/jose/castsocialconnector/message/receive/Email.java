package com.jose.castsocialconnector.message.receive;

import com.google.gson.annotations.Expose;

/**
 * Created by Jose Manuel on 29/04/2016.
 */
public class Email {

    @Expose
    public String user;

    @Expose
    public String message;

    @Expose
    public String photoUrl;

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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
