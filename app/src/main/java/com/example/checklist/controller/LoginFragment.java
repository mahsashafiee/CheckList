package com.example.checklist.controller;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    public static final String ARGS_LOGIN_USER = "args_login_user";
    public static final int REQUEST_CODE_SIGN_UP = 0;
    public static final String TAG_SIGN_UP = "signUpFragment";
    private User mUser;
    private EditText mUsername, mPassword;
    private TextInputLayout mFormUsername, mFormPassword;
    private Button mLogin;
    private TextView mCreateAccount;
    private boolean isSignedUp;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(/*User user*/) {

        Bundle args = new Bundle();

        LoginFragment fragment = new LoginFragment();
        /*args.putSerializable(ARGS_LOGIN_USER, user);*/
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        initUI(view);

        mCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = new User(mUsername.getText().toString(), mPassword.getText().toString());
                SignUpFragment signUpFragment = SignUpFragment.newInstance(user);
                signUpFragment.setTargetFragment(LoginFragment.this, REQUEST_CODE_SIGN_UP);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().add(R.id.fragment_container, signUpFragment, TAG_SIGN_UP).commit();
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                User user = new User(mUsername.getText().toString(), mPassword.getText().toString());

                if (mUser == null || !isSignedUp) {
                    Snackbar.make(getActivity().findViewById(R.id.fragment_container), "You should sing up first!", BaseTransientBottomBar.LENGTH_LONG).show();
                    return;

                } else if (mUsername.getText().toString().isEmpty()) {
                    mFormUsername.setError("please enter your username!");
                    return;

                } else if (mPassword.getText().toString().isEmpty()) {
                    mFormPassword.setError("please enter your password!");
                    return;

                } else if (!mUser.getPassword().equals(mPassword.getText().toString())) {
                    mFormPassword.setError("Password is wrong!");
                    return;

                } else if (!mUser.getUsername().equals(mUsername.getText().toString())) {
                    mFormUsername.setError("Username is wrong!");
                    return;
                }else if (mUser.equals(user)){
                    Intent intent = TaskPagerActivity.newIntent(getActivity(), mUser);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    private void initUI(View view) {
        mPassword = view.findViewById(R.id.et_login_password);
        mUsername = view.findViewById(R.id.et_login_username);
        mFormUsername = view.findViewById(R.id.form_login_username);
        mFormPassword = view.findViewById(R.id.form_login_password);
        mLogin = view.findViewById(R.id.button_login);
        mCreateAccount = view.findViewById(R.id.tv_go_to_sign_up);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_CANCELED || data == null)
            return;

        if (requestCode == REQUEST_CODE_SIGN_UP) {

            mUser = (User) data.getSerializableExtra(SignUpFragment.EXTRA_USER_FROM_SIGN_UP);

            isSignedUp = data.getBooleanExtra(SignUpFragment.EXTRA_IS_SIGNED_UP, false);

            mUsername.setText(mUser.getUsername());
            mPassword.setText(mUser.getPassword());

        }
    }
}
