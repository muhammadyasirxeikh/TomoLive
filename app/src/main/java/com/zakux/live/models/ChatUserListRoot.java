package com.zakux.live.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatUserListRoot {

    @SerializedName("data")
    private List<DataItem> data;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public List<DataItem> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public boolean getStatus() {
        return status;
    }

    public static class DataItem {

        boolean isFake;

        @SerializedName("country_name")
        private String countryName;

        public String getCountryName() {
            return countryName;
        }

        public void setCountryName(String countryName) {
            this.countryName = countryName;
        }

        public DataItem(String countryName, boolean isFake, String name, String image, String time, String message) {
            this.countryName = countryName;
            this.isFake = isFake;
            this.name = name;
            this.image = image;
            this.time = time;
            this.message = message;
        }

        public boolean isFake() {
            return isFake;
        }

        public void setFake(boolean fake) {
            isFake = fake;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @SerializedName("name")
        private String name;

        @SerializedName("image")
        private String image;

        @SerializedName("topic")
        private String topic;

        @SerializedName("_id")
        private String id;

        @SerializedName("time")
        private String time;

        @SerializedName("message")
        private String message;

        public String getname() {
            return name;
        }

        public String getImage() {
            return image;
        }

        public String getTopic() {
            return topic;
        }

        public String getId() {
            return id;
        }

        public String getTime() {
            return time;
        }

        public String getMessage() {
            return message;
        }
    }
}