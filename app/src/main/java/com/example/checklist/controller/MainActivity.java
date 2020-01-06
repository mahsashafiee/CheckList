package com.example.checklist.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.checklist.R;

public class MainActivity extends AppCompatActivity {

    public static Intent newIntent(Context context) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment toDoFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (toDoFragment == null) {
            fragmentManager.beginTransaction().
                    add(R.id.fragment_container, LoginFragment.newInstance()).commit();
        }
    }
}
