package com.jose.castsocialconnector.photo;

import com.jose.castsocialconnector.instagram.InstagramUser;
import com.jose.castsocialconnector.instagram.InstagramUtils;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by dmunoz on 02-09-15.
 */
public class Photo {

    private String nickName = "";
    private String id;
    private String caption = "";
    private Long creationDate;
    private String highResUrl;
    private String lowResUrl;
    private Boolean seen;
    private InstagramUser instagramUser;

    public Photo(JSONObject element) {
        elementToPhoto(element);
    }

    public Photo(JSONObject element, String nickName) {
        elementToPhoto(element);
        this.nickName = nickName;
    }

    private void elementToPhoto(JSONObject element) {
        id = element.optString("id");
        JSONObject captionJSON = element.optJSONObject("caption");
        if (captionJSON != null)
            caption = InstagramUtils.processCaptionText(captionJSON.optString("text"));
        this.creationDate = element.optLong("created_time");
        JSONObject images = element.optJSONObject("images");
        JSONObject lowRes = images.optJSONObject("low_resolution");
        JSONObject highRes = images.optJSONObject("standard_resolution");
        highResUrl = highRes.optString("url");
        lowResUrl = lowRes.optString("url");
        seen = false;
        instagramUser = new InstagramUser(element.optJSONObject("user"));
    }

    public static Boolean isInList(Photo photo, ArrayList<Photo> list) {
        for (Photo p : list) {
            if (p.id.compareTo(photo.getId()) == 0)
                return true;
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public String getCaption() {
        return caption;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public String getHighResUrl() {
        return highResUrl;
    }

    public String getLowResUrl() {
        return lowResUrl;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public InstagramUser getInstagramUser() {
        return instagramUser;
    }

    public PhotoJSON toPhotoJSON() {
        PhotoJSON photoJSON = new PhotoJSON();
        photoJSON.setCaption(this.caption);
        photoJSON.setId(this.id);
        photoJSON.setHighResUrl(this.highResUrl);
        photoJSON.setNickname(this.nickName);
        return photoJSON;
    }
}
