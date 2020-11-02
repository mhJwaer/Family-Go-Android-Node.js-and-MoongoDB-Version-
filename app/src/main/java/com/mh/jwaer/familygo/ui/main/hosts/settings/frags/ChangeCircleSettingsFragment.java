package com.mh.jwaer.familygo.ui.main.hosts.settings.frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mh.jwaer.familygo.R;
import com.mh.jwaer.familygo.data.models.ResponseBody;
import com.mh.jwaer.familygo.databinding.FragmentChangeCircleSettingsBinding;
import com.mh.jwaer.familygo.ui.main.hosts.settings.SettingsViewModel;
import com.mh.jwaer.familygo.util.CONSTANTS;
import com.mh.jwaer.familygo.util.LoadingDialog;
import com.mh.jwaer.familygo.util.UserClient;

public class ChangeCircleSettingsFragment extends Fragment {
    private LoadingDialog loadingDialog;
    private Switch switch1;
    private boolean checkState = false;
    private boolean checkNewState = false;
    private SettingsViewModel viewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentChangeCircleSettingsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_change_circle_settings, container, false);
        loadingDialog = new LoadingDialog(getActivity());
        switch1 = binding.switch1;
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
        if (UserClient.getUser().getCircle().equals(CONSTANTS.NULL_CIRCLE)) return;

        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNewState = ((Switch) v).isChecked();
                loadingDialog.startAlertDialog();
                viewModel.updateCircleSettings(checkNewState);
            }
        });

//        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                checkNewState = isChecked;
//                loadingDialog.startAlertDialog();
//                viewModel.updateCircleSettings(isChecked);
//            }
//        });

    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.getUpdateCircleSettingsRes().observe(this, new Observer<ResponseBody>() {
            @Override
            public void onChanged(ResponseBody res) {
                loadingDialog.dissmissDialog();
                if (res.isSuccessfull()) {
                    switch1.setChecked(checkNewState);
                    Toast.makeText(getContext(), res.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    switch1.setChecked(checkState);
                    Toast.makeText(getContext(), res.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
