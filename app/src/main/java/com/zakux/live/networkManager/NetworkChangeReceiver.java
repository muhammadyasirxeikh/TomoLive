package com.zakux.live.networkManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {
    OnNetworkListner onNetworkListner;

    public OnNetworkListner getOnNetworkListner() {
        return onNetworkListner;
    }

    public void setOnNetworkListner(OnNetworkListner onNetworkListner) {
        this.onNetworkListner = onNetworkListner;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {

        int status = NetworkUtil.getConnectivityStatusString(context);
        Log.e("network reciever", "Sulod sa network reciever");
        Log.d("TAG", "onReceive: " + intent.getAction());
        Log.d("TAG", "onReceive: " + status);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                onNetworkListner.onDisconnnected();
                Log.d("TAG", "onReceive: disconnect");
                Toast.makeText(context, "Network Disconnected", Toast.LENGTH_SHORT).show();
            } else {
                onNetworkListner.onConnected();
                Log.d("TAG", "onReceive: connect");
            }
        }
    }

    public interface OnNetworkListner {
        void onDisconnnected();

        void onConnected();
    }
}
