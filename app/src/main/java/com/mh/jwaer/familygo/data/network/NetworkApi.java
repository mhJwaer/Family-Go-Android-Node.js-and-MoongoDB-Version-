package com.mh.jwaer.familygo.data.network;

import com.mh.jwaer.familygo.data.models.AuthModel;
import com.mh.jwaer.familygo.data.models.AuthResponse;
import com.mh.jwaer.familygo.data.models.ResponseBody;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface NetworkApi {

    @POST("auth/login/")
    Call<AuthResponse> loginUser(@Body AuthModel authModel);

    @POST("auth/register/")
    Call<AuthResponse> signupUser(@Body AuthModel authModel);

    @POST("user/save-username/{userName}")
    Call<ResponseBody> updateUserName (@Path("userName") String userName);

    @Multipart
    @POST("user/avatar/")
    Call<ResponseBody> uploadProfileImage(@Part MultipartBody.Part avatar);

    @POST("circle/join/{circle_code}")
    Call<ResponseBody> joinCircle (@Path("circle_code") String circle_code);

    @GET("circle/create/")
    Call<ResponseBody> createCircle ();
}
