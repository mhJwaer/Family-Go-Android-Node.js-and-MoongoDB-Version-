package com.mh.jwaer.familygo.ui.main.hosts.settings.frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.mh.jwaer.familygo.databinding.FragmentCircleMembersBinding;
import com.mh.jwaer.familygo.ui.main.hosts.settings.SettingsViewModel;
import com.mh.jwaer.familygo.util.LoadingDialog;

import java.util.List;


public class CircleMembersFragment extends Fragment  implements MembersAdapter.OnItemClickListener{

    private FragmentCircleMembersBinding binding;
    private SettingsViewModel viewModel;
    private LoadingDialog loadingDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_circle_members, container, false);
        loadingDialog = new LoadingDialog(getActivity());

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

        loadingDialog.startAlertDialog();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.getCircleMembers().observe(this, new Observer<List<CircleMember>>() {
            @Override
            public void onChanged(List<CircleMember> circleMembers) {
                loadingDialog.dissmissDialog();
                setupRecyclerView(circleMembers);
            }
        });
    }

    private void setupRecyclerView(List<CircleMember> circleMembers) {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        MembersAdapter membersAdapter = new MembersAdapter(this, requireContext());
        membersAdapter.setUsersList(circleMembers);
        binding.recyclerView.setAdapter(membersAdapter);
    }


    @Override
    public void onItemClicked(int position) {

    }
}
