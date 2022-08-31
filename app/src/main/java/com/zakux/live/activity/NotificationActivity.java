package com.zakux.live.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.adaptor.NotificationAdapter;
import com.zakux.live.databinding.ActivityNotificationBinding;
import com.zakux.live.models.NotificationRoot;
import com.zakux.live.retrofit.Const;
import com.zakux.live.retrofit.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends BaseActivity {
    ActivityNotificationBinding binding;
    SessionManager sessionManager;
    NotificationAdapter notificationAdapter = new NotificationAdapter();
    private boolean isLoding = false;
    private int start = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        sessionManager = new SessionManager(this);
        binding.rvNotifications.setAdapter(notificationAdapter);
        getNotifications();
        MainActivity.isHostLive = false;

        binding.rvNotifications.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!binding.rvNotifications.canScrollVertically(1)) {
                    LinearLayoutManager manager = (LinearLayoutManager) binding.rvNotifications.getLayoutManager();
                    Log.d("TAG", "onScrollStateChanged: ");
                    int visibleItemcount = manager.getChildCount();
                    int totalitem = manager.getItemCount();
                    int firstvisibleitempos = manager.findFirstCompletelyVisibleItemPosition();
                    Log.d("TAG", "onScrollStateChanged:187   " + visibleItemcount);
                    Log.d("TAG", "onScrollStateChanged:188 " + totalitem);
                    if (!isLoding && (visibleItemcount + firstvisibleitempos >= totalitem) && firstvisibleitempos >= 0) {

                        start = start + Const.LIMIT;
                        getNotifications();

                    }
                }
            }
        });

    }

    private void getNotifications() {
        Call<NotificationRoot> call = RetrofitBuilder.create().getNotifications(sessionManager.getUser().getId(), start, Const.LIMIT);
        call.enqueue(new Callback<NotificationRoot>() {
            @Override
            public void onResponse(Call<NotificationRoot> call, Response<NotificationRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getData().isEmpty()) {
                        notificationAdapter.addData(response.body().getData());
                        isLoding = false;

                    } else if (start == 0) {
                        binding.lyt404.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificationRoot> call, Throwable t) {
//ll
            }
        });
    }

    public void onClickBack(View view) {
        onBackPressed();
    }
}