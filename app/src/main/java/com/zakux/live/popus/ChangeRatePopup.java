package com.zakux.live.popus;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.databinding.ItemRatepopupBinding;
import com.zakux.live.models.User;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ChangeRatePopup {


    private final Dialog dialog;
    OnSubmitClickListnear onSubmitClickListnear;
    SessionManager sessionManager;
    public ChangeRatePopup(Context context, User user) {
        sessionManager = new SessionManager(context);
        dialog = new Dialog(context, R.style.customStyle);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        ItemRatepopupBinding popupbinding = DataBindingUtil.inflate(inflater, R.layout.item_ratepopup, null, false);

        dialog.setContentView(popupbinding.getRoot());

        Glide.with(context)
                .load(user.getImage()).error(R.drawable.bg_whitebtnround)
                .placeholder(R.drawable.bg_whitebtnround)
                .circleCrop()
                .into(popupbinding.imagepopup);

        popupbinding.tvName.setText(user.getName());
        popupbinding.tvusername.setText("@" + user.getUsername());
        popupbinding.tvdes.setText("Hello dear " + user.getName() + " you can change your rate/min here");

        int rate = user.getRate();
        if (rate == 0) {
            popupbinding.etRate.setText("");
        } else {
            popupbinding.etRate.setText(String.valueOf(user.getRate()));
        }

        popupbinding.btnSubmit.setOnClickListener(v -> {
            Log.d("TAG", "ChangeRatePopup: submit");

            if (popupbinding.etRate.getText().toString().length() > 6) {
                popupbinding.etRate.setError("Your Rate is Very High");
                return;
            }
            if (popupbinding.etRate.getText().toString().equals("")) {
                popupbinding.etRate.setError("Required!");

            } else {
                if (Integer.parseInt(popupbinding.etRate.getText().toString()) < sessionManager.getSetting().getStreamingMinValue()) {
                    popupbinding.etRate.setError("Minimum rate is " + sessionManager.getSetting().getStreamingMinValue());
                } else if (Integer.parseInt(popupbinding.etRate.getText().toString()) >= sessionManager.getSetting().getStreamingMaxValue()) {
                    popupbinding.etRate.setError("Maximum rate is " + sessionManager.getSetting().getStreamingMaxValue());
                } else {
                    onSubmitClickListnear.onSubmit(popupbinding.etRate.getText().toString());
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
        popupbinding.tvCencel.setOnClickListener(v -> dialog.dismiss());

    }

    public OnSubmitClickListnear getOnSubmitClickListnear() {
        return onSubmitClickListnear;
    }

    public void setOnSubmitClickListnear(OnSubmitClickListnear onSubmitClickListnear) {
        this.onSubmitClickListnear = onSubmitClickListnear;
    }

    public void close() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public interface OnSubmitClickListnear {
        void onSubmit(String rate);
    }
}
