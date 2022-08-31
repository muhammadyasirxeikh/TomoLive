package com.zakux.live.oflineModels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Basim on 19-May-21.
 */
public class ChatRootFake {
    private int flag;
    private String message;

    public ChatRootFake(int flag, String message) {
        this.flag = flag;
        this.message = message;
    }

    public static List<ChatRootFake> setFakeChates() {
        List<ChatRootFake> list = new ArrayList<>();
        list.add(new ChatRootFake(2, "Hii"));
        list.add(new ChatRootFake(2, "Hello"));
        list.add(new ChatRootFake(2, "How are you?"));
        list.add(new ChatRootFake(2, "Have a nice day"));
        list.add(new ChatRootFake(2, "Lets call"));
        list.add(new ChatRootFake(2, "Where are you?"));
        list.add(new ChatRootFake(2, "Give me your number"));
        list.add(new ChatRootFake(2, "Do you know me ?"));


        return list;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
