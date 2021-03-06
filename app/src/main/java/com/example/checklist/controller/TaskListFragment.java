package com.example.checklist.controller;


import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.checklist.R;
import com.example.checklist.model.State;
import com.example.checklist.model.Task;
import com.example.checklist.controller.adapter.TaskAdapter;
import com.example.checklist.repository.Repository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskListFragment extends Fragment implements TaskAdapter.TaskUpdateListener {

    private static final int REQUEST_CODE_TASK_DETAIL = 0;
    private static final String TAG_TASK_DETAIL = "taskDetail";
    private FloatingActionButton mAddTask;
    private RecyclerView mRecyclerView;
    private TaskAdapter mAdapter;
    private List<Task> mTasks = new ArrayList<>();
    private Long mID;
    private State mState;
    private TextView mTaskNotFound;
    private Repository mRepository;
    private LinearLayout mBackground;

    private static final String TAG = "TaskListFragment";

    private static final String ARGS_USER_ID_FROM_LOGIN = "args_user_uuid_from_login";
    private static final String ARGS_STATE_FROM_VIEW_PAGER = "args_state_from_view_pager";

    public static TaskListFragment newInstance(Long id, State state) {

        Bundle args = new Bundle();

        TaskListFragment fragment = new TaskListFragment();
        args.putSerializable(ARGS_USER_ID_FROM_LOGIN, id);
        args.putSerializable(ARGS_STATE_FROM_VIEW_PAGER, state);
        fragment.setArguments(args);
        return fragment;
    }


    public TaskListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);


        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.actionBar_title);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setElevation(0);

        setHasOptionsMenu(true);
        mID = ((Long) getArguments().getSerializable(ARGS_USER_ID_FROM_LOGIN));
        mState = (State) getArguments().getSerializable(ARGS_STATE_FROM_VIEW_PAGER);
        mRepository = Repository.getInstance(getActivity().getApplicationContext());

        mTasks = mRepository.getTasks(mID, mState);


        Log.d(TAG, "onCreate " + mState.toString() + ": called");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        initRecyclerView(view);

        setBackgroundVisible();

        mAddTask.setOnClickListener(view1 -> {

            TaskDetailFragment taskDetailFragment = TaskDetailFragment.newInstance(mID, mState);
            taskDetailFragment.setTargetFragment(TaskListFragment.this, REQUEST_CODE_TASK_DETAIL);
            taskDetailFragment.show(getFragmentManager(), TAG_TASK_DETAIL);
        });

        Log.d(TAG, "onCreateView: " + mState.toString() + " called!");
        return view;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.task_list_menu_option, menu);

        MenuItem searchItem = menu.findItem(R.id.item_menu_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sub_item_menu_log_out:
                getActivity().startActivity(MainActivity.newIntent(getActivity()));
                return true;
            case R.id.item_menu_remove_all: {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                builder1.setMessage("Are you sure you want to delete all of your tasks?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        (dialog, id) -> {
                            mRepository.deleteTasks(mID);
                            onTaskUpdate(mID, mState);
                            dialog.cancel();
                        });

                builder1.setNegativeButton(
                        "No",
                        (dialog, id) -> dialog.cancel());

                AlertDialog alert11 = builder1.create();
                alert11.show();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTaskUpdate(Long id, State state) {
        mTasks = Repository.getInstance(getContext()).getTasks(id, state);
        if (isAdded()) {
            if (mAdapter != null) {
                mAdapter.setTasks(mTasks);
                mAdapter.notifyDataSetChanged();
            } else {
                mAdapter = new TaskAdapter(getActivity(), this, mTasks);
                mRecyclerView.setAdapter(mAdapter);
            }

            setBackgroundVisible();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @SuppressLint("SetTextI18n")
    public void setBackgroundVisible() {

        if (mTasks == null || mTasks.size() == 0) {
            mTaskNotFound.setText("No " + mState.toString() + " Task Found!!");
            mBackground.setVisibility(View.VISIBLE);
        } else {
            mBackground.setVisibility(View.GONE);
        }

    }

    private void initRecyclerView(View view) {
        mBackground = view.findViewById(R.id.task_not_found);
        mTaskNotFound = view.findViewById(R.id.tv_task_not_found);
        mAddTask = view.findViewById(R.id.addTask);
        mRecyclerView = view.findViewById(R.id.task_list_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new TaskAdapter(getActivity(), TaskListFragment.this, mTasks);
        mRecyclerView.setAdapter(mAdapter);
    }

}
