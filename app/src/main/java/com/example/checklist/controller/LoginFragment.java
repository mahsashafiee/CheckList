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
import com.example.checklist.model.Hash;
import com.example.checklist.model.User;
import com.example.checklist.repository.Repository;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    static final int REQUEST_CODE_SIGN_UP = 0;
    private static final String TAG_SIGN_UP = "signUpFragment";
    private User mUser;
    private Repository mRepository;
    private EditText mUsername, mPassword;
    private TextInputLayout mFormUsername, mFormPassword;
    private Button mLogin;
    private TextView mCreateAccount;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {

        Bundle args = new Bundle();

        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        initUI(view);

        mCreateAccount.setOnClickListener(view12 -> {
            String username = mUsername.getText().toString();
            String password = mPassword.getText().toString();
            SignUpFragment signUpFragment = SignUpFragment.newInstance(username, password);
            signUpFragment.setTargetFragment(LoginFragment.this, REQUEST_CODE_SIGN_UP);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().add(R.id.fragment_container, signUpFragment, TAG_SIGN_UP).commit();
        });

        mLogin.setOnClickListener(view1 -> {
            if (validateUsername()) {
                if (validatePassword()) {
                    Intent intent = TaskPagerActivity.newIntent(getActivity(), mUser.get_id(), true);
                    startActivity(intent);
                    getActivity().finish();
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
        mRepository = Repository.getInstance(getActivity().getApplicationContext());


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == Activity.RESULT_CANCELED || data == null)
            return;

        if (requestCode == REQUEST_CODE_SIGN_UP) {

            User user = new User();

            mUsername.setText(user.getUsername());
            mPassword.setText(user.getPassword());

            mFormUsername.setErrorEnabled(false);
            mFormPassword.setErrorEnabled(false);
        }
    }

    private boolean validateUsername() {
        mFormUsername.setErrorEnabled(true);
        String username = mUsername.getText().toString().trim();
        if (username.isEmpty()) {
            mFormUsername.setError("please enter your username!");
            return false;

        } else if (mRepository.getUser(username) == null || username.length() > mFormUsername.getCounterMaxLength()) {
            mFormUsername.setError("Username is not valid!");
            return false;

        } else
            mFormUsername.setErrorEnabled(false);
        return true;
    }

    private Boolean validatePassword() {
        mFormPassword.setErrorEnabled(true);

        mUser = mRepository.getUser(mUsername.getText().toString().trim());
        String password = mPassword.getText().toString().trim();

        if (!password.isEmpty()) {
            password = Hash.MD5(mPassword.getText().toString());
        }

        if (password.isEmpty()) {
            mFormPassword.setError("please enter your password!");
            return false;

        } else if (!mUser.getPassword().equals(password)) {
            mFormPassword.setError("Password is not valid!");
            return false;

        } else if (mUser.getPassword().equals(password)) {
            mFormPassword.setErrorEnabled(false);
            return true;
        }
        return null;
    }
}
