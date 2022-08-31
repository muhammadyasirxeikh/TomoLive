package com.zakux.live;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.zakux.live.models.ChatUserListRoot;
import com.zakux.live.models.RestResponse;
import com.zakux.live.models.Thumb;
import com.zakux.live.retrofit.Const;
import com.zakux.live.retrofit.RetrofitBuilder;
import com.zakux.live.token.RtcTokenBuilderSample;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LivexUtils {
    private static final String TAG = "livexutils";
    private static final String STR_INDIA = "INDIA";

    public static void setCustomToast(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    public static List<String> getNames() {
        List<String> names = new ArrayList<>();
        names.add("Nayan");
        names.add("Babu");
        names.add("Ramesh");
        names.add("Nayan");
        names.add("Prem");
        names.add("Raja");
        names.add("Vikrant");

        return names;
    }

    public static List<String> getComments() {
        List<String> names = new ArrayList<>();
        names.add("Hello ji");
        names.add("Heyy!!");
        names.add("I love you");
        names.add("you are so cute");
        names.add("7899044356 ye mera number he");
        names.add("Aap kaha se ho?");
        names.add("hello ji ");

        return names;
    }

//    public static List<Thumb> setFakeData() {
//        List<Thumb> thumbsFake = new ArrayList<>();
//        Thumb thumb1 = new Thumb(true, "INDIA",
//                "https://images.unsplash.com/photo-1522262590532-a991489a0253?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=564&q=80",
//                "sushmita", "Sushmita kanke",
//                "https://livme.codderlab.com/storage/160915023766994.mp4", 105, "fake", "", 1250, "", "");
//        Thumb thumb2 = new Thumb(true, "CHINA",
//                "https://images.unsplash.com/photo-1536811145290-bc394643e51e?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=564&q=80",
//                "Janki", "Janki", "https://livme.codderlab.com/storage/160914735380881.mp4", 57, "fake", "", 556, "", "");
//        Thumb thumb3 = new Thumb(true, "UAE",
//                "https://images.unsplash.com/photo-1601006986549-e028da30b614?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1072&q=80",
//                "malika", "malika roni", "https://livme.codderlab.com/storage/160914760959482.mp4", 11, "fake", "", 785, "", "");
//        Thumb thumb4 = new Thumb(true, "USA",
//                "https://images.unsplash.com/photo-1520694977332-9122aa8e8b7a?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=564&q=80",
//                "Sheela", "Sheela", "https://livme.codderlab.com/storage/160914898083391%20(1).mp4", 97, "fake", "", 52, "", "");
//        Thumb thumb5 = new Thumb(true, "UAE",
//                "https://images.unsplash.com/photo-1522765312985-2a1e2bce9ad7?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80",
//                "miya", "miya udari", "https://livme.codderlab.com/storage/160914898083391%20(1).mp4", 97, "fake", "", 52, "", "");
//
//        thumbsFake.add(thumb1);
//        thumbsFake.add(thumb2);
//        thumbsFake.add(thumb3);
//        thumbsFake.add(thumb4);
//        thumbsFake.add(thumb5);
//
//        return thumbsFake;
//    }
//
//    public static List<Thumb> setFakeHost() {
//        List<Thumb> thumbsFake = new ArrayList<>();
//        Thumb thumb1 = new Thumb(true, "INDIA", "https://images.unsplash.com/photo-1526574535041-ef5547111eb8?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=632&q=80", "Anupama", "Anupama", "https://livme.codderlab.com/storage/160915023766994.mp4", 105, "fake", "", 1250, "", "");
//        Thumb thumb2 = new Thumb(true, "USA", "https://images.unsplash.com/photo-1557173132-feaf86fb335e?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=634&q=80", "Luliya", "Luliya", "https://livme.codderlab.com/storage/160914735380881.mp4", 57, "fake", "", 556, "", "");
//        Thumb thumb3 = new Thumb(true, "UAE", "https://images.unsplash.com/photo-1517805686688-47dd930554b2?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=564&q=80", "Senorita", "Senorita", "https://livme.codderlab.com/storage/160914760959482.mp4", 11, "fake", "", 785, "", "");
//        Thumb thumb4 = new Thumb(true, "USA", "https://images.unsplash.com/photo-1505033852907-44fe8ec1e170?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=633&q=80", "Eiliya", "Eiliya", "https://livme.codderlab.com/storage/160914898083391%20(1).mp4", 97, "fake", "", 52, "", "");
//        Thumb thumb5 = new Thumb(true, "CHINA", "https://images.unsplash.com/photo-1492106087820-71f1a00d2b11?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80", "Juliya", "Juliya", "https://livme.codderlab.com/storage/160914898083391%20(1).mp4", 97, "fake", "", 52, "", "");
//
//        thumbsFake.add(thumb1);
//        thumbsFake.add(thumb2);
//        thumbsFake.add(thumb3);
//        thumbsFake.add(thumb4);
//        thumbsFake.add(thumb5);
//
//        return thumbsFake;
//    }

    public static void destoryHost(Context context) {
        SessionManager sessionManager = new SessionManager(context);
        String chenal = sessionManager.getUser().getId();
        try {
            String tkn = RtcTokenBuilderSample.main(sessionManager.getSetting().getAgoraId(), sessionManager.getSetting().getAgoraCertificate(), chenal);

            Call<RestResponse> call = RetrofitBuilder.create().destoryHost(Const.DEVKEY, chenal);
            call.enqueue(new Callback<RestResponse>() {
                @Override
                public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                    Log.d(TAG, "onResponse: host destoried");
                }

                @Override
                public void onFailure(Call<RestResponse> call, Throwable t) {
//ll
                }
            });

            JsonObject jsonObject2 = new JsonObject();
            jsonObject2.addProperty("token", tkn); //chanel is user id
            Call<RestResponse> call2 = RetrofitBuilder.create().destorytoken(Const.DEVKEY, jsonObject2);
            call2.enqueue(new Callback<RestResponse>() {
                @Override
                public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                    Log.d(TAG, "onResponse: token destoried");
                }

                @Override
                public void onFailure(Call<RestResponse> call, Throwable t) {
//ll
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getCurrency(Context context) {

        return new SessionManager(context).getSetting().getCurrency();
    }

//    public static List<ChatUserListRoot.DataItem> setFakeChat() {
//        List<ChatUserListRoot.DataItem> list = new ArrayList<>();
//
//        ChatUserListRoot.DataItem chat1 = new ChatUserListRoot.DataItem("INDIA", true, "Sushmita",
//                "https://images.unsplash.com/photo-1522262590532-a991489a0253?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=564&q=80",
//                "5 Minutes ago", "Hello Dear...");
//
//        ChatUserListRoot.DataItem chat2 = new ChatUserListRoot.DataItem("CHINA", true, "Janki",
//                "https://images.unsplash.com/photo-1536811145290-bc394643e51e?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=564&q=80",
//                "30 minutes ago", "Hii");
//
//
//        ChatUserListRoot.DataItem chat3 = new ChatUserListRoot.DataItem("UAE", true, "Malika",
//                "https://images.unsplash.com/photo-1601006986549-e028da30b614?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1072&q=80",
//                "2 Hour ago", "Have a nice day");
//
//
//        ChatUserListRoot.DataItem chat4 = new ChatUserListRoot.DataItem("USA", true, "Sheela",
//                "https://images.unsplash.com/photo-1520694977332-9122aa8e8b7a?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=564&q=80",
//                "3 Hour ago", "Chat with me");
//
//
//        ChatUserListRoot.DataItem chat5 = new ChatUserListRoot.DataItem("UAE", true, "Miya",
//                "https://images.unsplash.com/photo-1522765312985-2a1e2bce9ad7?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80",
//                "5 Hour ago", "Hey Babes Lets Fun");
//
//
//        list.add(chat1);
//        list.add(chat2);
//        list.add(chat3);
//        list.add(chat4);
//        list.add(chat5);
//        return list;
//    }
}
