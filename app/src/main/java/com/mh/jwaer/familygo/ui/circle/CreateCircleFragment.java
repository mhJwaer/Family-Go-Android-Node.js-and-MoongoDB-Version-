package com.mh.jwaer.familygo.ui.circle;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.mh.jwaer.familygo.R;
import com.mh.jwaer.familygo.databinding.FragmentCreateCircleBinding;
import com.mh.jwaer.familygo.ui.home.LoadingActivity;


public class CreateCircleFragment extends Fragment {

    private FragmentCreateCircleBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_circle, container, false);


        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null){

            CreateCircleFragmentArgs args = CreateCircleFragmentArgs.fromBundle(getArguments());
            String circleCode = args.getCircleCode();
            binding.codeTV.setText(circleCode);
        }

        binding.doneTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), LoadingActivity.class));
                requireActivity().finish();
            }
        });
    }
}