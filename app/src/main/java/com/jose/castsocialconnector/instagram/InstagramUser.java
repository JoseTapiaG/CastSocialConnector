package com.jose.castsocialconnector.instagram;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by dmunoz on 15-09-15.
 *
 */
public class InstagramUser implements Serializable{

    private String id;
    private String email;
    private String profilePictureUrl;
    private String nickname;
    private String username;

    public InstagramUser(JSONObject json) {
        id = json.optString("id");
        profilePictureUrl = json.optString("profile_picture");
        username = json.optString("username");
        email = InstagramUtils.searchUserEmail(username);
        nickname = InstagramUtils.searchUserNickname(username);
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUsername() {
        return username;
    }
}
