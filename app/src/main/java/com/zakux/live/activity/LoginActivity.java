package com.zakux.live.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;
import com.zakux.live.R;
import com.zakux.live.SessionManager;
import com.zakux.live.databinding.ActivityLoginBinding;
import com.zakux.live.databinding.BottomSheetUpdateprofileBinding;
import com.zakux.live.models.RestResponse;
import com.zakux.live.models.User;
import com.zakux.live.models.UserRoot;
import com.zakux.live.retrofit.Const;
import com.zakux.live.retrofit.RetrofitBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.provider.MediaStore.MediaColumns.DATA;

public class LoginActivity extends BaseActivity {
    private static final int PERMISSION_REQUEST_CODE = 101;
    private static final int GALLERY_CODE = 1001;
    private static final int RC_SIGN_IN = 100;
    private static final String STR_IMAGE = "image";
    private static final String STR_USERNAME = "username";
    private static final String STR_TEXT = "text/plain";
    SessionManager sessionManager;
    private GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    BottomSheetUpdateprofileBinding sheetDilogBinding;
    Call<RestResponse> userNameCall;
    private BottomSheetDialog bottomSheetDialog;
    Uri selectedImage;
    private String picturePath;
    private boolean userNameExist = false;
    private boolean emptyname = false;
    private boolean emptyusername = false;
    ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        sessionManager = new SessionManager(this);
        MainActivity.isHostLive = false;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();


        bottomSheetDialog = new BottomSheetDialog(LoginActivity.this, R.style.CustomBottomSheetDialogTheme);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet)
                    .setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        sheetDilogBinding = DataBindingUtil.inflate(LayoutInflater.from(LoginActivity.this), R.layout.bottom_sheet_updateprofile, null, false);

        binding.lythost.setOnClickListener(v -> {
            binding.animationView.setVisibility(View.VISIBLE);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", "CodderLab Host");
            jsonObject.addProperty("identity", "host@codderlab.com");
            jsonObject.addProperty(STR_IMAGE, Const.FAKE_IMGURL);
            jsonObject.addProperty(STR_USERNAME, "CodderlabHost");
            jsonObject.addProperty("country", sessionManager.getStringValue(Const.Country));
            jsonObject.addProperty("fcmtoken", sessionManager.getStringValue(Const.NOTIFICATION_TOKEN));


            Call<UserRoot> call = RetrofitBuilder.create().signUpUser(Const.DEVKEY, jsonObject);
            call.enqueue(new Callback<UserRoot>() {
                @Override
                public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                    if (response.code() == 200 && response.body().isStatus() && response.body().getUser() != null) {
                        user = response.body().getUser();
                        sessionManager.saveUser(user);

                        sessionManager.saveBooleanValue(Const.ISLOGIN, true);

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));

                    }
                    binding.animationView.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<UserRoot> call, Throwable t) {
                    Log.d("TAGlogin", "onFailure: " + t.getMessage());
                }
            });
        });
    }

    public void onClickWithGoogle(View view) {


        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private User user;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    Log.d("TAG", "firebaseAuthWithGoogle:" + account.getDisplayName());
                    signupUser(account);

                }

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
                // ...
            }
        }

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && null != data) {

            selectedImage = data.getData();

            Glide.with(this)
                    .load(selectedImage)
                    .circleCrop()
                    .into(sheetDilogBinding.imgUser);
            String[] filePathColumn = {DATA};

            Cursor cursor = LoginActivity.this.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();


        }

    }

    private void signupUser(GoogleSignInAccount account) {
        binding.animationView.setVisibility(View.VISIBLE);
        String[] username = account.getEmail().split("@");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", account.getDisplayName());
        jsonObject.addProperty("identity", account.getEmail());
        jsonObject.addProperty(STR_IMAGE, account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : Const.FAKE_IMGURL);
        jsonObject.addProperty(STR_USERNAME, username[0]);
        jsonObject.addProperty("country", sessionManager.getStringValue(Const.Country));
        jsonObject.addProperty("fcmtoken", sessionManager.getStringValue(Const.NOTIFICATION_TOKEN));


        Call<UserRoot> call = RetrofitBuilder.create().signUpUser(Const.DEVKEY, jsonObject);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && response.body().getUser() != null) {
                    user = response.body().getUser();
                    sessionManager.saveUser(user);

                    sessionManager.saveBooleanValue(Const.ISLOGIN, true);

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                   /* if (account.getPhotoUrl() != null) {


                    } else {
                        openEditSheet();
                    }*/
                }
                binding.animationView.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
                Log.d("TAGlogin", "onFailure: " + t.getMessage());
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        }
    }


    private void openEditSheet() {

        bottomSheetDialog.setContentView(sheetDilogBinding.getRoot());
        bottomSheetDialog.show();
        sheetDilogBinding.btnclose.setVisibility(View.INVISIBLE);
        sheetDilogBinding.etName.setText(user.getName());
        sheetDilogBinding.etbio.setText(user.getBio());
        sheetDilogBinding.etusername.setText(user.getUsername());
        sheetDilogBinding.etusername.setEnabled(false);
        Glide.with(LoginActivity.this).load(user.getImage()).circleCrop().addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                sheetDilogBinding.profilechar.setText(String.valueOf(user.getName().charAt(0)).toUpperCase());
                return true;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                sheetDilogBinding.imgUser.setImageDrawable(resource);
                return true;
            }
        }).into(sheetDilogBinding.imgUser);

        sheetDilogBinding.btnPencil.setOnClickListener(v -> choosePhoto());

        sheetDilogBinding.etusername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//ll
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                checkValidationOfUserName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
//ll
            }
        });

        sheetDilogBinding.tvSubmit.setOnClickListener(v -> updateProfile());
        sheetDilogBinding.tvskip.setOnClickListener(v -> {
            sessionManager.saveUser(user);
            sessionManager.saveBooleanValue(Const.ISLOGIN, true);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        });

    }

    private void updateProfile() {

        String name = sheetDilogBinding.etName.getText().toString();
        String username = sheetDilogBinding.etusername.getText().toString();
        String bio = sheetDilogBinding.etbio.getText().toString();

        if (name.equals("")) {
            emptyname = true;
            sheetDilogBinding.etName.setError("Required!");
        }
        if (username.equals("")) {
            emptyusername = true;
            sheetDilogBinding.etusername.setError("Required!");
        }
        if (userNameExist) {
            sheetDilogBinding.etusername.setError("Taken!");
        }

        if (emptyname && emptyusername && userNameExist) {
            return;
        }
        RequestBody fname = RequestBody.create(MediaType.parse(STR_TEXT), name);
        RequestBody uname = RequestBody.create(MediaType.parse(STR_TEXT), username);
        RequestBody ubio = RequestBody.create(MediaType.parse(STR_TEXT), bio);
        RequestBody userid = RequestBody.create(MediaType.parse(STR_TEXT), user.getId());

        HashMap<String, RequestBody> map = new HashMap<>();


        MultipartBody.Part body = null;
        if (picturePath != null) {
            File file = new File(picturePath);
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
            body = MultipartBody.Part.createFormData(STR_IMAGE, file.getName(), requestFile);

        }

        map.put("user_id", userid);
        map.put("name", fname);
        map.put(STR_USERNAME, uname);
        map.put("bio", ubio);

        binding.animationView.setVisibility(View.VISIBLE);
        Call<UserRoot> call = RetrofitBuilder.create().updateUser(Const.DEVKEY, map, body);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && response.body().getUser() != null) {
                        Toast.makeText(LoginActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                        sessionManager.saveUser(response.body().getUser());
                        sessionManager.saveBooleanValue(Const.ISLOGIN, true);
                        sessionManager.saveBooleanValue(Const.Is_Guest_Login, true);
                        getData();
                    } else {
                        Toast.makeText(LoginActivity.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                    }
                }
                binding.animationView.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
//ll
            }
        });
    }

    private void getData() {
        Log.d("checkinggg ", "getData: login");
        binding.animationView.setVisibility(View.VISIBLE);
        Call<UserRoot> call = RetrofitBuilder.create().getUserProfile(Const.DEVKEY, user.getId());
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && response.body().getUser() != null) {
                    user = response.body().getUser();
                    sessionManager.saveUser(user);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));

                }
                binding.animationView.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
//ll
            }
        });
    }

    private void checkValidationOfUserName(String toString) {
        if (userNameCall != null) {
            userNameCall.cancel();
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(STR_USERNAME, toString);
        jsonObject.addProperty("user_id", user.getId());
        userNameCall = RetrofitBuilder.create().checkUserName(Const.DEVKEY, jsonObject);
        userNameCall.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code() == 200 && !response.body().isStatus()) {
                    userNameExist = true;
                    sheetDilogBinding.etusername.setError("Taken!");
                } else {
                    userNameExist = false;
                }
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
//ll
            }
        });
    }

    private void choosePhoto() {
        if (checkPermission()) {
            Intent i = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, GALLERY_CODE);
        } else {
            requestPermission();
        }
    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(LoginActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .");
                choosePhoto();
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .");
            }
        }
    }


    public void onClickQuickLogin(View view) {
        binding.animationView.setVisibility(View.VISIBLE);
        String androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", getResources().getString(R.string.app_name) + " User");
        jsonObject.addProperty("identity", androidId);
        jsonObject.addProperty(STR_IMAGE, Const.FAKE_IMGURL);
        jsonObject.addProperty(STR_USERNAME, androidId);
        jsonObject.addProperty("country", sessionManager.getStringValue(Const.Country));
        jsonObject.addProperty("fcmtoken", sessionManager.getStringValue(Const.NOTIFICATION_TOKEN));


        Call<UserRoot> call = RetrofitBuilder.create().signUpUser(Const.DEVKEY, jsonObject);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && response.body().getUser() != null) {
                    user = response.body().getUser();
                    sessionManager.saveUser(user);
                    sessionManager.saveBooleanValue(Const.Is_Guest_Login, true);
                    sessionManager.saveBooleanValue(Const.ISLOGIN, true);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                 /*   try {
                        openEditSheet();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/

                }
                binding.animationView.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
                Log.d("TAGlogin", "onFailure: " + t.getMessage());
            }
        });

    }
}