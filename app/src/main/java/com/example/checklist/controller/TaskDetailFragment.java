package com.example.checklist.controller;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.checklist.R;
import com.example.checklist.model.State;
import com.example.checklist.model.Task;
import com.example.checklist.model.User;
import com.example.checklist.repository.Repository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskDetailFragment extends DialogFragment {

    public static final String ARGS_TASK_UUID_FROM_LIST = "args_task_uuid_from_list";
    public static final String ARGS_TASK_STATE_FROM_LIST = "args_task_state_from_list";
    public static final String ARGS_USER_UUID_FROM_LIST = "args_user_uuid_from_list";
    public static final int REQUEST_CODE_DATE_PICKER = 0;
    public static final int REQUEST_CODE_TIME_PICKER = 1;
    public static final String TAG_DATE_PICKER = "DatePicker";
    public static final String TAG_TIME_PICKER = "TimePicker";

    private Task mTask;
    private Date mDate;
    private CheckBox mTaskState;
    private EditText mTaskTitle, mTaskDescription;
    private Button mButtonDate, mButtonTime;
    private Repository mRepository;
    private UUID mUserId;


    public static TaskDetailFragment newInstance(UUID userId/*, UUID taskId*/, State taskState) {

        Bundle args = new Bundle();

        TaskDetailFragment fragment = new TaskDetailFragment();
        args.putSerializable(ARGS_USER_UUID_FROM_LIST, userId);
        args.putSerializable(ARGS_TASK_STATE_FROM_LIST, taskState);
        /*args.putSerializable(ARGS_TASK_UUID_FROM_LIST, taskId);*/
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserId = ((UUID) getArguments().getSerializable(ARGS_USER_UUID_FROM_LIST));

        /*UUID taskId = ((UUID) getArguments().getSerializable(ARGS_TASK_UUID_FROM_LIST));*/
        /*mTask = mRepository.getSingleTask(mUserId, taskId);*/

        mRepository = Repository.getInstance(new User());
        mTask = new Task();
        mTask.setState(((State) getArguments().getSerializable(ARGS_TASK_STATE_FROM_LIST)));
        mDate = mTask.getDate();
    }

    public TaskDetailFragment() {
        // Required empty public constructor
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.fragment_task_detail, null, false);

        initUI(view);

        initListeners();

        return new AlertDialog.Builder(getActivity())
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TaskValidation();
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .setView(view)
                .create();

    }

    private void initListeners() {
        mButtonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(mTask.getDate());
                datePickerFragment.setTargetFragment(TaskDetailFragment.this, REQUEST_CODE_DATE_PICKER);
                datePickerFragment.show(getFragmentManager(), TAG_DATE_PICKER);

            }
        });

        mButtonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerFragment timePickerFragment = TimePickerFragment.newInstance(mDate);
                timePickerFragment.setTargetFragment(TaskDetailFragment.this, REQUEST_CODE_TIME_PICKER);
                timePickerFragment.show(getFragmentManager(), TAG_TIME_PICKER);
            }
        });
    }

    private void TaskValidation() {

        if (!mTaskTitle.getText().toString().isEmpty() && !mTaskDescription.getText().toString().isEmpty()) {
            mTask.setDescription(mTaskDescription.getText().toString());
            mTask.setTitle(mTaskTitle.getText().toString());
            mRepository.addTask(mUserId, mTask);
            updateUI();
            return;
        } else if (mTaskTitle.getText().toString().isEmpty() && !(mTaskDescription.getText().toString().isEmpty())) {
            if (mTaskDescription.getText().toString().contains(" "))
                mTask.setTitle(mTaskDescription.getText().toString().substring(0, mTaskDescription.getText().toString().indexOf(' ')));
            mTask.setTitle(mTaskDescription.getText().toString());
            mTask.setDescription(mTaskDescription.getText().toString());
            mRepository.addTask(mUserId, mTask);
            updateUI();
            return;
        }
    }

    private void updateUI() {
        if (getTargetFragment() instanceof TaskListFragment)
            ((TaskListFragment) getTargetFragment()).updateUI();
    }

    private void initUI(View view) {
        mTaskDescription = view.findViewById(R.id.edittext_description);
        mTaskTitle = view.findViewById(R.id.edittext_title);
        mButtonDate = view.findViewById(R.id.button_date);
        mButtonTime = view.findViewById(R.id.button_time);
        mTaskState = view.findViewById(R.id.checkbox_state);

        DateFormat df = new SimpleDateFormat("dd MMM yyyy");
        mButtonDate.setText(df.format(mTask.getDate()));

        DateFormat tf = new SimpleDateFormat("hh:mm a");
        mButtonTime.setText(tf.format(mTask.getDate()));

        mTaskTitle.setText(mTask.getTitle());
        mTaskDescription.setText(mTask.getDescription());
        mTaskState.setText(mTask.getState().toString());
        mTaskState.setChecked(true);
        mTaskState.setEnabled(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK || data == null)
            return;

        if (requestCode == REQUEST_CODE_DATE_PICKER) {
            mDate = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_TASK_DATE);

            mTask.setDate(mDate);
            DateFormat df = new SimpleDateFormat("dd MMM yyyy");
            mButtonDate.setText(df.format(mTask.getDate()));

        }
        if (requestCode == REQUEST_CODE_TIME_PICKER) {
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TASK_TIME);

            mDate.setTime(date.getTime());
            mTask.setDate(mDate);

            DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            mButtonTime.setText(dateFormat.format(mTask.getDate()));

        }
    }


}
