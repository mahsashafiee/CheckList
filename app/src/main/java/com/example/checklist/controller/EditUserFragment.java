package com.example.checklist.controller;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.checklist.R;
import com.example.checklist.model.Hash;
import com.example.checklist.model.User;
import com.example.checklist.repository.Repository;
import com.example.checklist.utils.PictureUtils;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditUserFragment extends DialogFragment {

    public static final String TAG = "EditUserFragment";
    private static final String ARGS_USER_ID = "args_user_id";
    private static final String AUTHORITY_FILE_PROVIDER = "com.example.checklist.fileProvider";
    private static final int PICK_IMAGE = 3;

    public static EditUserFragment newInstance(Long userId) {

        Bundle args = new Bundle();
        args.putLong(ARGS_USER_ID, userId);

        EditUserFragment fragment = new EditUserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private User mUser;
    private CircleImageView mProfileImage;
    private Repository mRepository;
    private EditText mUsername, mPassword, mConfirmPass;
    private TextInputLayout mFormUsername, mFormPassword, mFormConfirm;
    private File mPhotoFile;
    private CheckBox mIsAdmin;
    private Bitmap mBitmap;
    private Uri mPhotoUri;


    public EditUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRepository = Repository.getInstance(getActivity());
        mUser = mRepository.getUser(getArguments().getLong(ARGS_USER_ID));
        mPhotoFile = Repository.getInstance(getContext()).getPhotoFile(mUser);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View root = LayoutInflater.from(getContext()).inflate(R.layout.fragment_edit_user, null, false);

        initUI(root);


        mProfileImage.setOnClickListener(v -> {

            if (mPhotoFile == null)
                return;

            mPhotoUri = FileProvider.getUriForFile(getContext(),
                    AUTHORITY_FILE_PROVIDER,
                    mPhotoFile);

            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);

            List<ResolveInfo> cameraActivities = getActivity().getPackageManager()
                    .queryIntentActivities(captureIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : cameraActivities) {
                getActivity().grantUriPermission(resolveInfo.activityInfo.packageName,
                        mPhotoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }

            Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

            Intent chooserIntent = Intent.createChooser(captureIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

            startActivityForResult(chooserIntent, PICK_IMAGE);
        });

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setPositiveButton(R.string.button_edit, null).setNegativeButton(android.R.string.cancel, null)
                .setView(root)
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnShowListener(dialog -> {
            Button buttonP = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            Button buttonN = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
            buttonP.setOnClickListener(v -> {
                if (validateUsername())
                    if (validatePassword())
                        if (confirmedPassword()) {
                            updateUser();
                            updateUI();
                            dismiss();
                        }
            });

            buttonN.setOnClickListener(v -> dismiss());
        });

        return alertDialog;
    }

    private void updateUser() {

        mUser.setUsername(mUsername.getText().toString());
        mUser.setPassword(Hash.MD5(mPassword.getText().toString()));
        mUser.setIsAdmin(mIsAdmin.isChecked());
        mRepository.updateUser(mUser);
        saveProfileImage(mBitmap);

    }

    private void saveProfileImage(Bitmap bitmap) {
        try {

            if (mBitmap == null || mPhotoFile == null)
                return;

            FileOutputStream stream = new FileOutputStream(mPhotoFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);

            stream.flush();
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initUI(View root) {
        mUsername = root.findViewById(R.id.et_edit_username);
        mPassword = root.findViewById(R.id.et_edit_password);
        mConfirmPass = root.findViewById(R.id.et_confirm_password);
        mFormUsername = root.findViewById(R.id.form_edit_username);
        mFormPassword = root.findViewById(R.id.form_edit_password);
        mFormConfirm = root.findViewById(R.id.form_confirm_password);
        mIsAdmin = root.findViewById(R.id.checkbox_isAdmin);
        mProfileImage = root.findViewById(R.id.profile_image_edit);

        mUsername.setText(mUser.getUsername());

        if (mPhotoFile == null)
            return;
        Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getAbsolutePath(), getActivity());
        if (bitmap != null)
            mProfileImage.setImageBitmap(bitmap);
    }

    private void updateUI() {
        if (getTargetFragment() instanceof UserListFragment)
            ((UserListFragment) getTargetFragment()).updateUI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK || data == null)
            return;

        if (requestCode == PICK_IMAGE) {
            if (data.getData() != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                if (selectedImage != null) {
                    Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();
                        mBitmap = BitmapFactory.decodeFile(picturePath);
                        mProfileImage.setImageBitmap(mBitmap);
                    }
                }
            } else {

                updatePhotoView();

                getActivity().revokeUriPermission(
                        mPhotoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            }
        }
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mProfileImage.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getAbsolutePath(), getActivity());
            mProfileImage.setImageBitmap(bitmap);
        }
    }

    private boolean validateUsername() {

        String username = mUsername.getText().toString().trim();
        if (username.isEmpty()) {
            mFormUsername.setError("please enter a username!");
            return false;

        } else if (username.length() > mFormUsername.getCounterMaxLength()) {
            mFormUsername.setError("Username cannot be more than " + mFormUsername.getCounterMaxLength() + " characters!");
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
