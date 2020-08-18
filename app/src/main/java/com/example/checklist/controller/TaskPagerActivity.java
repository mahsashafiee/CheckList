package com.example.checklist.controller;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.checklist.R;
import com.example.checklist.controller.adapter.TaskPagerAdapter;
import com.example.checklist.model.State;
import com.example.checklist.repository.Repository;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import static com.example.checklist.model.State.DOING;
import static com.example.checklist.model.State.DONE;
import static com.example.checklist.model.State.TODO;

public class TaskPagerActivity extends AppCompatActivity {

    private static final String TAG = "TaskPagerActivity";
    public static final String EXTRA_USER_ID_FROM_LOGIN = "com.example.checklist.extra_user_from_login";
    public static final String EXTRA_IS_FROM_LOGIN = "extra_is_from_login";
    private Long mID;
    private ViewPager mViewPager;
    List<State> mTaskState = new ArrayList<State>() {{
        add(TODO);
        add(DOING);
        add(DONE);
    }};

    public static Intent newIntent(Context context, Long userId, boolean fromLogin) {
        Intent intent = new Intent(context, TaskPagerActivity.class);
        intent.putExtra(EXTRA_USER_ID_FROM_LOGIN, userId);
        intent.putExtra(EXTRA_IS_FROM_LOGIN, fromLogin);
        return intent;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setElevation(0);

        mID = (Long) getIntent().getSerializableExtra(EXTRA_USER_ID_FROM_LOGIN);
        boolean fromLogin = getIntent().getBooleanExtra(EXTRA_IS_FROM_LOGIN, false);
        Repository repository = Repository.getInstance(this);

        if (fromLogin && repository.isAdmin(mID)) {
            setContentView(R.layout.activity_user_list);
            startAdminFragment();
        } else {
            setContentView(R.layout.activity_view_pager);
            startUserFragment();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void startAdminFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragmentManager.beginTransaction().add(R.id.fragment_container, UserListFragment.newInstance()).commit();
        }
    }

    private void startUserFragment() {

        initUserUI();


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onPageSelected(int position) {

                if (mViewPager.getAdapter() instanceof TaskPagerAdapter) {
                    TaskPagerAdapter pagerAdapter = (TaskPagerAdapter) mViewPager.getAdapter();
                    if (pagerAdapter.getItem(position) instanceof TaskListFragment)
                        ((TaskListFragment) pagerAdapter.getItem(position)).onTaskUpdate(mID, mTaskState.get(position));

                    Log.d(TAG, "onPageSelected: ss::" + mTaskState.get(position));
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void initUserUI() {

        mViewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        TaskPagerAdapter pagerAdapter = new TaskPagerAdapter(getSupportFragmentManager(), this, mID);
        mViewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);

    }


}
