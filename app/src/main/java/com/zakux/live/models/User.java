package com.zakux.live.models;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("country")
    private String country;

    @SerializedName("dailyTaskFinishedCount")
    private int dailyTaskFinishedCount;

    @SerializedName("channel")
    private String channel;

    @SerializedName("bio")
    private String bio;

    @SerializedName("isOnline")
    private boolean isOnline;

    @SerializedName("isLogout")
    private boolean isLogout;

    @SerializedName("isHost")
    private boolean isHost;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("rate")
    private int rate;

    @SerializedName("identity")
    private String identity;

    @SerializedName("__v")
    private int V;

    @SerializedName("fcm_token")
    private String fcmToken;

    @SerializedName("block")
    private boolean block;

    @SerializedName("plan_start_date")
    private String planStartDate;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("image")
    private String image;

    @SerializedName("isBusy")
    private boolean isBusy;

    @SerializedName("isVIP")
    private boolean isVIP;

    @SerializedName("token")
    private String token;

    @SerializedName("following_count")
    private int followingCount;

    @SerializedName("followers_count")
    private int followersCount;

    @SerializedName("name")
    private String name;

    @SerializedName("_id")
    private String id;

    @SerializedName("plan_id")
    private String planId;


    @SerializedName("thumbImage")
    private String thumbImage;

    public String getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(String thumbImage) {
        this.thumbImage = thumbImage;
    }

    @SerializedName("coin")
    private int coin;

    @SerializedName("username")
    private String username;

    public String getCountry() {
        return country;
    }

    public int getDailyTaskFinishedCount() {
        return dailyTaskFinishedCount;
    }

    public String getChannel() {
        return channel;
    }

    public String getBio() {
        return bio;
    }

    public boolean isIsOnline() {
        return isOnline;
    }

    public boolean isIsLogout() {
        return isLogout;
    }

    public boolean isIsHost() {
        return isHost;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public int getRate() {
        return rate;
    }

    public String getIdentity() {
        return identity;
    }

    public int getV() {
        return V;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public boolean isBlock() {
        return block;
    }

    public String getPlanStartDate() {
        return planStartDate;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getImage() {
        return image;
    }

    public boolean isIsBusy() {
        return isBusy;
    }

    public boolean isIsVIP() {
        return isVIP;
    }

    public String getToken() {
        return token;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getPlanId() {
        return planId;
    }

    public int getCoin() {
        return coin;
    }

    public String getUsername() {
        return username;
    }
}