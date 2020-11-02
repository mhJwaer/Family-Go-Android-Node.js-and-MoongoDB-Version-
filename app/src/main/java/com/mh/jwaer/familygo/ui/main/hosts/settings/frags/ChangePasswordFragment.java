package com.mh.jwaer.familygo.ui.main.hosts.settings.frags;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mh.jwaer.familygo.R;
import com.mh.jwaer.familygo.databinding.FragmentChangePasswordBinding;
import com.mh.jwaer.familygo.util.LoadingDialog;

public class ChangePasswordFragment extends Fragment {
    private String oldPass;
    private String newPass;
    private String confirmNewPass;
    private LoadingDialog loadingDialog;
    private FragmentChangePasswordBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_change_password, container, false);

//        loadingDialog = new LoadingDialog(getActivity());
//        user = FirebaseAuth.getInstance().getCurrentUser();

        return binding.getRoot();
    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        binding.changePassBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                oldPass = binding.oldPass.getText().toString();
//                newPass = binding.newPass.getText().toString();
//                confirmNewPass = binding.confirmNewPass.getText().toString();
//                if (oldPass.isEmpty()) {
//                    binding.oldPass.setError("Enter The Old Password!");
//                    binding.oldPass.requestFocus();
//                    return;
//                }
//                if (newPass.isEmpty()) {
//                    binding.newPass.setError("Enter the new password!");
//                    binding.newPass.requestFocus();
//                    return;
//                }
//                if (!newPass.equals(confirmNewPass)) {
//                    binding.confirmNewPass.setError("Passwords does not match!");
//                    binding.confirmNewPass.requestFocus();
//                    return;
//                }
//
//                //authenticate user to check if the old password true
//                if (user.getEmail() == null) return;
//                loadingDialog.startAlertDialog();
//                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);
//                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            //old pass correct
//                            changePassword(newPass);
//                        } else {
//                            loadingDialog.dissmissDialog();
//                            binding.oldPass.setError("Incorrect old password!");
//                        }
//                    }
//                });
//            }
//        });
//    }
//
//    private void changePassword(String newPass) {
//        user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()){
//                    loadingDialog.dissmissDialog();
//                    Toast.makeText(getActivity(), "Password Updated", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(getActivity(), LoadingActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                }
//                else {
//                    Toast.makeText(getActivity(), "Something went wrong!\nplease try again later"
//                            , Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
}
