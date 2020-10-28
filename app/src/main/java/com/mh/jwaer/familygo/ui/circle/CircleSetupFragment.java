package com.mh.jwaer.familygo.ui.circle;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.mh.jwaer.familygo.R;
import com.mh.jwaer.familygo.data.models.ErrorResponse;
import com.mh.jwaer.familygo.data.models.ResponseBody;
import com.mh.jwaer.familygo.databinding.FragmentCircleSetupBinding;
import com.mh.jwaer.familygo.util.LoadingDialog;

public class CircleSetupFragment extends Fragment {

    private FragmentCircleSetupBinding binding;
    private CircleViewModel viewModel;
    private LoadingDialog loadingDialog;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_circle_setup, container, false);


        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CircleViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingDialog = new LoadingDialog(getActivity());
        navController = Navigation.findNavController(view);
        final Button joinCircleBtn = binding.joinCircleBtn;
        final Button createCircleBtn = binding.createCircleBtn;
        final EditText circleCodeEd = binding.circleCodeEd;

        joinCircleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String code = circleCodeEd.getText().toString().trim();
                if (code.length() != 6) {
                    circleCodeEd.setError(getString(R.string.code_length));
                    circleCodeEd.requestFocus();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                        .setTitle("Joining to '" + code + "'")
                        .setMessage(getString(R.string.sure_joining_new_circle))
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loadingDialog.startAlertDialog();
                                viewModel.joinCircle(code);
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        createCircleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.startAlertDialog();
                viewModel.createCircle();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.getJoinCircleTask().observe(this, new Observer<ResponseBody>() {
            @Override
            public void onChanged(ResponseBody responseBody) {
                loadingDialog.dissmissDialog();
                if (responseBody.isSuccessfull()){
                    navController.navigate(R.id.action_circleSetupFragment_to_mainActivity);
                }
                else
                    Toast.makeText(getContext(), "Error Joining Circle", Toast.LENGTH_SHORT).show();
            }
        });
        viewModel.getCreateCircleTask().observe(this, new Observer<ResponseBody>() {
            @Override
            public void onChanged(ResponseBody responseBody) {
                loadingDialog.dissmissDialog();
                if (responseBody.isSuccessfull()){
                    navController.navigate(R.id.action_circleSetupFragment_to_createCircleFragment);
                }
                else
                    Toast.makeText(getContext(), "Error Creating a Circle", Toast.LENGTH_SHORT).show();
            }
        });
        viewModel.getErrorRes().observe(this, new Observer<ErrorResponse>() {
            @Override
            public void onChanged(ErrorResponse errorResponse) {
                loadingDialog.dissmissDialog();
                Toast.makeText(getContext(), errorResponse.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}