package com.zakux.live.models;

import com.google.gson.annotations.SerializedName;

public class SettingsRoot {

    @SerializedName("data")
    private Data data;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public Data getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public static class Data {

        @SerializedName("agoraId")
        private String agoraId;

        @SerializedName("userCallCharge")
        private int callChargeforUser;
        @SerializedName("userLiveStreamingCharge")
        private int liveChargeforUser;

        @SerializedName("agoraCertificate")
        private String agoraCertificate;

        public String getAgoraCertificate() {
            return agoraCertificate;
        }

        public void setAgoraCertificate(String agoraCertificate) {
            this.agoraCertificate = agoraCertificate;
        }

        public int getCallChargeforUser() {
            return callChargeforUser;
        }

        public void setCallChargeforUser(int callChargeforUser) {
            this.callChargeforUser = callChargeforUser;
        }

        public int getLiveChargeforUser() {
            return liveChargeforUser;
        }

        public void setLiveChargeforUser(int liveChargeforUser) {
            this.liveChargeforUser = liveChargeforUser;
        }

        @SerializedName("hostCharge")
        private int hostCharge;

        @SerializedName("dailyTaskMaxValue")
        private int dailyTaskMaxValue;

        @SerializedName("minPoints")
        private int minPoints;

        @SerializedName("stripeSwitch")
        private boolean stripeSwitch;

        @SerializedName("loginBonus")
        private int loginBonus;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("dailyTaskMinValue")
        private int dailyTaskMinValue;

        @SerializedName("streamingMinValue")
        private int streamingMinValue;

        @SerializedName("googlePaySwitch")
        private boolean googlePaySwitch;

        @SerializedName("toCurrency")
        private int toCurrency;

        @SerializedName("stripeId")
        private String stripeId;

        @SerializedName("razorPayId")
        private String razorPayId;

        @SerializedName("__v")
        private int V;

        @SerializedName("howManyCoins")
        private int howManyCoins;

        @SerializedName("razorPaySwitch")
        private boolean razorPaySwitch;

        @SerializedName("redeemGateway")
        private String redeemGateway;

        @SerializedName("googlePayId")
        private String googlePayId;

        @SerializedName("currency")
        private String currency;

        @SerializedName("_id")
        private String id;

        @SerializedName("policyLink")
        private String policyLink;

        @SerializedName("streamingMaxValue")
        private int streamingMaxValue;

        @SerializedName("updatedAt")
        private String updatedAt;

        public String getAgoraId() {
            return agoraId;
        }

        public int getHostCharge() {
            return hostCharge;
        }

        public int getDailyTaskMaxValue() {
            return dailyTaskMaxValue;
        }

        public int getMinPoints() {
            return minPoints;
        }

        public boolean isStripeSwitch() {
            return stripeSwitch;
        }

        public int getLoginBonus() {
            return loginBonus;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public int getDailyTaskMinValue() {
            return dailyTaskMinValue;
        }

        public int getStreamingMinValue() {
            return streamingMinValue;
        }

        public boolean isGooglePaySwitch() {
            return googlePaySwitch;
        }

        public int getToCurrency() {
            return toCurrency;
        }

        public String getStripeId() {
            return stripeId;
        }

        public String getRazorPayId() {
            return razorPayId;
        }

        public int getV() {
            return V;
        }

        public int getHowManyCoins() {
            return howManyCoins;
        }

        public boolean isRazorPaySwitch() {
            return razorPaySwitch;
        }

        public String getRedeemGateway() {
            return redeemGateway;
        }

        public String getGooglePayId() {
            return googlePayId;
        }

        public String getCurrency() {
            return currency;
        }

        public String getId() {
            return id;
        }

        public String getPolicyLink() {
            return policyLink;
        }

        public int getStreamingMaxValue() {
            return streamingMaxValue;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}