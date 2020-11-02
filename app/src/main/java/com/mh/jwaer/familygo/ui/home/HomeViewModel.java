package com.mh.jwaer.familygo.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mh.jwaer.familygo.data.models.ErrorModel;
import com.mh.jwaer.familygo.data.models.ErrorResponse;
import com.mh.jwaer.familygo.data.models.ResponseBody;
import com.mh.jwaer.familygo.data.models.UserModel;
import com.mh.jwaer.familygo.data.network.RetrofitClient;
import com.mh.jwaer.familygo.util.ErrorUtil;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<UserModel> userRes = new MutableLiveData<>();
    private MutableLiveData<ErrorResponse> errorResponse = new MutableLiveData<>();


    //getUserDetails
    public void getCurrentUserDetails() {

        RetrofitClient.getInstance().getUserDetails().enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(@NotNull Call<UserModel> call, @NotNull Response<UserModel> response) {
                if (response.isSuccessful()){
                    userRes.setValue(response.body());
                }
                else{
                    errorResponse.setValue(ErrorUtil.parseError(response));
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserModel> call, @NotNull Throwable t) {
                ErrorModel error = new ErrorModel(400, t.getMessage());
                errorResponse.setValue(new ErrorResponse(error));
            }
        });
    }


    public LiveData<UserModel> getUserRes (){
        return userRes;
    }

    public LiveData<ErrorResponse> getErrorRes(){
        return errorResponse;
    }
}
