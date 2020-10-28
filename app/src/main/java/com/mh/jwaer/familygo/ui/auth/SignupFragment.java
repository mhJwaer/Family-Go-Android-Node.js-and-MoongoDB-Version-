package com.mh.jwaer.familygo.ui.auth;

import android.os.Bundle;
import android.util.Patterns;
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
import com.mh.jwaer.familygo.data.models.AuthModel;
import com.mh.jwaer.familygo.data.models.AuthResponse;
import com.mh.jwaer.familygo.data.models.ErrorResponse;
import com.mh.jwaer.familygo.databinding.FragmentSignupBinding;
import com.mh.jwaer.familygo.util.LoadingDialog;

public class SignupFragment extends Fragment {

    private FragmentSignupBinding binding;
    private AuthViewModel viewModel;
    private LoadingDialog loadingDialog;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingDialog = new LoadingDialog(getActivity());
        navController = Navigation.findNavController(view);

        final Button signUpBtn = binding.signUpBtn;
        final EditText emailEd = binding.emailEd;
        final EditText passEd = binding.passEd;
        final EditText conPassEd = binding.conPassEd;

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Patterns.EMAIL_ADDRESS.matcher(emailEd.getText().toString().trim()).matches()){
                    emailEd.setError(getString(R.string.email_not_valid));
                    return;
                }
                if (passEd.getText().toString().isEmpty()){
                    passEd.setError(getString(R.string.pass_req));
                    return;
                }
                if (passEd.getText().toString().length() > 6){
                    passEd.setError(getString(R.string.pass_length));
                    return;
                }
                if (!conPassEd.getText().toString().equals(passEd.getText().toString())){
                    conPassEd.setError(getString(R.string.passwords_matching));
                    return;
                }
                loadingDialog.startAlertDialog();
                viewModel.singupUser(new AuthModel( emailEd.getText().toString().trim(),
                        passEd.getText().toString() ));

            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.getAuthResponse().observe(this, new Observer<AuthResponse>() {
            @Override
            public void onChanged(AuthResponse authResponse) {
                loadingDialog.dissmissDialog();
                if (authResponse != null)
                    navController.navigate(R.id.action_signupFragment_to_profileFragment);
            }
        });

        viewModel.getErrorResponse().observe(this, new Observer<ErrorResponse>() {
            @Override
            public void onChanged(ErrorResponse errorResponse) {
                loadingDialog.dissmissDialog();
                Toast.makeText(getActivity(), errorResponse.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}