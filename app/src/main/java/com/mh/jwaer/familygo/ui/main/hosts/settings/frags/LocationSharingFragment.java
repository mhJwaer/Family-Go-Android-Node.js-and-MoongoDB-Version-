package com.mh.jwaer.familygo.ui.main.hosts.settings.frags;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mh.jwaer.familygo.R;
import com.mh.jwaer.familygo.data.network.NetworkUtil;
import com.mh.jwaer.familygo.databinding.FragmentLocationSharingBinding;
import com.mh.jwaer.familygo.util.LoadingDialog;
import com.mh.jwaer.familygo.util.SystemCheck;
import com.mh.jwaer.familygo.util.UserClient;


import java.util.Timer;
import java.util.TimerTask;

import static com.mh.jwaer.familygo.util.CONSTANTS.MY_PERMISSIONS_REQUEST_LOCATION;


public class LocationSharingFragment extends Fragment {
    private Switch switch1;
    private LoadingDialog loadingDialog;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean isChecked;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentLocationSharingBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_location_sharing, container, false);

        switch1 = binding.switch1;
        loadingDialog = new LoadingDialog(getActivity());

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switch1.setChecked(UserClient.getUser().isSharing());

        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChecked = ((Switch) v).isChecked();


                if (!SystemCheck.isLocationPermissionGranted(getContext())) {
                    //request permission
                    checkLocationPermission();
                    return;
                }
                //show alert dialog for change settings
                showConfirmDialog();

            }
        });


    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown

                               requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        } else {
            //permission already granted
            //call alert dialog fun
            showConfirmDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted,
                //call show dialog
                showConfirmDialog();

            } else {
                // permission denied, Disable the
                // functionality that depends on this permission.
                Toast.makeText(getActivity(), "This functionality depends on location permissions,  " +
                        "you cant share your location with the circle unless you grant the location permissions", Toast.LENGTH_LONG).show();
                switch1.setChecked(UserClient.getUser().isSharing());
            }
        }
    }

    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("Change Location Settings")
                .setMessage("Sure you wont to change Location?\n members wont see your location update any more!")
                .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch1.setChecked(UserClient.getUser().isSharing());

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setIsSharingLocation(isChecked);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setIsSharingLocation(final boolean b) {

        final boolean[] resultFlag = new boolean[1];

        final OnCompleteListener<Void> onCompleteListener = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    resultFlag[0] = true;
                    loadingDialog.dissmissDialog();
                    Toast.makeText(getActivity(), "Settings are updated", Toast.LENGTH_SHORT).show();
                    switch1.setChecked(b);
                    UserClient.getUser().setIsSharing(b);
                } else {
                    resultFlag[0] = false;
                    loadingDialog.dissmissDialog();
                    Toast.makeText(getActivity(), "Some thing went wrong!\nplease try again later"
                            , Toast.LENGTH_SHORT).show();
                    switch1.setChecked(UserClient.getUser().isSharing());
                }
            }
        };


        if (NetworkUtil.isNetworkAvailable()) {
            final Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    timer.cancel();
                    if (!resultFlag[0]) { //  Timeout
                        // Your timeout code goes here
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(requireActivity().getApplicationContext(), "Request timed out", Toast.LENGTH_SHORT).show();
                                loadingDialog.dissmissDialog();
                                switch1.setChecked(UserClient.getUser().isSharing());
                            }
                        });
                    }
                }
            };
            // Setting timeout of 10 sec to the request
            timer.schedule(timerTask, 10000L);
        } else {
            // Internet not available
            loadingDialog.dissmissDialog();
            Toast.makeText(getActivity(), "No Internet Connectivity"
                    , Toast.LENGTH_SHORT).show();
            switch1.setChecked(UserClient.getUser().isSharing());
        }

    }


}
