package com.mh.jwaer.familygo.ui.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mh.jwaer.familygo.R;
import com.mh.jwaer.familygo.databinding.ActivityMainBinding;
import com.mh.jwaer.familygo.services.LocationService;
import com.mh.jwaer.familygo.ui.main.hosts.chat.ChatFragment;
import com.mh.jwaer.familygo.ui.main.hosts.maps.MapsFragment;
import com.mh.jwaer.familygo.ui.main.hosts.settings.SettingsFragment;
import com.mh.jwaer.familygo.util.CONSTANTS;
import com.mh.jwaer.familygo.util.SystemCheck;

public class MainActivity extends AppCompatActivity {

    private AlertDialog dialog;

    private final Fragment fragment1 = new MapsFragment();
    private final Fragment fragment2 = new SettingsFragment();
    private final Fragment fragment3 = new ChatFragment();
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment active = fragment1;


//--------------------------------Lifecycle methods overrides---------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        BottomNavigationView bottomNavigationView = binding.bottomNavigationView;
        fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.main_container, fragment1, "1").commit();

        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                //to prevent recreating fragment
                //do no thing here
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.mapsFragment:
                        fm.beginTransaction().hide(active).show(fragment1).commit();
                        active = fragment1;
                        return true;

                    case R.id.settingsFragment:
                        fm.beginTransaction().hide(active).show(fragment2).commit();
                        active = fragment2;
                        return true;


                    case R.id.chatFragment:
                        fm.beginTransaction().hide(active).show(fragment3).commit();
                        active = fragment3;
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
//check is location service is enabled
        if (!SystemCheck.isLocationEnabled(getApplicationContext())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Enable Location Service")
                    .setMessage("this app require location for regular use")
                    .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .setCancelable(false);
            dialog = builder.create();
            dialog.show();
        }

        if (SystemCheck.isLocationPermissionGranted(getApplicationContext())) {
            startLocationService();
        }
        else{
            Toast.makeText(this, "Grant Location Permission first", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        // if there is a fragment and the back stack of this fragment is not empty,
        // then emulate 'onBackPressed' behaviour, because in default, it is not working
        FragmentManager fm = getSupportFragmentManager();
        for (Fragment frag : fm.getFragments()) {
            if (frag.isVisible()) {
                FragmentManager childFm = frag.getChildFragmentManager();
                if (childFm.getBackStackEntryCount() > 0) {
                    childFm.popBackStack();
                    return;
                }
            }
        }
        super.onBackPressed();
    }

//------------------------------------------Helper Methods------------------------------------------
//location service **START || STOP**
    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent serviceIntent = new Intent(this, LocationService.class);
            serviceIntent.setAction(CONSTANTS.START_SERVICE_ACTION);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                MainActivity.this.startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.mh.jwaer.familygo.services.LocationService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}