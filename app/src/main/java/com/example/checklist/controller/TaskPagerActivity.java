package com.example.checklist.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.checklist.R;
import com.example.checklist.model.TaskPagerAdapter;
import com.example.checklist.model.User;
import com.google.android.material.tabs.TabLayout;

import java.util.UUID;

public class TaskPagerActivity extends AppCompatActivity {
    public static final String EXTRA_USER_UUID_FROM_LOGIN = "com.example.checklist.extra_user_from_login";
    private UUID mID;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TaskPagerAdapter mPagerAdapter;

    public static Intent newIntent(Context context, UUID userId) {
        Intent intent = new Intent(context, TaskPagerActivity.class);
        intent.putExtra(EXTRA_USER_UUID_FROM_LOGIN, userId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);


        mID = (UUID) getIntent().getSerializableExtra(EXTRA_USER_UUID_FROM_LOGIN);

        initUI();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                ((TaskListFragment) ((TaskPagerAdapter) mViewPager.getAdapter()).getItem(position)).updateUI();/*
                mPagerAdapter.notifyDataSetChanged();*/
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }

    public void initUI(){

        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);

        mPagerAdapter = new TaskPagerAdapter(getSupportFragmentManager(), this, mID);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(0);

        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_action_more);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_action_clock);
        mTabLayout.getTabAt(2).setIcon(R.drawable.ic_action_check);

    }
}
