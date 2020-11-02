package com.mh.jwaer.familygo.ui.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.mh.jwaer.familygo.R;
import com.mh.jwaer.familygo.data.models.ErrorResponse;
import com.mh.jwaer.familygo.data.models.UserModel;
import com.mh.jwaer.familygo.ui.auth.AuthActivity;
import com.mh.jwaer.familygo.ui.main.MainActivity;
import com.mh.jwaer.familygo.util.SharedPreferencesHelper;
import com.mh.jwaer.familygo.util.UserClient;

import static com.mh.jwaer.familygo.util.CONSTANTS.ID_TOKEN;
import static com.mh.jwaer.familygo.util.CONSTANTS.SHARED_ACCESS_TOKEN;
import static com.mh.jwaer.familygo.util.CONSTANTS.SHARED_REFRESH_TOKEN;

public class LoadingActivity extends AppCompatActivity {


    private HomeViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticvity_loading);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        try {
//            String refreshToken = SharedPreferencesHelper.get(this, SHARED_REFRESH_TOKEN, "")+"";

            if (! SharedPreferencesHelper.contains(this,SHARED_ACCESS_TOKEN)){
                startActivity(new Intent(LoadingActivity.this, AuthActivity.class));
                LoadingActivity.this.finish();
            }
            ID_TOKEN = SharedPreferencesHelper.get(this,SHARED_ACCESS_TOKEN, "")+"";


        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        //get current User data
        viewModel.getCurrentUserDetails();

    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.getUserRes().observe(this, new Observer<UserModel>() {
            @Override
            public void onChanged(UserModel userModel) {
                UserClient.setUser(userModel);
                startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                LoadingActivity.this.finish();
            }
        });

        viewModel.getErrorRes().observe(this, new Observer<ErrorResponse>() {
            @Override
            public void onChanged(ErrorResponse errorResponse) {
                Toast.makeText(LoadingActivity.this, errorResponse.getError().getMessage(), Toast.LENGTH_SHORT).show();
                //handle error response! and caching data
            }
        });
    }
}