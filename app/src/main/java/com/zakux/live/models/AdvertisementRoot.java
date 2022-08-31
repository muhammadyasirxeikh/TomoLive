package com.zakux.live.models;

import com.google.gson.annotations.SerializedName;

public class AdvertisementRoot {

    @SerializedName("facebook")
    private Facebook facebook;

    @SerializedName("google")
    private Google google;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public Facebook getFacebook() {
        return facebook;
    }

    public Google getGoogle() {
        return google;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public static class Google {

        @SerializedName("reward")
        private String reward;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("native")
        private String jsonMemberNative;

        @SerializedName("interstitial")
        private String interstrial;

        @SerializedName("__v")
        private int V;

        @SerializedName("show")
        private boolean show;

        @SerializedName("_id")
        private String id;

        @SerializedName("type")
        private String type;

        @SerializedName("updatedAt")
        private String updatedAt;

        public String getReward() {
            return reward;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getJsonMemberNative() {
            return jsonMemberNative;
        }

        public int getV() {
            return V;
        }

        public boolean isShow() {
            return show;
        }

        public String getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public String getInterstrial() {
            return interstrial;
        }

        public void setInterstrial(String interstrial) {
            this.interstrial = interstrial;
        }
    }

    public static class Facebook {
        @SerializedName("interstitial")
        private String interstrial;

        public String getInterstrial() {
            return interstrial;
        }

        public void setInterstrial(String interstrial) {
            this.interstrial = interstrial;
        }

        @SerializedName("reward")
        private String reward;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("native")
        private String jsonMemberNative;

        @SerializedName("__v")
        private int V;

        @SerializedName("show")
        private boolean show;

        @SerializedName("_id")
        private String id;

        @SerializedName("type")
        private String type;

        @SerializedName("updatedAt")
        private String updatedAt;

        public String getReward() {
            return reward;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getJsonMemberNative() {
            return jsonMemberNative;
        }

        public int getV() {
            return V;
        }

        public boolean isShow() {
            return show;
        }

        public String getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}