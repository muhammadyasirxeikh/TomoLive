package com.zakux.live.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.databinding.ActivityHostRequestBinding;
import com.zakux.live.models.RestResponse;
import com.zakux.live.retrofit.Const;
import com.zakux.live.retrofit.RetrofitBuilder;

import java.io.File;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.provider.MediaStore.MediaColumns.DATA;

public class HostRequestActivity extends BaseActivity {
    private static final int PERMISSION_REQUEST_CODE = 1001;
    ActivityHostRequestBinding binding;
    SessionManager sessionManager;
    private int galleryCode = 123;
    private Uri selectedImage;
    private String picturePath1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_host_request);
        sessionManager = new SessionManager(this);


        binding.hostImage.setOnClickListener(v -> {
            if (checkPermission()) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, galleryCode);
            } else {
                requestPermission();
            }
        });

        binding.txtNext.setOnClickListener(v -> sendRequest());


    }

    private void sendRequest() {
        String des = binding.bio.getText().toString();
        if (des.isEmpty()) {
            Toast.makeText(this, "Please Enter Bio", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedImage == null || picturePath1 == null || picturePath1.equals("")) {
            Toast.makeText(this, "Please Select Picture", Toast.LENGTH_SHORT).show();
            return;
        }


        RequestBody bio = RequestBody.create(MediaType.parse("text/plain"), des);
        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), sessionManager.getUser().getId());
        HashMap<String, RequestBody> map = new HashMap<>();


        MultipartBody.Part body = null;
        if (!picturePath1.isEmpty()) {
            File file = new File(picturePath1);
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
            body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        }


        map.put("user_id", id);
        map.put("bio", bio);

        binding.pd.setVisibility(View.VISIBLE);
        Call<RestResponse> call = RetrofitBuilder.create().addHostRequeest(Const.DEVKEY, map, body);
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code() == 200 && response.body().isStatus()) {
                    sessionManager.saveBooleanValue(Const.IS_FIRST_TIMEHOST, false);
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_layout_green,
                            (ViewGroup) findViewById(R.id.customtoastlyt_green));


                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();


                    new Handler().postDelayed(() -> onBackPressed(), 1500);
                }
                binding.pd.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
//ll
            }
        });
    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == galleryCode && resultCode == RESULT_OK && null != data) {
            selectedImage = null;
            selectedImage = data.getData();
            String[] filePathColumn = {DATA};

            Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            Glide.with(this).load(selectedImage).centerCrop().into(binding.hostImage);
            picturePath1 = cursor.getString(columnIndex);
            binding.addImage.setVisibility(View.GONE);

            cursor.close();


        }

    }

    public void onClickBack(View view) {
        onBackPressed();
    }
}