package com.mh.jwaer.familygo.ui.auth;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.bumptech.glide.Glide;
import com.mh.jwaer.familygo.R;
import com.mh.jwaer.familygo.data.models.ErrorResponse;
import com.mh.jwaer.familygo.data.models.ResponseBody;
import com.mh.jwaer.familygo.databinding.FragmentProfileBinding;
import com.mh.jwaer.familygo.util.LoadingDialog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 101;

    private FragmentProfileBinding binding;
    private AuthViewModel viewModel;
    private LoadingDialog loadingDialog;
    private NavController navController;
    private CircleImageView profileImageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);

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
        profileImageView = binding.profileImage;
        final Button nextBtn = binding.profileNextBtn;
        final EditText usernameEd = binding.usernameED;

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        nextBtn.setEnabled(false);
        usernameEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count >= 3) {
                    nextBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() >= 3) {
                    nextBtn.setEnabled(true);
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.startAlertDialog();
                viewModel.updateUserNameInDB(usernameEd.getText().toString().trim());
            }

        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            lunchImageCropper(data.getData());
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            assert result != null;
            if (resultCode == RESULT_OK) {
//                upload the image
                Glide.with(this)
                        .load(result.getUri())
                        .thumbnail(0.1f)
                        .centerInside()
                        .into(profileImageView);
                loadingDialog.startAlertDialog();
                viewModel.uploadProfileImage(result.getUri());
            }

        }

    }
    private void lunchImageCropper(Uri uri) {
        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1920, 1920)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(requireActivity(), this);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.getUserNameRes().observe(this, new Observer<ResponseBody>() {
            @Override
            public void onChanged(ResponseBody responseBody) {
                loadingDialog.dissmissDialog();
                if (navController.getCurrentDestination().getId() == R.id.profileFragment)
                    navController.navigate(R.id.action_profileFragment_to_circleSetupFragment);
            }
        });

        viewModel.getUploadImageRes().observe(this, new Observer<ResponseBody>() {
            @Override
            public void onChanged(ResponseBody responseBody) {
                loadingDialog.dissmissDialog();
                if (responseBody.isSuccessfull()){
                    Toast.makeText(getContext(), "image updated", Toast.LENGTH_SHORT).show();
                }
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