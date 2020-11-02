package com.mh.jwaer.familygo.ui.main.hosts.settings.frags;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mh.jwaer.familygo.R;
import com.mh.jwaer.familygo.data.models.ResponseBody;
import com.mh.jwaer.familygo.databinding.FragmentChangeCircleBinding;
import com.mh.jwaer.familygo.ui.circle.CircleViewModel;
import com.mh.jwaer.familygo.ui.home.LoadingActivity;
import com.mh.jwaer.familygo.util.LoadingDialog;


public class ChangeCircleFragment extends Fragment {

    private FragmentChangeCircleBinding binding;
    private CircleViewModel viewModel;
    private LoadingDialog loadingDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_change_circle, container, false);


        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CircleViewModel.class);
        binding.setLifecycleOwner(this);
        loadingDialog = new LoadingDialog(getActivity());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.joinCircleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String code = binding.circleCodeEd.getText().toString().trim().toUpperCase();
                if (code.length() != 6) {
                    binding.circleCodeEd.setError(getString(R.string.code_length));
                    binding.circleCodeEd.requestFocus();
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


        binding.createCircleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.startAlertDialog();
                viewModel.createCircle();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.getJoinCircleTask().observe(this, new Observer<ResponseBody>() {
            @Override
            public void onChanged(ResponseBody response) {
                loadingDialog.dissmissDialog();
                if (!response.isSuccessfull()) {
                    Toast.makeText(getContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                //successfully joined the circle -> navigate to loading activity
                Toast.makeText(getContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), LoadingActivity.class));
            }
        });

        viewModel.getCreateCircleTask().observe(this, new Observer<ResponseBody>() {
            @Override
            public void onChanged(ResponseBody response) {
                loadingDialog.dissmissDialog();
                if (!response.isSuccessfull()) {
                    Toast.makeText(getContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                //successfully Created the circle -> navigate to CreateCircleActivity
                Intent intent = new Intent(getActivity(), LoadingActivity.class);
//                intent.putExtra("code", response.getMessage());

                startActivity(intent);
                requireActivity().finish();
            }
        });
    }
}
