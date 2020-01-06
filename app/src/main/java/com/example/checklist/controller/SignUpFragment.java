package com.example.checklist.controller;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.checklist.R;
import com.example.checklist.model.Hash;
import com.example.checklist.model.User;
import com.example.checklist.repository.Repository;
import com.example.checklist.utils.PictureUtils;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    private static final String ARGS_USERNAME = "args_username";
    private static final String ARGS_PASSWORD = "args_password";
    private static final int PICK_IMAGE = 1;
    private User mUser = new User();
    private CircleImageView mProfileImage;
    private Repository mRepository;
    private EditText mUsername, mPassword, mConfirmPass;
    private TextInputLayout mFormUsername, mFormPassword, mFormConfirm;
    private Button mSignUp;
    private TextView mLogin;
    private File mPhotoFile;
    private CheckBox isAdmin;

    public static SignUpFragment newInstance(String username, String password) {

        Bundle args = new Bundle();

        SignUpFragment fragment = new SignUpFragment();
        args.putSerializable(ARGS_USERNAME, username);
        args.putSerializable(ARGS_PASSWORD, password);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUser.setUsername(getArguments().getString(ARGS_USERNAME));
        mUser.setPassword(getArguments().getString(ARGS_PASSWORD));


        mPhotoFile = Repository.getInstance(getContext()).getPhotoFile(mUser);
    }

    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        initUI(view);

        mSignUp.setOnClickListener(view1 -> {

            if (validateUsername())
                if (validatePassword())
                    if (confirmedPassword()) {
                        sendResult();
                    }

        });

        mProfileImage.setOnClickListener(v -> {

            if (mPhotoFile == null)
                return;

            Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

            Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");

            startActivityForResult(chooserIntent, PICK_IMAGE);
        });

        mLogin.setOnClickListener(view12 -> {

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().remove(SignUpFragment.this).commit();
        });


        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK || data == null)
            return;

        if (requestCode == PICK_IMAGE) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                mProfileImage.setImageBitmap(bitmap);


                try {
                    OutputStream stream = new FileOutputStream(mPhotoFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    stream.flush();
                    stream.close();


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


    }

    private void sendResult() {

        mUser.setUsername(mUsername.getText().toString());
        mUser.setPassword(Hash.MD5(mPassword.getText().toString()));
        mUser.setIsAdmin(isAdmin.isChecked());
        mRepository.insertUser(mUser);
        Intent intent = new Intent();
        getTargetFragment().onActivityResult(LoginFragment.REQUEST_CODE_SIGN_UP, Activity.RESULT_OK, intent);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(SignUpFragment.this).commit();
    }

    private void initUI(View view) {
        mUsername = view.findViewById(R.id.et_sign_up_username);
        mPassword = view.findViewById(R.id.et_sign_up_password);
        mConfirmPass = view.findViewById(R.id.et_confirm_password);
        mFormUsername = view.findViewById(R.id.form_sign_up_username);
        mFormPassword = view.findViewById(R.id.form_sign_up_password);
        mFormConfirm = view.findViewById(R.id.form_confirm_password);
        isAdmin = view.findViewById(R.id.checkbox_isAdmin);
        mLogin = view.findViewById(R.id.tv_go_to_login);
        mProfileImage = view.findViewById(R.id.profile_image_sign_up);
        mSignUp = view.findViewById(R.id.button_sign_up);
        mRepository = Repository.getInstance(getActivity().getApplicationContext());

        mUsername.setText(mUser.getUsername());
        mPassword.setText(mUser.getPassword());
        mConfirmPass.setText(mUser.getPassword());
    }

    private boolean validateUsername() {

        String username = mUsername.getText().toString().trim();
        if (username.isEmpty()) {
            mFormUsername.setError("please enter a username!");
            return false;

        } else if (username.length() > mFormUsername.getCounterMaxLength()) {
            mFormUsername.setError("Username cannot be more than " + mFormUsername.getCounterMaxLength() + " characters!");
            return false;

        } else if (mRepository.getUser(username) != null) {
            mFormUsername.setError("Username not available, Choose another!");
            return false;

        } else
            mFormUsername.setErrorEnabled(false);
        return true;
    }

    private boolean validatePassword() {

        String password = mPassword.getText().toString().trim();
        if (password.isEmpty()) {
            mFormPassword.setError("please enter your password!");
            return false;

        } else
            mFormPassword.setErrorEnabled(false);
        return true;

    }

    private boolean confirmedPassword() {

        String password = mConfirmPass.getText().toString().trim();
        if (password.isEmpty()) {
            mFormConfirm.setError("please confirm your password!");
            return false;

        } else if (!mPassword.getText().toString().equals(mConfirmPass.getText().toString())) {
            mFormConfirm.setError("Your passwords dose not Match");
            return false;

        } else
            mFormPassword.setErrorEnabled(false);
        return true;

    }


}
