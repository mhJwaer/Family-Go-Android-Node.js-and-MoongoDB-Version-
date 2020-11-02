package com.mh.jwaer.familygo.ui.main.hosts.settings.frags;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.Task;

import com.mh.jwaer.familygo.R;
import com.mh.jwaer.familygo.data.models.ErrorResponse;
import com.mh.jwaer.familygo.data.models.ResponseBody;
import com.mh.jwaer.familygo.data.network.NetworkUtil;
import com.mh.jwaer.familygo.data.network.RetrofitClient;
import com.mh.jwaer.familygo.databinding.FragmentProfileSettingBinding;
import com.mh.jwaer.familygo.ui.auth.AuthViewModel;
import com.mh.jwaer.familygo.ui.home.LoadingActivity;
import com.mh.jwaer.familygo.util.LoadingDialog;
import com.mh.jwaer.familygo.util.UserClient;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.mh.jwaer.familygo.util.CONSTANTS.NULL_PHOTO;

public class ProfileSettingFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 101;

    private FragmentProfileSettingBinding binding;
    private LoadingDialog loadingDialog;
    private AuthViewModel viewModel;
    private boolean isImageUpdated = false;


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_setting, container, false);
        loadingDialog = new LoadingDialog(getActivity());


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

        //setting info
        binding.usernameEditText.setText(UserClient.getUser().getName());
        if (!UserClient.getUser().getPhotoUrl().equals(NULL_PHOTO)) {
            String modelPhotoUrl =UserClient.getUser().getPhotoUrl().replace("\\", "/");
            String photoUrl = RetrofitClient.BASE_URL + modelPhotoUrl;
            Glide.with(this)
                    .load(photoUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .thumbnail(0.1f)
                    .centerInside()
                    .into(binding.profileImage);
        }


        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start image chooser
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        //setting some restrict
        binding.icTickMark.setVisibility(View.GONE);
        binding.usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.icTickMark.setVisibility(View.GONE);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.icTickMark.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (UserClient.getUser().getName().equals(s.toString())) {
                    binding.icTickMark.setVisibility(View.GONE);
                }
            }
        });

        binding.icTickMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!NetworkUtil.isNetworkAvailable()){
                    Toast.makeText(getActivity(), "No Internet Connection Available!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (binding.usernameEditText.getText().toString().length() < 3 ||
                        binding.usernameEditText.getText().toString().length() > 25) {
                    Toast.makeText(getActivity(), "Invalid User Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                loadingDialog.startAlertDialog();
                viewModel.updateUserNameInDB(binding.usernameEditText.getText().toString().trim());
            }
        });



    }

    @Override
    public void onResume() {
        super.onResume();
        //observe view model methods

        viewModel.getUploadImageRes().observe(this, new Observer<ResponseBody>() {
            @Override
            public void onChanged(ResponseBody responseBody) {
                loadingDialog.dissmissDialog();
                if (responseBody.isSuccessfull()){
                    Toast.makeText(getContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewModel.getUserNameRes().observe(this, new Observer<ResponseBody>() {
            @Override
            public void onChanged(ResponseBody responseBody) {
                loadingDialog.dissmissDialog();
                if (responseBody.isSuccessfull()){
                    Toast.makeText(getContext(), responseBody.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewModel.getErrorResponse().observe(this, new Observer<ErrorResponse>() {
            @Override
            public void onChanged(ErrorResponse errorResponse) {
                loadingDialog.dissmissDialog();
                Toast.makeText(getContext(), errorResponse.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (isImageUpdated){
            Intent intent = new Intent(getActivity(), LoadingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
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
            if (resultCode == Activity.RESULT_OK) {
                //call the view model and upload image
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
}