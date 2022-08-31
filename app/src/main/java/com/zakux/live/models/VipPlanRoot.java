package com.zakux.live.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VipPlanRoot {

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

    public boolean isStatus() {
        return status;
    }

    public static class DataItem {

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("productId")
        private String productid;

        public String getProductid() {
            return productid;
        }

        public void setProductid(String productid) {
            this.productid = productid;
        }

        @SerializedName("price")
        private int price;

        @SerializedName("__v")
        private int V;

        @SerializedName("discount")
        private int discount;

        @SerializedName("_id")
        private String id;

        @SerializedName("time")
        private String time;

        @SerializedName("updatedAt")
        private String updatedAt;

        @SerializedName("paymentGateway")
        private String paymentGateway;

        public String getCreatedAt() {
            return createdAt;
        }

        public int getPrice() {
            return price;
        }

        public int getV() {
            return V;
        }

        public int getDiscount() {
            return discount;
        }

        public String getId() {
            return id;
        }

        public String getTime() {
            return time;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public String getPaymentGateway() {
            return paymentGateway;
        }
    }
}