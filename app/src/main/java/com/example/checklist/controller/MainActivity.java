package com.example.checklist.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.example.checklist.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment toDoFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (toDoFragment == null){
            fragmentManager.beginTransaction().
                    add(R.id.fragment_container, LoginFragment.newInstance(/*new User()*/)).commit();
        }
    }
}
