package com.example.checklist.controller;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.checklist.R;
import com.example.checklist.model.Hash;
import com.example.checklist.model.User;
import com.example.checklist.repository.Repository;
import com.google.android.material.textfield.TextInputLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    public static final String ARGS_USERNAME = "args_username";
    public static final String ARGS_PASSWORD = "args_password";
    private User mUser = new User();
    private Repository mRepository;
    private EditText mUsername, mPassword, mConfirmPass;
    private TextInputLayout mFormUsername, mFormPassword, mFormConfirm;
    private Button mSignUp;
    private TextView mLogin;

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

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validateUsername())
                    if (validatePassword())
                        if (confirmedPassword()) {
                            sendResult();
                        }

            }

        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendResult();
            }
        });


        return view;
    }

    private void sendResult() {

        User user = new User();
        user.setUsername(mUsername.getText().toString());
        user.setPassword(Hash.MD5(mPassword.getText().toString()));
        mRepository.insertUser(user);
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
        mLogin = view.findViewById(R.id.tv_go_to_login);
        mSignUp = view.findViewById(R.id.button_sign_up);
        mRepository = Repository.getInstance(getActivity().getApplicationContext());

        mUsername.setText(mUser.getUsername());
        mPassword.setText(mUser.getPassword());
        mConfirmPass.setText(mUser.getPassword());
    }

    public boolean validateUsername() {

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

    public boolean validatePassword() {

        String password = mPassword.getText().toString().trim();
        if (password.isEmpty()) {
            mFormPassword.setError("please enter your password!");
            return false;

        } else
            mFormPassword.setErrorEnabled(false);
        return true;

    }

    public boolean confirmedPassword() {

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
