package com.zakux.live;

public interface LiveStatusListner {
    void isOnline(boolean b);

    void onFailure();
}
