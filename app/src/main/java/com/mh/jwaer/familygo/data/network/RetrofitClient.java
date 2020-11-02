package com.mh.jwaer.familygo.data.network;

import android.util.Log;

import com.mh.jwaer.familygo.data.models.AuthModel;
import com.mh.jwaer.familygo.data.models.AuthResponse;
import com.mh.jwaer.familygo.data.models.CircleMember;
import com.mh.jwaer.familygo.data.models.LogoutModel;
import com.mh.jwaer.familygo.data.models.ResponseBody;
import com.mh.jwaer.familygo.data.models.UpdateLocationModel;
import com.mh.jwaer.familygo.data.models.UserLocationModel;
import com.mh.jwaer.familygo.data.models.UserModel;
import com.mh.jwaer.familygo.util.CONSTANTS;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String TAG = "RetrofitClient";
    public static final String BASE_URL = "http://192.168.100.100:3000/";


    private static RetrofitClient instance;

    public static RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }
    public static Retrofit retrofit() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor()) // used if network off OR on
//                .addInterceptor(httpLoggingInterceptor()) // used if network off OR on
//                .addNetworkInterceptor(networkInterceptor()) // only used when network is on
                .addNetworkInterceptor(authInterceptor())
//                .addInterceptor(offlineInterceptor())
                .build();
    }

    private static HttpLoggingInterceptor httpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor =
                new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(@NotNull String message) {
                        Log.d(TAG, "log: http log: " + message);
                    }
                });
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return httpLoggingInterceptor;
    }

    //network interceptor to add auth id token
    private static Interceptor authInterceptor() {
        return new Interceptor() {
            @NotNull
            @Override
            public Response intercept(@NotNull Chain chain) throws IOException {
                Log.d(TAG, "intercept: adding auth header");
                Request request = chain.request();
                request = request.newBuilder()
                        .header("Authorization", "Bearer " + CONSTANTS.ID_TOKEN)
                        .build();
                return chain.proceed(request);
            }

        };
    }

    private static NetworkApi getNetworkApi() {
        return retrofit().create(NetworkApi.class);
    }


    //user api call

    public Call<AuthResponse> loginUser (AuthModel authModel){
        return getNetworkApi().loginUser(authModel);
    }

    public Call<AuthResponse> signupUser (AuthModel authModel){
        return getNetworkApi().signupUser(authModel);
    }

    public Call<ResponseBody> updateUserName(String userName){
        return getNetworkApi().updateUserName(userName);
    }

    public Call<ResponseBody> uploadProfileImage(MultipartBody.Part avatar){
        return getNetworkApi().uploadProfileImage(avatar);
    }

    public Call<ResponseBody> joinCircle (String circle_code){
        return getNetworkApi().joinCircle(circle_code);
    }

    public  Call<ResponseBody> createCircle(){
        return getNetworkApi().createCircle();
    }

    public Call<UserModel> getUserDetails(){
        return getNetworkApi().getUserDetails();
    }

    public Call<ResponseBody> updateUserLocation(UpdateLocationModel location){
        return getNetworkApi().updateUserLocation(location);
    }

    public Call<List<CircleMember>> getCircleMembers(){
        return getNetworkApi().getCircleMembers();
    }

    public Call<List<UserLocationModel>> getCircleMembersLocations(){
        return getNetworkApi().getCircleMembersLocations();
    }

    public Call<ResponseBody> changeCircleAdmin(String targetUserId){
        return getNetworkApi().changeCircleMember(targetUserId);
    }

    public  Call<ResponseBody> leaveCircle(){
        return getNetworkApi().leaveCircle();
    }

    public Call<ResponseBody> changeCircleAccessibility(Boolean accessFlag){
        return getNetworkApi().changeCircleAccessibility(accessFlag);
    }
    public Call<ResponseBody> deleteCircleMember(String targetUserId){
        return getNetworkApi().deleteCircleMember(targetUserId);
    }

    public Call<Void>logout(LogoutModel logoutModel){
        return getNetworkApi().logout(logoutModel);
    }
}
