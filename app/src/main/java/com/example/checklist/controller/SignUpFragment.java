package com.example.checklist.controller;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.checklist.R;
import com.example.checklist.model.User;
import com.google.android.material.textfield.TextInputLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    public static final String ARGS_SIGN_UP = "args_sign_up";
    public static final String EXTRA_USER_FROM_SIGN_UP = "extra_user_from_sign_up";
    public static final String EXTRA_IS_SIGNED_UP = "extra_is_signed_up";
    private User mUser;
    private EditText mUsername, mPassword, mConfirmPass;
    private TextInputLayout mFormUsername, mFormPassword, mFormConfirm;
    private Button mSignUp;
    private TextView mLogin;

    public static SignUpFragment newInstance(User user) {

        Bundle args = new Bundle();

        SignUpFragment fragment = new SignUpFragment();
        args.putSerializable(ARGS_SIGN_UP, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUser = (User) getArguments().getSerializable(ARGS_SIGN_UP);
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

                if (mUsername.getText().toString().isEmpty()) {
                    mFormUsername.setError("please enter your username!");
                    return;

                } else if (mPassword.getText().toString().isEmpty()){
                    mFormPassword.setError("please enter your password!");
                    return;

                }else if (!mPassword.getText().toString().equals(mConfirmPass.getText().toString())){
                    mFormConfirm.setError("Your passwords dose not Match");
                    return;

                } else {
                    sendResult(true);
                }
            }

        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendResult(false);
            }
        });


        return  view;
    }

    private void sendResult(boolean isSignedUp) {

        User user = new User(mUsername.getText().toString(), mPassword.getText().toString());
        Intent intent = new Intent();
        intent.putExtra(EXTRA_USER_FROM_SIGN_UP, user);
        intent.putExtra(EXTRA_IS_SIGNED_UP, isSignedUp);
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

        mUsername.setText(mUser.getUsername());
        mPassword.setText(mUser.getPassword());
        mConfirmPass.setText(mUser.getPassword());
    }

}
