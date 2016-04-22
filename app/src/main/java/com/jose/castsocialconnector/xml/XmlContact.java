package com.jose.castsocialconnector.xml;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jose.castsocialconnector.instagram.InstagramUser;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * InstagramUser: diego
 * Date: 15-10-13
 * Time: 02:53 PM
 * Based on tutorial in http://androidexample.com/XML_Parsing_-_Android_Example/index.php?view=article_discription&aid=69&aaid=94
 * To change this template use File | Settings | File Templates.
 */
public class XmlContact implements Serializable{

    private long id;

    @Expose
    private String email;
    private String instagram;
    private InstagramUser instagramUser;
    @Expose
    private String nickname;
    @Expose
    private String photoURL;
    private String skype;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInstagram() {
        return instagram;
    }

    public InstagramUser getInstagramUser() {
        return instagramUser;
    }

    public void setInstagramUser(InstagramUser instagramUser) {
        this.instagramUser = instagramUser;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }
}
