package com.mh.jwaer.familygo.ui.auth;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mh.jwaer.familygo.data.models.AuthModel;
import com.mh.jwaer.familygo.data.models.AuthResponse;
import com.mh.jwaer.familygo.data.models.ErrorModel;
import com.mh.jwaer.familygo.data.models.ErrorResponse;
import com.mh.jwaer.familygo.data.models.ResponseBody;
import com.mh.jwaer.familygo.data.network.RetrofitClient;
import com.mh.jwaer.familygo.util.ErrorUtil;
import com.mh.jwaer.familygo.util.MyApplication;
import com.mh.jwaer.familygo.util.SharedPreferencesHelper;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mh.jwaer.familygo.util.CONSTANTS.ID_TOKEN;
import static com.mh.jwaer.familygo.util.CONSTANTS.SHARED_ACCESS_TOKEN;
import static com.mh.jwaer.familygo.util.CONSTANTS.SHARED_REFRESH_TOKEN;

public class AuthViewModel extends ViewModel {


    private MutableLiveData<AuthResponse> authResponse = new MutableLiveData<>();
    private MutableLiveData<ErrorResponse> errorResponse = new MutableLiveData<>();
    private MutableLiveData<ResponseBody> updateUserNameRes = new MutableLiveData<>();
    private MutableLiveData<ResponseBody> uploadImageRes = new MutableLiveData<>();

    public void loginUser(AuthModel authModel) {
        RetrofitClient.getInstance().loginUser(authModel).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NotNull Call<AuthResponse> call,@NotNull  Response<AuthResponse> response) {
                if (response.isSuccessful()){
                    AuthResponse authRes = response.body();

                    //save tokens into shared preferences
                    assert authRes != null;
                    SharedPreferencesHelper.put(MyApplication.getInstance().getApplicationContext(),
                            SHARED_ACCESS_TOKEN,
                            authRes.getAccessToken());
                    SharedPreferencesHelper.put(MyApplication.getInstance().getApplicationContext(),
                            SHARED_REFRESH_TOKEN,
                            authRes.getRefreshToken());

                    ID_TOKEN = authRes.getAccessToken();
                    authResponse.setValue(authRes);
                }
                else
                    errorResponse.setValue(ErrorUtil.parseError(response));
            }

            @Override
            public void onFailure(@NotNull Call<AuthResponse> call,@NotNull  Throwable t) {
                ErrorModel error = new ErrorModel(400, t.getMessage());
                errorResponse.setValue(new ErrorResponse(error));
            }
        });
    }

    public void singupUser(AuthModel authModel) {
        RetrofitClient.getInstance().signupUser(authModel).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NotNull Call<AuthResponse> call, @NotNull Response<AuthResponse> response) {
                if (response.isSuccessful()){
                    AuthResponse authRes = response.body();

                    //save tokens into shared preferences
                    assert authRes != null;
                    SharedPreferencesHelper.put(MyApplication.getInstance().getApplicationContext(),
                            SHARED_ACCESS_TOKEN,
                            authRes.getAccessToken());
                    SharedPreferencesHelper.put(MyApplication.getInstance().getApplicationContext(),
                            SHARED_REFRESH_TOKEN,
                            authRes.getRefreshToken());

                    ID_TOKEN = authRes.getAccessToken();
                    authResponse.setValue(authRes);
                }
                else
                    errorResponse.setValue(ErrorUtil.parseError(response));
            }

            @Override
            public void onFailure(@NotNull Call<AuthResponse> call, @NotNull Throwable t) {
                ErrorModel error = new ErrorModel(400, t.getMessage());
                errorResponse.setValue(new ErrorResponse(error));
            }
        });
    }

    public void updateUserNameInDB(String userName) {
        RetrofitClient.getInstance().updateUserName(userName).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    updateUserNameRes.setValue(response.body());
                }
                else{
                    errorResponse.setValue(ErrorUtil.parseError(response));
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                ErrorModel error = new ErrorModel(400, t.getMessage());
                errorResponse.setValue(new ErrorResponse(error));
            }
        });
    }

    public void uploadProfileImage(Uri imageUri) {

        File file = new File(Objects.requireNonNull(imageUri.getPath()));

        RequestBody body = RequestBody.create(file, MediaType.parse("image/*"));

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("avatar", file.getName(), body);

        RetrofitClient.getInstance().uploadProfileImage(filePart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull  Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    uploadImageRes.setValue(response.body());
                }
                else{
                    errorResponse.setValue(ErrorUtil.parseError(response));
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                ErrorModel error = new ErrorModel(400, t.getMessage());
                errorResponse.setValue(new ErrorResponse(error));
            }
        });

    }


    public LiveData<AuthResponse> getAuthResponse(){
        return authResponse;
    }
    public LiveData<ErrorResponse> getErrorResponse(){
        return errorResponse;
    }
    public LiveData<ResponseBody> getUserNameRes(){return updateUserNameRes;}
    public LiveData<ResponseBody> getUploadImageRes(){return uploadImageRes;}
}
