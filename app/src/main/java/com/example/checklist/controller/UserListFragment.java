package com.example.checklist.controller;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.checklist.R;
import com.example.checklist.controller.adapter.UserAdapter;
import com.example.checklist.repository.Repository;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserListFragment extends Fragment {

    public static final String TAG = "UserListFragment";
    private RecyclerView mRecyclerView;
    private UserAdapter mAdapter;
    private Repository mRepository;


    public UserListFragment() {
        // Required empty public constructor
    }

    public static UserListFragment newInstance() {

        Bundle args = new Bundle();

        UserListFragment fragment = new UserListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        mRepository = Repository.getInstance(getActivity());

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        updateUI();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.user_list_menu_option, menu);
    }

    public void updateUI() {
        if (isAdded()) {
            if (mAdapter != null) {
                mAdapter.setUserList(mRepository.getUsers());
                mAdapter.notifyDataSetChanged();
            } else {
                mAdapter = new UserAdapter(getActivity(), this, mRepository.getUsers());
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_log_out: {
                startActivity(MainActivity.newIntent(getActivity()));
                getActivity().finish();
                return true;
            }
            case R.id.action_delete_all: {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                builder1.setMessage("Are you sure you want to delete all of your members?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        (dialog, id) -> {
                            mRepository.deleteAll();
                            mAdapter.setUserList(mRepository.getUsers());
                            mAdapter.notifyDataSetChanged();
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
}
