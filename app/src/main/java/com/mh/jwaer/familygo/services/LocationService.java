package com.mh.jwaer.familygo.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.mh.jwaer.familygo.data.models.ErrorResponse;
import com.mh.jwaer.familygo.data.models.ResponseBody;
import com.mh.jwaer.familygo.data.models.UpdateLocationModel;
import com.mh.jwaer.familygo.data.network.RetrofitClient;
import com.mh.jwaer.familygo.util.CONSTANTS;
import com.mh.jwaer.familygo.util.ErrorUtil;
import com.mh.jwaer.familygo.util.UserClient;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationService extends Service {

    //init
    private static final String TAG = "LocationService";
    private FusedLocationProviderClient mFusedLocationClient;
    private final static long UPDATE_INTERVAL = 60000 * 10;  /* 10 min */
    private final static long FASTEST_INTERVAL = 60000 * 5; /* 5 min */

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "myChannel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);


            ((NotificationManager) Objects.requireNonNull(getSystemService(Context.NOTIFICATION_SERVICE))).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Group App")
                    .setContentText("Your Location is being shared with your circle...").build();

            startForeground(1, notification);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        assert intent.getAction() != null;
        if (intent.getAction().equals(CONSTANTS.START_SERVICE_ACTION)) {
            getLastLocation();//get current Device Location
        }
        if (intent.getAction().equals(CONSTANTS.STOP_SERVICE_ACTION)) {
            stopForeground(true);
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    private void getLastLocation() {
        // ---------------------------------- LocationRequest ------------------------------------
        LocationRequest mLocationRequestBalancedAccuracy = new LocationRequest();
        mLocationRequestBalancedAccuracy.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequestBalancedAccuracy.setInterval(UPDATE_INTERVAL);
        mLocationRequestBalancedAccuracy.setFastestInterval(FASTEST_INTERVAL);

        LocationRequest mHighAccuracyRequest = new LocationRequest();
        mHighAccuracyRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);


        if ((ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            stopSelf();
            return;
        }
        if ((ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            stopSelf();
            return;
        }

        //getting location information:
        mFusedLocationClient.requestLocationUpdates(mLocationRequestBalancedAccuracy, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {

                        //Location Result got


                        final Location location = locationResult.getLastLocation();
                        updateLocation(location.getLatitude(), location.getLongitude());
//                        if (location != null) { //upload location to database /*do crypt to this var*\
//                            Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    updateLocation(location.getLatitude(), location.getLongitude());
//                                }
//                            }, 60000);
//
//                        }
                    }
                },
                Looper.myLooper());
    }

    private void updateLocation(double latitude, double longitude) {

        if (!UserClient.getUser().isSharing()) return;

        //update location to database
        UpdateLocationModel updateLocationModel = new UpdateLocationModel();
        updateLocationModel.setLatitude(String.valueOf(latitude));
        updateLocationModel.setLongitude(String.valueOf(longitude));


        RetrofitClient.getInstance().updateUserLocation(updateLocationModel).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    assert response.body() != null;
                    Log.d(TAG, "onResponse: "+ response.body().getMessage());
                }
                else
                {
                    ErrorResponse error = ErrorUtil.parseError(response);
                    Log.d(TAG, "onResponse: "+error.getError().getStatus()+ "\n"+error.getError().getMessage());
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                Log.i(TAG, "onFailure:" + t.getMessage());
            }
        });
    }
}
