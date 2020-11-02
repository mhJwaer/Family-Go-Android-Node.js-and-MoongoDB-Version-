package com.mh.jwaer.familygo.ui.main.hosts.settings.frags;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.mh.jwaer.familygo.R;
import com.mh.jwaer.familygo.adapters.MembersAdapter;
import com.mh.jwaer.familygo.data.models.CircleMember;
import com.mh.jwaer.familygo.data.models.ErrorResponse;
import com.mh.jwaer.familygo.data.models.ResponseBody;
import com.mh.jwaer.familygo.data.network.NetworkUtil;
import com.mh.jwaer.familygo.databinding.FragmentDeleteCircleMemberBinding;
import com.mh.jwaer.familygo.ui.home.LoadingActivity;
import com.mh.jwaer.familygo.ui.main.hosts.settings.SettingsViewModel;
import com.mh.jwaer.familygo.util.LoadingDialog;
import com.mh.jwaer.familygo.util.UserClient;

import java.util.ArrayList;
import java.util.List;

public class DeleteCircleMemberFragment extends Fragment implements MembersAdapter.OnItemClickListener {
    private SettingsViewModel viewModel;
    private FragmentDeleteCircleMemberBinding binding;
    private LoadingDialog loadingDialog;
    private List<CircleMember> adapterList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_delete_circle_member, container, false);


        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        getLifecycle().addObserver(viewModel);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingDialog = new LoadingDialog(getActivity());

        if (!NetworkUtil.isNetworkAvailable()) {
            binding.textView.setText(getString(R.string.you_can_delete_circle_member_connectivity));
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        loadingDialog.startAlertDialog();
        viewModel.getCircleMembers().observe(requireActivity(), new Observer<List<CircleMember>>() {
            @Override
            public void onChanged(List<CircleMember> circleMembers) {
                loadingDialog.dissmissDialog();
                if (circleMembers.size() == 1) {
                    //only current user in the circle!
                    Toast.makeText(requireContext(), "add members first!", Toast.LENGTH_LONG).show();
                    return;
                }
                setupRecycler(circleMembers);
            }
        });

        viewModel.getDeleteCircleMemberRes().observe(this, new Observer<ResponseBody>() {
            @Override
            public void onChanged(ResponseBody res) {
                loadingDialog.dissmissDialog();
                if (res.isSuccessfull()){
                    Toast.makeText(requireContext().getApplicationContext(), res.getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoadingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(requireContext().getApplicationContext(), res.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewModel.getErrorResponse().observe(this, new Observer<ErrorResponse>() {
            @Override
            public void onChanged(ErrorResponse errorResponse) {
                loadingDialog.dissmissDialog();
                Toast.makeText(getContext(), errorResponse.getError().getMessage(), Toast.LENGTH_SHORT).show();
                binding.textView.setText(getString(R.string.admin_permission_desc_connectivity));
            }
        });
    }

    private void setupRecycler(List<CircleMember> circleMembers) {
        for (CircleMember member : circleMembers) {
            if (!member.getUid().equals(UserClient.getUser().getUid()))
                adapterList.add(member);
        }
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        MembersAdapter membersAdapter = new MembersAdapter(this, requireContext());
        membersAdapter.setUsersList(adapterList);
        binding.recyclerView.setAdapter(membersAdapter);
    }

    @Override
    public void onItemClicked(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("Change Circle Admin :")
                .setCancelable(false)
                .setMessage("are you sure want to delete '" + adapterList.get(position).getName() + " \nthis action cannot be undo")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.deleteCircleMember(adapterList.get(position).getUid());
                        dialog.dismiss();
                        loadingDialog.startAlertDialog();
                    }
                })
                .setNegativeButton("cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
