package com.mh.jwaer.familygo.ui.circle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mh.jwaer.familygo.data.models.ErrorModel;
import com.mh.jwaer.familygo.data.models.ErrorResponse;
import com.mh.jwaer.familygo.data.models.ResponseBody;
import com.mh.jwaer.familygo.data.network.RetrofitClient;
import com.mh.jwaer.familygo.util.ErrorUtil;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CircleViewModel extends ViewModel {

    private MutableLiveData<ResponseBody> joinCircleRes = new MutableLiveData<>();
    private MutableLiveData<ResponseBody> createCircleRes = new MutableLiveData<>();
    private MutableLiveData<ErrorResponse> errorResponse = new MutableLiveData<>();

    public void joinCircle(String code) {

        RetrofitClient.getInstance().joinCircle(code).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful())
                    joinCircleRes.setValue(response.body());
                else
                    errorResponse.setValue(ErrorUtil.parseError(response));
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                ErrorModel error = new ErrorModel(400, t.getMessage());
                errorResponse.setValue(new ErrorResponse(error));
            }
        });
    }

    public void createCircle() {
        RetrofitClient.getInstance().createCircle().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful())
                    createCircleRes.setValue(response.body());
                else
                    errorResponse.setValue(ErrorUtil.parseError(response));
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                ErrorModel error = new ErrorModel(400, t.getMessage());
                errorResponse.setValue(new ErrorResponse(error));
            }
        });
    }


    //return methods
    public LiveData<ResponseBody> getJoinCircleTask(){
        return joinCircleRes;
    }

    public LiveData<ResponseBody> getCreateCircleTask(){
        return createCircleRes;
    }

    public  LiveData<ErrorResponse> getErrorRes(){
        return errorResponse;
    }
}
