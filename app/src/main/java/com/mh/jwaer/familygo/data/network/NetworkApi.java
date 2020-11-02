package com.mh.jwaer.familygo.data.network;

import com.mh.jwaer.familygo.data.models.AuthModel;
import com.mh.jwaer.familygo.data.models.AuthResponse;
import com.mh.jwaer.familygo.data.models.CircleMember;
import com.mh.jwaer.familygo.data.models.LogoutModel;
import com.mh.jwaer.familygo.data.models.ResponseBody;
import com.mh.jwaer.familygo.data.models.UpdateLocationModel;
import com.mh.jwaer.familygo.data.models.UserLocationModel;
import com.mh.jwaer.familygo.data.models.UserModel;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface NetworkApi {

    @POST("auth/login/")
    Call<AuthResponse> loginUser(@Body AuthModel authModel);

    @POST("auth/register/")
    Call<AuthResponse> signupUser(@Body AuthModel authModel);

    @HTTP(method = "DELETE", path = "auth/logout", hasBody = true)
    Call<Void> logout(@Body LogoutModel logoutModel);

    @POST("user/save-username/{userName}")
    Call<ResponseBody> updateUserName (@Path("userName") String userName);

    @Multipart
    @POST("user/avatar/")
    Call<ResponseBody> uploadProfileImage(@Part MultipartBody.Part avatar);

    @POST("circle/join/{circle_code}")
    Call<ResponseBody> joinCircle (@Path("circle_code") String circle_code);

    @GET("circle/create/")
    Call<ResponseBody> createCircle ();

    @GET("user/")
    Call<UserModel> getUserDetails ();

    @POST("/user/location")
    Call<ResponseBody> updateUserLocation(@Body UpdateLocationModel location);

    @GET("circle/members")
    Call<List<CircleMember>> getCircleMembers();

    @GET("circle/members-locations")
    Call<List<UserLocationModel>> getCircleMembersLocations();

    @PATCH("circle/admin/{targetUserId}")
    Call<ResponseBody> changeCircleMember(@Path("targetUserId") String targetUserId);

    @PATCH("circle/leave")
    Call<ResponseBody> leaveCircle();

    @PATCH("circle/accessibility")
    Call<ResponseBody> changeCircleAccessibility(@Body Boolean accessFlag);

    @PATCH("circle/delete-member/{targetMemberId}")
    Call<ResponseBody> deleteCircleMember(@Path("targetMemberId") String targetUserId);
}
