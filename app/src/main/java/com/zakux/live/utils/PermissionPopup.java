package com.zakux.live.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;

import com.zakux.live.R;
import com.zakux.live.databinding.PopupPermissionBinding;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class PermissionPopup {


    private Dialog dialog;

    public PermissionPopup(Context context, OnPlanClickListner onPlanClickListner) {

        dialog = new Dialog(context, R.style.customStyle);
        dialog.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        PopupPermissionBinding popupInsufficientCoinsBinding = DataBindingUtil.inflate(inflater, R.layout.popup_permission, null, false);
        dialog.setContentView(popupInsufficientCoinsBinding.getRoot());

        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }

        popupInsufficientCoinsBinding.btnallow.setOnClickListener(v -> {
            dialog.dismiss();
            onPlanClickListner.onDismiss();
        });
    }

    public interface OnPlanClickListner {


        void onDismiss();
    }
}
