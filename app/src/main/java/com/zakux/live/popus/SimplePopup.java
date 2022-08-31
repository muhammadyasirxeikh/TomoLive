package com.zakux.live.popus;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;

import com.zakux.live.R;
import com.zakux.live.databinding.ItemSimplepopupBinding;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Anurag Savaliya on 22-May-21.
 */
public class SimplePopup {

    private final Dialog dialog;

    public SimplePopup(Context context) {
        dialog = new Dialog(context, R.style.customStyle);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        ItemSimplepopupBinding popupbinding = DataBindingUtil.inflate(inflater, R.layout.item_simplepopup, null, false);

        dialog.setContentView(popupbinding.getRoot());
        dialog.show();
        popupbinding.btncountinue.setOnClickListener(v -> dialog.dismiss());

    }
}
