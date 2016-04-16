package com.jose.castsocialconnector.photo;

/**
 * Created by Jose Manuel on 11/04/2016.
 */
public class PhotoJSON {

    private String id;
    private String caption = "";
    private String highResUrl;
    private String nickname;
    private boolean seen;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getHighResUrl() {
        return highResUrl;
    }

    public void setHighResUrl(String highResUrl) {
        this.highResUrl = highResUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
