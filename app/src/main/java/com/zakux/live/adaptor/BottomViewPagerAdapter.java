package com.zakux.live.adaptor;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.zakux.live.R;
import com.zakux.live.models.EmojiIconRoot;
import com.zakux.live.models.EmojicategoryRoot;
import com.zakux.live.retrofit.Const;
import com.zakux.live.retrofit.RetrofitBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomViewPagerAdapter extends PagerAdapter {
    EmojiListnerViewPager emojiListnerViewPager;
    private List<EmojicategoryRoot.Datum> categories = new ArrayList<>();

    public BottomViewPagerAdapter(List<EmojicategoryRoot.Datum> categories) {

        this.categories = categories;
    }

    public EmojiListnerViewPager getEmojiListnerViewPager() {
        return emojiListnerViewPager;
    }

    public void setEmojiListnerViewPager(EmojiListnerViewPager emojiListnerViewPager) {
        this.emojiListnerViewPager = emojiListnerViewPager;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_emojiviewpager, container, false);

        Log.d("TAG", "instantiateItem: " + categories.get(position).getName());
        RecyclerView recyclerView = view.findViewById(R.id.rvEmojiSheet);
        Call<EmojiIconRoot> call = RetrofitBuilder.create().getEmojiByCategory(Const.DEVKEY, categories.get(position).get_id());
        call.enqueue(new Callback<EmojiIconRoot>() {
            @Override
            public void onResponse(Call<EmojiIconRoot> call, Response<EmojiIconRoot> response) {
                if (response.code() == 200 && response.body().getStatus() && !response.body().getData().isEmpty()) {

                    EmojiAdapter emojiAdapter = new EmojiAdapter(response.body().getData());
                    recyclerView.setAdapter(emojiAdapter);
                    emojiAdapter.setOnEmojiClickListnear((bitmap, coin, emoji) -> emojiListnerViewPager.emojilistnerViewpager(bitmap, coin, emoji));
                }
            }

            @Override
            public void onFailure(Call<EmojiIconRoot> call, Throwable t) {
                Log.d("TAG", "onFailure: emoji adapter" + t.getMessage());
//ll
            }
        });

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);

    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public interface EmojiListnerViewPager {
        void emojilistnerViewpager(Bitmap bitmap, Long coin, EmojiIconRoot.Datum emoji);
    }
}
