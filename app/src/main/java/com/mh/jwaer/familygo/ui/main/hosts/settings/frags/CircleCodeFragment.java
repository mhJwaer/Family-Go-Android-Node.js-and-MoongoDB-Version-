package com.mh.jwaer.familygo.ui.main.hosts.settings.frags;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.mh.jwaer.familygo.R;
import com.mh.jwaer.familygo.databinding.FragmentCircleCodeBinding;
import com.mh.jwaer.familygo.util.UserClient;

import static com.mh.jwaer.familygo.util.CONSTANTS.NULL_CIRCLE;


public class CircleCodeFragment extends Fragment {

    FragmentCircleCodeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_circle_code, container, false);


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (UserClient.getUser().getCircle().equals(NULL_CIRCLE)) return;

        binding.codeTV.setText(UserClient.getUser().getCircle());
    }
}
