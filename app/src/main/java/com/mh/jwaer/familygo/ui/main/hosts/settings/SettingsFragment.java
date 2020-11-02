package com.mh.jwaer.familygo.ui.main.hosts.settings;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mh.jwaer.familygo.R;
import com.mh.jwaer.familygo.data.models.ErrorResponse;
import com.mh.jwaer.familygo.data.models.ResponseBody;
import com.mh.jwaer.familygo.databinding.FragmentSettingsBinding;
import com.mh.jwaer.familygo.services.LocationService;
import com.mh.jwaer.familygo.ui.home.LoadingActivity;
import com.mh.jwaer.familygo.ui.main.hosts.settings.frags.ChangeAdminPermissionsFragment;
import com.mh.jwaer.familygo.ui.main.hosts.settings.frags.ChangeCircleFragment;
import com.mh.jwaer.familygo.ui.main.hosts.settings.frags.ChangeCircleSettingsFragment;
import com.mh.jwaer.familygo.ui.main.hosts.settings.frags.ChangePasswordFragment;
import com.mh.jwaer.familygo.ui.main.hosts.settings.frags.CircleCodeFragment;
import com.mh.jwaer.familygo.ui.main.hosts.settings.frags.CircleMembersFragment;
import com.mh.jwaer.familygo.ui.main.hosts.settings.frags.DeleteCircleMemberFragment;
import com.mh.jwaer.familygo.ui.main.hosts.settings.frags.LocationSharingFragment;
import com.mh.jwaer.familygo.ui.main.hosts.settings.frags.ProfileSettingFragment;
import com.mh.jwaer.familygo.util.CONSTANTS;
import com.mh.jwaer.familygo.util.LoadingDialog;
import com.mh.jwaer.familygo.util.SharedPreferencesHelper;
import com.mh.jwaer.familygo.util.UserClient;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.mh.jwaer.familygo.util.CONSTANTS.SHARED_ACCESS_TOKEN;
import static com.mh.jwaer.familygo.util.CONSTANTS.SHARED_REFRESH_TOKEN;


public class SettingsFragment extends Fragment
        implements View.OnClickListener
{

    private FragmentSettingsBinding binding;
    private SettingsViewModel viewModel;
    private LoadingDialog loadingDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);

        binding.personalCard.setOnClickListener(this);
        binding.cardShareLocation.setOnClickListener(this);
        binding.cardChangePassword.setOnClickListener(this);
        binding.cardSwitchPermissions.setOnClickListener(this);
        binding.cardCircleMembers.setOnClickListener(this);
        binding.cardChangeCircle.setOnClickListener(this);
        binding.cardReloadMap.setOnClickListener(this);
        binding.cardShareCircle.setOnClickListener(this);
        binding.cardLeaveCircle.setOnClickListener(this);
        binding.cardChangeCircleSettings.setOnClickListener(this);
        binding.cardLogOut.setOnClickListener(this);
        binding.cardDeleteCircleMember.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingDialog = new LoadingDialog(requireActivity());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personal_card:
                startFrag(new ProfileSettingFragment());
                break;

            case R.id.card_shareLocation:
                startFrag(new LocationSharingFragment());
                break;

            case R.id.card_changePassword:
                startFrag(new ChangePasswordFragment());
                break;
            case R.id.card_switchPermissions:
                if (!UserClient.getUser().isAdmin()) {
                    Toast.makeText(getContext(), "Require Circle Admin!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (UserClient.getUser().getCircle().equals(CONSTANTS.NULL_CIRCLE)) {
                    Toast.makeText(getContext(), "You don't have circle", Toast.LENGTH_SHORT).show();
                    return;
                }
                startFrag(new ChangeAdminPermissionsFragment());
                break;
            case R.id.card_circleMembers:
                startFrag(new CircleMembersFragment());
                break;
            case R.id.card_changeCircle:
                startFrag(new ChangeCircleFragment());
                break;
            case R.id.card_reloadMap:
                Intent intent = new Intent(getActivity(), LoadingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.card_shareCircle:
                startFrag(new CircleCodeFragment());
                break;
            case R.id.card_leaveCircle:
                showLeaveDialog();
                break;
            case R.id.card_changeCircleSettings:
                if (!UserClient.getUser().isAdmin()) {
                    Toast.makeText(getContext(), "Require Circle Admin!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (UserClient.getUser().getCircle().equals(CONSTANTS.NULL_CIRCLE)) {
                    Toast.makeText(getContext(), "You don't have circle", Toast.LENGTH_SHORT).show();
                    return;
                }
                startFrag(new ChangeCircleSettingsFragment());
                break;
            case R.id.card_deleteCircleMember:
                if (!UserClient.getUser().isAdmin()) {
                    Toast.makeText(getContext(), "Require Circle Admin!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (UserClient.getUser().getCircle().equals(CONSTANTS.NULL_CIRCLE)) {
                    Toast.makeText(getContext(), "You don't have circle", Toast.LENGTH_SHORT).show();
                    return;
                }
                startFrag(new DeleteCircleMemberFragment());
                break;
            case R.id.card_logOut:
                logout();
                break;
        }

    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Sign Out")
                .setMessage("Sure you wont to sign out?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadingDialog.startAlertDialog();
                        String refreshToken = SharedPreferencesHelper.get(requireContext(), SHARED_REFRESH_TOKEN, "")+"";
                        viewModel.logout(refreshToken);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.getLogoutResponse().observe(this, new Observer<ResponseBody>() {
            @Override
            public void onChanged(ResponseBody responseBody) {
                loadingDialog.dissmissDialog();
                if (responseBody.isSuccessfull()){
                    //stop location service if running
                    SharedPreferencesHelper.remove(requireContext().getApplicationContext(), SHARED_ACCESS_TOKEN );
                    SharedPreferencesHelper.remove(requireContext().getApplicationContext(), SHARED_REFRESH_TOKEN );
                    stopLocationService();
                    Intent intent = new Intent(getActivity(), LoadingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

        viewModel.getLeaveCircleRes().observe(this, new Observer<ResponseBody>() {
            @Override
            public void onChanged(ResponseBody responseBody) {
                loadingDialog.dissmissDialog();
                if (responseBody.isSuccessfull()){
                    Toast.makeText(getContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewModel.getErrorResponse().observe(this, new Observer<ErrorResponse>() {
            @Override
            public void onChanged(ErrorResponse errorResponse) {
                loadingDialog.dissmissDialog();
                Toast.makeText(getContext(), errorResponse.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLeaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Leave Circle")
                .setMessage("Sure you wont to 'LEAVE CIRCLE'?, this action cannot be undo!")
                .setNegativeButton("cancel", null)
                .setPositiveButton("Leave Circle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadingDialog.startAlertDialog();
                        viewModel.leaveCircle();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startFrag(Fragment fragInstance) {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.childFragmentContainer, fragInstance);
        ft.addToBackStack("fragInstance");
        ft.commit();
    }

    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent stopIntent = new Intent(getActivity(), LocationService.class);
            stopIntent.setAction(CONSTANTS.STOP_SERVICE_ACTION);
            requireActivity().startService(stopIntent);
        }
    }

    private boolean isLocationServiceRunning() {
        Log.d("TAG", "isLocationServiceRunning: starting the location service!");
        ActivityManager manager = (ActivityManager) requireActivity().getSystemService(ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.mh.jwaer.familygo.services.LocationService".equals(service.service.getClassName())) {
                Log.d("TAG", "isLocationServiceRunning: location is running!!!!!!!!!");
                return true;
            }
        }
        return false;
    }
}