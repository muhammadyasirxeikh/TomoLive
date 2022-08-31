package com.zakux.live.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.databinding.ActivitySupportBinding;
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

public class SupportActivity extends BaseActivity {

    private static final int GALLERY_CODE = 1001;
    private static final int PERMISSION_REQUEST_CODE = 101;
    ActivitySupportBinding binding;
    SessionManager sessionManager;
    String userId;
    Uri selectedImage;
    String picturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_support);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUser().getId();
        initmain();
    }

    private void initmain() {
        opengallery();
    }

    private void opengallery() {

        binding.btnOpenGallery.setOnClickListener(v -> {
            if (checkPermission()) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, GALLERY_CODE);
            } else {
                requestPermission();
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
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && null != data) {

            selectedImage = data.getData();

            Glide.with(this)
                    .load(selectedImage)
                    .circleCrop()
                    .into(binding.image);
            String[] filePathColumn = {DATA};

            Cursor cursor = this.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();


        }

    }

    public void onClickBack(View view) {
        finish();
    }

    public void onClickSubmit(View view) {
        String message = binding.etMessage.getText().toString().trim();
        if (message.equals("")) {
            binding.etMessage.setError("Required!");
            return;
        }
        RequestBody messagebody = RequestBody.create(MediaType.parse("text/plain"), message);
        RequestBody userIdbody = RequestBody.create(MediaType.parse("text/plain"), userId);
        HashMap<String, RequestBody> map = new HashMap<>();
        MultipartBody.Part body = null;
        Call<RestResponse> call = null;
        if (picturePath != null) {
            File file = new File(picturePath);
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
            body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);


        } else {
            // ll
        }
        call = RetrofitBuilder.create().addSupport(Const.DEVKEY, map, body);

        map.put("message", messagebody);
        map.put("user_id", userIdbody);
        binding.animationView.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus()) {
                        Toast.makeText(SupportActivity.this, "Complain Send Successfully", Toast.LENGTH_SHORT).show();
                        try {
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(SupportActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SupportActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                binding.animationView.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
            }
        });
    }
}