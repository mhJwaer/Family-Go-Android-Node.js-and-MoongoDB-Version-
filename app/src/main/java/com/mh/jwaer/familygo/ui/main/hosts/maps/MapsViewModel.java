package com.mh.jwaer.familygo.ui.main.hosts.maps;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModel;

import com.mh.jwaer.familygo.data.models.CircleMember;
import com.mh.jwaer.familygo.data.models.CombinedMember;
import com.mh.jwaer.familygo.data.models.UserLocationModel;
import com.mh.jwaer.familygo.data.network.RetrofitClient;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mh.jwaer.familygo.util.CONSTANTS.LOCATION_UPDATE_INTERVAL;

public class MapsViewModel extends ViewModel implements LifecycleObserver {

    private static final String TAG = "MapsViewModel";

    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private boolean isRunnableRunning = false;

    private MutableLiveData<List<CombinedMember>> circleCombinedMembers = new MutableLiveData<>();
    private MutableLiveData<List<UserLocationModel>> circleMembersLocations = new MutableLiveData<>();
    private List<CircleMember> circleMembers = new ArrayList<>();
    private MutableLiveData<List<CircleMember>> circleMembersLiveData = new MutableLiveData<>();

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void getCircleMembers(){
        //request remote for circle members
        RetrofitClient.getInstance().getCircleMembers().enqueue(new Callback<List<CircleMember>>() {
            @Override
            public void onResponse(@NotNull Call<List<CircleMember>> call, @NotNull Response<List<CircleMember>> response) {
                if (response.isSuccessful()) {
                    circleMembers = response.body();
                    circleMembersLiveData.setValue(circleMembers);
                    requestCircleMembersLocation();
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<CircleMember>> call, @NotNull Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
            }
        });

    }

    private void requestCircleMembersLocation() {
    //request for circle members locations
        RetrofitClient.getInstance().getCircleMembersLocations().enqueue(new Callback<List<UserLocationModel>>() {
            @Override
            public void onResponse(@NotNull Call<List<UserLocationModel>> call, @NotNull Response<List<UserLocationModel>> response) {
                if (response.isSuccessful() && response.body() != null){
                    //now we got circle members and circle locations -> combine them and send back to fragment
                    combineObjects(circleMembers, response.body());
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<UserLocationModel>> call, @NotNull Throwable t) {
                Log.i(TAG, "onFailure: "+t.getMessage());
            }
        });

    }

    private void combineObjects(List<CircleMember> circleMembers, List<UserLocationModel> circleMembersLocation) {
        List<CombinedMember> combinedMembers = new ArrayList<>();
        for (CircleMember member : circleMembers)
            for (UserLocationModel memberLocation : circleMembersLocation)
                if (member.getUid().equals(memberLocation.getUid()))
                    combinedMembers.add(new CombinedMember(
                            member.getName(),
                            member.getUid(),
                            member.getPhotoUrl(),
                            memberLocation.getLatitude(),
                            memberLocation.getLongitude(),
                            member.isSharing(),
                            member.isAdmin(),
                            memberLocation.getTimestamp()
                    ));

        circleCombinedMembers.setValue(combinedMembers);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void startRetrieveMemberLocations() {
        if (isRunnableRunning) return;
        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                isRunnableRunning = true;
                //retrieve members locations as a List<UserLocation>
                if (! circleMembers.isEmpty()){
                    //there are members -> get there locations
                    getCircleMembersLocationsUpdates();
                }
                mHandler.postDelayed(mRunnable, LOCATION_UPDATE_INTERVAL);
            }
        }, LOCATION_UPDATE_INTERVAL);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void stopRetrieveUserLocations() {
        if (isRunnableRunning)
            mHandler.removeCallbacks(mRunnable);
        isRunnableRunning = false;
    }


    private void getCircleMembersLocationsUpdates() {
        //request for members location updates
        RetrofitClient.getInstance().getCircleMembersLocations().enqueue(new Callback<List<UserLocationModel>>() {
            @Override
            public void onResponse(@NotNull Call<List<UserLocationModel>> call, @NotNull Response<List<UserLocationModel>> response) {
                if (response.isSuccessful())
                    circleMembersLocations.setValue(response.body());
            }

            @Override
            public void onFailure(@NotNull Call<List<UserLocationModel>> call, @NotNull Throwable t) {
            }
        });
    }


    //return methods
    public LiveData<List<CombinedMember>> getCircleCombinedMembers() {
        return circleCombinedMembers;
    }

    public  LiveData<List<UserLocationModel>> getCircleMembersLocationsTask(){
        return circleMembersLocations;
    }

    public LiveData<List<CircleMember>> getCircleMembersTask(){
        return circleMembersLiveData;
    }
}
