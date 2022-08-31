package com.zakux.live.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.databinding.DataBindingUtil;

import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.databinding.ActivityWebBinding;
import com.zakux.live.retrofit.Const;

public class WebActivity extends BaseActivity {

    ActivityWebBinding binding;
    String website;
    private boolean loadingFinished;
    private boolean redirect;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web);


        Intent intent = getIntent();
        if (intent != null) {
            website = intent.getStringExtra("website");
            title = intent.getStringExtra("title");
            binding.tvtitle.setText(title);
            loadUrl(website);
        }

        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.getBooleanValue(Const.ISLOGIN)) {
            binding.imgback.setVisibility(View.VISIBLE);
            binding.lytbottm.setVisibility(View.GONE);
        } else {
            binding.imgback.setVisibility(View.GONE);
            binding.lytbottm.setVisibility(View.VISIBLE);

        }

    }

    private void loadUrl(String url) {
        if (url != null) {
            binding.webview.loadUrl(url);

            binding.webview.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
                    if (!loadingFinished) {
                        redirect = true;
                    }

                    loadingFinished = false;
                    view.loadUrl(urlNewString);
                    return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                    loadingFinished = false;
                    //SHOW LOADING IF IT ISNT ALREADY VISIBLE
                    binding.pd.setVisibility(View.VISIBLE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if (!redirect) {
                        loadingFinished = true;
                        binding.pd.setVisibility(View.GONE);
                    }

                    if (loadingFinished && !redirect) {
                        //HIDE LOADING IT HAS FINISHED
                        binding.pd.setVisibility(View.GONE);
                    } else {
                        redirect = false;
                        binding.pd.setVisibility(View.GONE);
                    }

                }
            });

        }

    }

    public void onClickBack(View view) {
        onBackPressed();
    }

    public void onClickAgree(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void onClickCencel(View view) {
        finishAffinity();
    }
}