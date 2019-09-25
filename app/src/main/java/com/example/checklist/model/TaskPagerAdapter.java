package com.example.checklist.model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.checklist.R;
import com.example.checklist.controller.TaskListFragment;

import java.util.ArrayList;
import java.util.List;

public class TaskPagerAdapter extends FragmentStatePagerAdapter {

    private List<TaskListFragment> mListFragments = new ArrayList<>();
    private Context mContext;
    private User mUser;

    public TaskPagerAdapter(@NonNull FragmentManager fm, Context context, User user) {
        super(fm);
        mContext = context;
        mUser = user;
        mListFragments.add(TaskListFragment.newInstance(mUser, State.TODO));
        mListFragments.add(TaskListFragment.newInstance(mUser, State.DOING));
        mListFragments.add(TaskListFragment.newInstance(mUser, State.DONE));
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mListFragments.get(position);

    }


    @Override
    public int getCount() {
        return mListFragments.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        TaskListFragment fragment = ((TaskListFragment) object);
        fragment.updateUI();
        return super.getItemPosition(object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.category_TODO);
            case 1:
                return mContext.getString(R.string.category_DOING);
            case 2:
                return mContext.getString(R.string.category_DONE);
        }
        return null;
    }
}
