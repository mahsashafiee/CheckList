package com.example.checklist.controller.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.checklist.R;
import com.example.checklist.controller.TaskListFragment;
import com.example.checklist.controller.UserListFragment;
import com.example.checklist.model.State;
import com.example.checklist.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mListFragments = new ArrayList<>();
    private List<String> mListTitle = new ArrayList<>();
    private Context mContext;
    private Long mID;

    public TaskPagerAdapter(@NonNull FragmentManager fm, Context context, Long id) {
        super(fm);
        mContext = context;
        mID = id;
        mListFragments.add(TaskListFragment.newInstance(mID, State.TODO));
        mListFragments.add(TaskListFragment.newInstance(mID, State.DOING));
        mListFragments.add(TaskListFragment.newInstance(mID, State.DONE));

        mListTitle.add(mContext.getString(R.string.category_TODO));
        mListTitle.add(mContext.getString(R.string.category_DOING));
        mListTitle.add(mContext.getString(R.string.category_DONE));
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mListFragments.get(position);

    }
/*

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
*/

    @Override
    public int getCount() {
        return mListFragments.size();
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mListTitle.get(position);
    }
}
