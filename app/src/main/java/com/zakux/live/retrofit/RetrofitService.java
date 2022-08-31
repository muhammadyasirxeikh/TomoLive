package com.zakux.live.retrofit;

import com.google.gson.JsonObject;
import com.zakux.live.models.AdvertisementRoot;
import com.zakux.live.models.BecomeVipMemberRoot;
import com.zakux.live.models.ChatSendRoot;
import com.zakux.live.models.ChatTopicRoot;
import com.zakux.live.models.ChatUserListRoot;
import com.zakux.live.models.CoinHistoryRoot;
import com.zakux.live.models.CommentRootOriginal;
import com.zakux.live.models.CountryRoot;
import com.zakux.live.models.DailyTaskRoot;
import com.zakux.live.models.EmojiIconRoot;
import com.zakux.live.models.EmojicategoryRoot;
import com.zakux.live.models.GirlThumbListRoot;
import com.zakux.live.models.GuestUserRoot;
import com.zakux.live.models.HostEmojiRoot;
import com.zakux.live.models.IpAddressDataRoot;
import com.zakux.live.models.NewUserModel;
import com.zakux.live.models.NotificationRoot;
import com.zakux.live.models.OriginalMessageRoot;
import com.zakux.live.models.PaperRoot;
import com.zakux.live.models.PlanRoot;
import com.zakux.live.models.ProductKRoot;
import com.zakux.live.models.RechargeHistoryRoot;
import com.zakux.live.models.RestResponse;
import com.zakux.live.models.SettingsRoot;
import com.zakux.live.models.StikerRoot;
import com.zakux.live.models.UserRoot;
import com.zakux.live.models.ViewUserRoot;
import com.zakux.live.models.VipPlanRoot;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface RetrofitService {

    @GET("json")
    Call<IpAddressDataRoot> getCountryByIp();

    @POST("/user/signup")
    Call<UserRoot> signUpUser(@Header("key") String devkey, @Body JsonObject object);


    @DELETE("/user/logout")
    Call<RestResponse> logoutUser(@Header("key") String devkey, @Query("user_id") String uid);


    @Multipart
    @POST("user/edit_profile")
    Call<UserRoot> updateUser(@Header("key") String token,
                              @PartMap Map<String, RequestBody> partMap,
                              @Part MultipartBody.Part requestBody);


    @Multipart
    @POST("user/edit_profile")
    Call<UserRoot> updateUser(@Header("key") String token,
                              @PartMap Map<String, RequestBody> partMap);


    @POST("user/check_username")
    Call<RestResponse> checkUserName(@Header("key") String devkey, @Body JsonObject object);

    @GET("/user/profile")
    Call<UserRoot> getProfile(@Header("key") String devkey, @Query("user_id") String uid);

    @GET("/country")
    Call<CountryRoot> getCountries(@Header("key") String devkey);


    @GET("/thumblist")
    Call<GirlThumbListRoot> getThumbs(@Header("key") String devkey, @Query("country") String cid, @Query("start") int start, @Query("limit") int limit);


    @GET("/onlinehost")
    Call<GirlThumbListRoot> getOnlineHosts(@Header("key") String devkey, @Query("country") String cid, @Query("start") int start, @Query("limit") int limit);


    @POST("/user/allonlineusers")
    Call<NewUserModel> getOnlineuser(@Header("key") String devkey, @Body JsonObject object);

    @GET("/favouritelist")
    Call<GirlThumbListRoot> getFaviourites(@Header("key") String devkey, @Query("user_id") String uid);

    @GET("/user/profile")
    Call<UserRoot> getUserProfile(@Header("key") String devkey, @Query("user_id") String uid);


    @GET("/sticker")
    Call<StikerRoot> getStikers(@Header("key") String devkey);

    @GET("/emoji")
    Call<HostEmojiRoot> getHostEmoji(@Header("key") String devkey);


    @GET("/liveview")
    Call<ViewUserRoot> getTotalViewUser(@Header("token") String tkn, @Header("key") String devkey);

    @POST("/user/isonline")
    Call<RestResponse> actionOnlineHost(@Header("key") String token, @Body JsonObject jsonObject);

    @POST("/hostisonline")
    Call<RestResponse> hostIsOnline(@Header("key") String token, @Query("host_id") String hostId);

    @POST("/host/live")
    Call<RestResponse> actionLiveUserVideo(@Header("key") String token, @Body JsonObject jsonObject);

    @GET("/category")
    Call<EmojicategoryRoot> getCategories(@Header("key") String devkey);

    @GET("/gift/category")
    Call<EmojiIconRoot> getEmojiByCategory(@Header("key") String devkey, @Query("category") String cid);

    @POST("/user/less")
    Call<UserRoot> lessCoin(@Header("key") String devkey, @Body JsonObject object);

    @POST("history")
    Call<UserRoot> transferCoin(@Header("key") String devkey, @Body JsonObject object);

    @POST("/user/add")
    Call<UserRoot> addCoin(@Header("key") String devkey, @Body JsonObject object);


    @POST("/livecomment")
    Call<RestResponse> sendCommentToServer(@Header("key") String devkey, @Body JsonObject jsonObject);

    @GET("/livecomment")
    Call<CommentRootOriginal> getOldComments(@Header("key") String devkey, @Header("token") String tkn);


    @POST("/chattopic/add")
    Call<ChatTopicRoot> createChatTopic(@Header("key") String devkey, @Body JsonObject jsonObject);

    @GET("/chattopic")
    Call<ChatUserListRoot> getChatUserList(@Header("key") String devkey, @Query("user_id") String uid, @Query("start") int start, @Query("limit") int limit);

    @POST("/chat/oldchat")
    Call<OriginalMessageRoot> getOldMessage(@Header("key") String devkey, @Body JsonObject jsonObject);

    @POST("/chat/add")
    Call<ChatSendRoot> sendMessageToBackend(@Header("key") String devkey, @Body JsonObject jsonObject);

    @POST("/chattopic/search")
    Call<ChatUserListRoot> getSearchList(@Header("key") String devkey, @Body JsonObject jsonObject);


    @GET("user/globalsearch")
    Call<GuestUserRoot> globalSearch(@Header("key") String devkey, @Query("name") String keyword, @Query("user_id") String uid, @Query("start") int start, @Query("limit") int limit);


    @POST("/follow")
    Call<RestResponse> follow(@Header("key") String devkey, @Body JsonObject jsonObject);

    @POST("/unfollow")
    Call<RestResponse> unfollow(@Header("key") String devkey, @Body JsonObject jsonObject);

    @POST("/checkfollow")
    Call<RestResponse> checkFollow(@Header("key") String devkey, @Body JsonObject jsonObject);

    @POST("/followerlist")
    Call<GuestUserRoot> followrsList(@Header("key") String devkey, @Body JsonObject jsonObject);

    @POST("/followinglist")
    Call<GuestUserRoot> followingList(@Header("key") String devkey, @Body JsonObject jsonObject);

    @GET("/history/getrecharge")
    Call<RechargeHistoryRoot> getRechargeHistory(@Header("key") String devkey, @Query("user_id") String uid, @Query("start") int start, @Query("limit") int limit);

    @GET("/history/coinincome")
    Call<CoinHistoryRoot> getcoininflowHistory(@Header("key") String devkey, @Query("user_id") String uid, @Query("start") int start, @Query("limit") int limit);

    @GET("/history/coinoutcome")
    Call<CoinHistoryRoot> getcoinoutflowHistory(@Header("key") String devkey, @Query("user_id") String uid, @Query("start") int start, @Query("limit") int limit);

    @GET("/plan")
    Call<PlanRoot> getPlanList(@Header("key") String devkey);


    @GET("host/unlive")
    Call<RestResponse> destoryHost(@Header("key") String devkey, @Query("user_id") String hostid);


    @POST("/user/isoffline")
    Call<RestResponse> offlineHost(@Header("key") String devkey,@Body JsonObject jsonObject);


    @GET("hostisvalid")
    Call<RestResponse> checkHostIsValid(@Header("key") String devkey, @Query("host_id") String hostid);

    @POST("liveview")
    Call<RestResponse> destorytoken(@Header("key") String devkey, @Body JsonObject jsonObject);

    @POST("/history/purchasecoin")
    Call<RestResponse> purchaseCoin(@Header("key") String devkey, @Body JsonObject jsonObject);


    @POST("user/checkdailytask")
    Call<DailyTaskRoot> checkDailyTask(@Header("key") String devkey, @Body JsonObject jsonObject);

    @POST("user/dailytask")
    Call<RestResponse> updateDailyTask(@Header("key") String devkey, @Body JsonObject jsonObject);


    @GET("getnotification")
    Call<NotificationRoot> getNotifications(@Query("user_id") String userId, @Query("start") int start, @Query("limit") int limit);


    @GET("setting")
    Call<SettingsRoot> getSettings(@Header("key") String devkey);

    // key package
    @GET("/admin/api/gets")
    Call<PaperRoot> getPapers();

    @GET("/api/clientpackage")
    Call<ProductKRoot> getProducts(@Query("key") String key);


    @POST("redeem")
    Call<RestResponse> submitReedemRequest(@Header("key") String devkey, @Body JsonObject jsonObject);

    @POST("report")
    Call<RestResponse> reportThisUser(@Header("key") String devkey, @Body JsonObject jsonObject);


    @GET("/plan")
    Call<PlanRoot> getPlanListByPaymentGateway(@Header("key") String devkey);

    @GET("/VIPplan/paymentgateway")
    Call<VipPlanRoot> getVipPlans(@Header("key") String devkey);

    @POST("/user/addplan")
    Call<BecomeVipMemberRoot> becomeVip(@Header("key") String devkey, @Body JsonObject jsonObject);

    @GET("/advertisement")
    Call<AdvertisementRoot> getAdvertisement(@Query("key") String key);

    @Multipart
    @POST("support")
    Call<RestResponse> addSupport(@Header("key") String token,
                                  @PartMap Map<String, RequestBody> partMap,
                                  @Part MultipartBody.Part requestBody);


    @Multipart
    @POST("host")
    Call<RestResponse> addHostRequeest(@Header("key") String token,
                                       @PartMap Map<String, RequestBody> partMap,
                                       @Part MultipartBody.Part requestBody);


    @GET("host/connect")
    Call<RestResponse> busyHost(@Header("key") String devkey, @Query("user_id") String hostid);

    @GET("host/disconnect")
    Call<RestResponse> freeHost(@Header("key") String devkey, @Query("user_id") String hostid);


    @GET("hostisbusy")
    Call<RestResponse> hostIsBusyOrNot(@Header("key") String devkey, @Query("host_id") String hostid);


}
