package com.shops.chiggys.build.api;

/**
 * Created by tamil@appoets.com on 30-08-2017.
 */

import com.shops.chiggys.models.AddCart;
import com.shops.chiggys.models.AddMoney;
import com.shops.chiggys.models.Address;
import com.shops.chiggys.models.Card;
import com.shops.chiggys.models.Message;
import com.shops.chiggys.models.ChangePassword;
import com.shops.chiggys.models.ClearCart;
import com.shops.chiggys.models.Cuisine;
import com.shops.chiggys.models.DisputeMessage;
import com.shops.chiggys.models.Favorite;
import com.shops.chiggys.models.FavoriteList;
import com.shops.chiggys.models.ForgotPassword;
import com.shops.chiggys.models.LoginModel;
import com.shops.chiggys.models.Order;
import com.shops.chiggys.models.Otp;
import com.shops.chiggys.models.PromotionResponse;
import com.shops.chiggys.models.Promotions;
import com.shops.chiggys.models.RegisterModel;
import com.shops.chiggys.models.ResetPassword;
import com.shops.chiggys.models.RestaurantsData;
import com.shops.chiggys.models.Search;
import com.shops.chiggys.models.ShopDetail;
import com.shops.chiggys.models.User;
import com.shops.chiggys.models.WalletHistory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface    ApiInterface {


    /*-------------USER--------------------*/

    @GET("api/user/profile")
    Call<User> getProfile(@QueryMap HashMap<String, String> params);

    @Multipart
    @POST("api/user/profile")
    Call<User> updateProfileWithImage(@PartMap() Map<String, RequestBody> partMap, @Part MultipartBody.Part filename);

    @FormUrlEncoded
    @POST("api/user/otp")
    Call<Otp> postOtp(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("api/user/register")
    Call<RegisterModel> postRegister(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("oauth/token")
    Call<LoginModel> postLogin(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("api/user/social/login")
    Call<LoginModel> postSocialLogin(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("api/user/forgot/password")
    Call<ForgotPassword> forgotPassword(@Field("phone") String mobile);

    @FormUrlEncoded
    @POST("api/user/reset/password")
    Call<ResetPassword> resetPassword(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("api/user/profile/password")
    Call<ChangePassword> changePassword(@FieldMap HashMap<String, String> params);


    @GET("api/user/notification")
    Call<FavoriteList> getNotification();

    /*-------------SHOP--------------------*/

    @GET("api/user/shops")
    Call<RestaurantsData> getshops(@QueryMap HashMap<String, String> params);

    @GET("api/user/categories")
    Call<ShopDetail> getCategories(@QueryMap HashMap<String, String> params);


    /*-------------CUISINE--------------------*/
    @GET("api/user/cuisines")
    Call<List<Cuisine>> getcuCuisineCall();

    /*-------------CART--------------------*/

    @FormUrlEncoded
    @POST("api/user/cart")
    Call<AddCart> postAddCart(@FieldMap HashMap<String, String> params);

    @GET("api/user/cart")
    Call<AddCart> getViewCart();

    @GET("api/user/clear/cart")
    Call<ClearCart> clearCart();

    @FormUrlEncoded
    @POST("api/user/order")
    Call<Order> postCheckout(@FieldMap HashMap<String, String> params);

    /*-------------ADDRESS--------------------*/

    @GET("api/user/address")
    Call<List<Address>> getAddresses();

    @POST("api/user/address")
    Call<Address> saveAddress(@Body Address address);

    @PATCH("api/user/address/{id}")
    Call<Address> updateAddress(@Path("id") int id, @Body Address address);

    @DELETE("api/user/address/{id}")
    Call<Message> deleteAddress(@Path("id") int id);

    /*-------------FAVORITE--------------------*/

    @FormUrlEncoded
    @POST("api/user/favorite")
    Call<Favorite> doFavorite(@Field("shop_id") int shop_id);

    @DELETE("api/user/favorite/{id}")
    Call<Favorite> deleteFavorite(@Path("id") int id);

    @GET("api/user/favorite")
    Call<FavoriteList> getFavoriteList();

    /*-------------ORDER--------------------*/

    @GET("api/user/ongoing/order")
    Call<List<Order>> getOngoingOrders();

    @GET("api/user/order/{id}")
    Call<Order> getParticularOrders(@Path("id") int id);

    @GET("api/user/order")
    Call<List<Order>> getPastOders();

    @DELETE("api/user/order/{id}")
    Call<Order> cancelOrder(@Path("id") int id, @Query("reason") String reason);

    @FormUrlEncoded
    @POST("api/user/rating")
    Call<Message> rate(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("api/user/reorder")
    Call <AddCart> reOrder(@FieldMap HashMap<String, String> params);

     /*-------------DISPUTE--------------------*/

    @GET("api/user/disputehelp")
    Call<List<DisputeMessage>> getDisputeList();

    @FormUrlEncoded
    @POST("api/user/dispute")
    Call<Order> postDispute(@FieldMap HashMap<String, String> params);


    /*-------------SEARCH--------------------*/
    @GET("api/user/search")
    Call<Search> getSearch(@QueryMap HashMap<String,String> params);

    /*-----------------------WALLET-----------------------*/
    @GET("api/user/wallet")
    Call<List<WalletHistory>> getWalletHistory();

    @GET("api/user/wallet/promocode")
    Call<List<Promotions>> getWalletPromoCode();

    @FormUrlEncoded
    @POST("api/user/wallet/promocode")
    Call<PromotionResponse> applyWalletPromoCode(@Field("promocode_id") String id);


    @GET("json?")
    Call<ResponseBody> getResponse(@Query("latlng") String param1, @Query("key") String param2);

    /*-------------PAYMENT--------------------*/
    @GET("api/user/card")
    Call<List<Card>> getCardList();

    @FormUrlEncoded
    @POST("api/user/card")
    Call<Message> addCard(@Field("stripe_token") String stripeToken);

    @FormUrlEncoded
    @POST("api/user/add/money")
    Call<AddMoney> addMoney(@FieldMap HashMap<String, String> params);

    @DELETE("api/user/card/{id}")
    Call<Message> deleteCard(@Path("id") int id);



}
