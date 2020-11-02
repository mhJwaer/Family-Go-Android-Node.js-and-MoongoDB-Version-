package com.mh.jwaer.familygo.ui.main.hosts.settings;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModel;

import com.mh.jwaer.familygo.data.models.CircleMember;
import com.mh.jwaer.familygo.data.models.ErrorModel;
import com.mh.jwaer.familygo.data.models.ErrorResponse;
import com.mh.jwaer.familygo.data.models.LogoutModel;
import com.mh.jwaer.familygo.data.models.ResponseBody;
import com.mh.jwaer.familygo.data.network.RetrofitClient;
import com.mh.jwaer.familygo.util.ErrorUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsViewModel extends ViewModel implements LifecycleObserver {


    private MutableLiveData<List<CircleMember>> circleMembers = new MutableLiveData<>();
    private MutableLiveData<ErrorResponse> errorResponse = new MutableLiveData<>();
    private MutableLiveData<ResponseBody> setAdminRes = new MutableLiveData<>();
    private MutableLiveData<ResponseBody> leaveCircleRes = new MutableLiveData<>();
    private MutableLiveData<ResponseBody> logoutRes = new MutableLiveData<>();
    private MutableLiveData<ResponseBody> updateCircleSettingsRes = new MutableLiveData<>();
    private MutableLiveData<ResponseBody> deleteCircleMemberRes = new MutableLiveData<>();

    public void leaveCircle() {
        RetrofitClient.getInstance().leaveCircle().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful())
                    leaveCircleRes.setValue(response.body());
                else
                    errorResponse.setValue(ErrorUtil.parseError(response));
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ErrorModel error = new ErrorModel(400, t.getMessage());
                errorResponse.setValue(new ErrorResponse(error));
            }
        });
    }

    public void logout(String refreshToken){

        RetrofitClient.getInstance().logout( new LogoutModel(refreshToken)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 204){

                    logoutRes.setValue(new ResponseBody(true, "logged out"));

                }
                else
                    errorResponse.setValue(ErrorUtil.parseError(response));

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                ErrorModel error = new ErrorModel(400, t.getMessage());
                errorResponse.setValue(new ErrorResponse(error));
            }
        });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void getCircleMember() {
        RetrofitClient.getInstance().getCircleMembers().enqueue(new Callback<List<CircleMember>>() {
            @Override
            public void onResponse(@NotNull Call<List<CircleMember>> call, @NotNull Response<List<CircleMember>> response) {
                if (response.isSuccessful()) {
                    circleMembers.setValue(response.body());
                }
                else
                    errorResponse.setValue(ErrorUtil.parseError(response));
            }

            @Override
            public void onFailure(@NotNull Call<List<CircleMember>> call, @NotNull Throwable t) {
                ErrorModel error = new ErrorModel(400, t.getMessage());
                errorResponse.setValue(new ErrorResponse(error));
            }
        });
    }

    public void setNewAdmin(String uid) {
        RetrofitClient.getInstance().changeCircleAdmin(uid).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful())
                    setAdminRes.setValue(response.body());
                else
                    errorResponse.setValue(ErrorUtil.parseError(response));

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ErrorModel error = new ErrorModel(400, t.getMessage());
                errorResponse.setValue(new ErrorResponse(error));
            }
        });
    }

    public void updateCircleSettings(boolean isChecked) {
        RetrofitClient.getInstance().changeCircleAccessibility(isChecked).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful())
                    updateCircleSettingsRes.setValue(response.body());
                else
                    errorResponse.setValue(ErrorUtil.parseError(response));

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ErrorModel error = new ErrorModel(400, t.getMessage());
                errorResponse.setValue(new ErrorResponse(error));
            }
        });

    }

    public void deleteCircleMember(String uid) {
        RetrofitClient.getInstance().deleteCircleMember(uid).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful())
                    deleteCircleMemberRes.setValue(response.body());
                else
                    errorResponse.setValue(ErrorUtil.parseError(response));
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ErrorModel error = new ErrorModel(400, t.getMessage());
                errorResponse.setValue(new ErrorResponse(error));
            }
        });
    }


    public LiveData<List<CircleMember>> getCircleMembers() {
        return circleMembers;
    }

    public LiveData<ResponseBody> getAdminRes() {
        return setAdminRes;
    }

    public LiveData<ResponseBody> getLeaveCircleRes() {
        return leaveCircleRes;
    }

    public LiveData<ResponseBody> getUpdateCircleSettingsRes() {
        return updateCircleSettingsRes;
    }

    public LiveData<ResponseBody> getDeleteCircleMemberRes() {
        return deleteCircleMemberRes;
    }

    public LiveData<ResponseBody> getLogoutResponse(){
        return logoutRes;
    }

    public LiveData<ErrorResponse> getErrorResponse(){
        return errorResponse;
    }

}
