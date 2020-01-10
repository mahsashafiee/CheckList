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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.checklist.R;
import com.example.checklist.model.State;
import com.example.checklist.model.Task;
import com.example.checklist.repository.Repository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditTaskFragment extends DialogFragment {

    private static final String ARGS_TASK_UUID_FROM_LIST = "args_task_uuid_from_list";
    private static final int REQUEST_CODE_DATE_PICKER = 0;
    private static final int REQUEST_CODE_TIME_PICKER = 1;
    private static final String TAG_DATE_PICKER = "DatePicker";
    private static final String TAG_TIME_PICKER = "TimePicker";
    private static final String INSTANCE_TASK_TIME = "instance_task_time";
    private static final String INSTANCE_TASK_DATE = "instance_task_date";

    private Task mTask;
    private Date mDate = new Date();
    private Date mTime = new Date();
    private EditText mTaskTitle, mTaskDescription;
    private Button mButtonDate, mButtonTime;
    private Repository mRepository;
    private RadioButton mStateDone, mStateDoing, mStateToDo;
    private RadioGroup radioGroup;


    public static EditTaskFragment newInstance(UUID taskId) {

        Bundle args = new Bundle();

        EditTaskFragment fragment = new EditTaskFragment();
        args.putSerializable(ARGS_TASK_UUID_FROM_LIST, taskId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID taskId = ((UUID) getArguments().getSerializable(ARGS_TASK_UUID_FROM_LIST));

        mRepository = Repository.getInstance(getActivity().getApplicationContext());
        mTask = mRepository.getTask(taskId);
        mDate = mTask.getDate();
        mTime = mTask.getDate();

        if (savedInstanceState != null) {
            mDate = (Date) savedInstanceState.getSerializable(INSTANCE_TASK_DATE);
            mTime = (Date) savedInstanceState.getSerializable(INSTANCE_TASK_TIME);
        }

    }

    public EditTaskFragment() {
        // Required empty public constructor
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.fragment_edit_task, null, false);

        initUI(view);

        initListeners();

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setPositiveButton(R.string.button_edit, null).setNegativeButton(android.R.string.cancel, null)
                .setView(view)
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnShowListener(dialog -> {
            Button buttonP = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            Button buttonN = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
            buttonP.setOnClickListener(v -> {
                if (TaskValidation()) dismiss();
            });

            buttonN.setOnClickListener(v -> dismiss());
        });

        return alertDialog;

    }

    private void initListeners() {
        mButtonDate.setOnClickListener(view -> {
            DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(mTask.getDate());
            datePickerFragment.setTargetFragment(EditTaskFragment.this, REQUEST_CODE_DATE_PICKER);
            datePickerFragment.show(getFragmentManager(), TAG_DATE_PICKER);

        });

        mButtonTime.setOnClickListener(view -> {
            TimePickerFragment timePickerFragment = TimePickerFragment.newInstance(mDate);
            timePickerFragment.setTargetFragment(EditTaskFragment.this, REQUEST_CODE_TIME_PICKER);
            timePickerFragment.show(getFragmentManager(), TAG_TIME_PICKER);
        });
    }

    private boolean TaskValidation() {


        if (!mTaskTitle.getText().toString().isEmpty() && !mTaskDescription.getText().toString().isEmpty()) {
            mTask.setDescription(mTaskDescription.getText().toString());
            mTask.setTitle(mTaskTitle.getText().toString());
            stateChangeListener();
            mTask.setDate(mDate);
            mRepository.updateTask(mTask);
            updateUI();
            return true;
        } else if (mTaskTitle.getText().toString().isEmpty() && !(mTaskDescription.getText().toString().isEmpty())) {
            if (mTaskDescription.getText().toString().length() > 12)
                mTask.setTitle(mTaskDescription.getText().toString().substring(0, 12));
            else mTask.setTitle(mTaskDescription.getText().toString());
            mTask.setDescription(mTaskDescription.getText().toString());
            stateChangeListener();
            mTask.setDate(mDate);
            mRepository.updateTask(mTask);
            updateUI();
            return true;
        } else if (mTaskTitle.getText().toString().length() > 12) {
            mTaskTitle.setError("Title cannot be more than 12");
        } else
            mTaskDescription.setError("Description cannot be empty!!");
        return false;

    }

    private void stateChangeListener() {
        int checkedId = radioGroup.getCheckedRadioButtonId();
        if (checkedId == mStateDone.getId()) {
            mStateDone.setChecked(true);
            mTask.setState(State.DONE);
        } else if (checkedId == mStateDoing.getId()) {
            mStateDoing.setChecked(true);
            mTask.setState(State.DOING);
        } else {
            mStateToDo.setChecked(true);
            mTask.setState(State.TODO);
        }
    }

    private void updateUI() {
        if (getTargetFragment() instanceof TaskListFragment)
            ((TaskListFragment) getTargetFragment()).updateUI();
    }

    private void initUI(View view) {
        radioGroup = view.findViewById(R.id.radioGroup_taskState);
        mTaskDescription = view.findViewById(R.id.form_taskDescription);
        mTaskTitle = view.findViewById(R.id.form_Title);
        mButtonDate = view.findViewById(R.id.edit_task_button_date);
        mButtonTime = view.findViewById(R.id.edit_task_button_time);
        mStateDone = view.findViewById(R.id.radioButton_done);
        mStateDoing = view.findViewById(R.id.radioButton_doing);
        mStateToDo = view.findViewById(R.id.radioButton_todo);


        DateFormat format = new SimpleDateFormat("dd MMM yyyy");
        mButtonDate.setText(format.format(mTask.getDate()));

        format = new SimpleDateFormat("hh:mm a");
        mButtonTime.setText(format.format(mTask.getDate()));

        mTaskTitle.setText(mTask.getTitle());
        mTaskDescription.setText(mTask.getDescription());
        mStateDoing.setChecked(mTask.getState() == State.DOING);
        mStateDone.setChecked(mTask.getState() == State.DONE);
        mStateToDo.setChecked(mTask.getState() == State.TODO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK || data == null)
            return;

        if (requestCode == REQUEST_CODE_DATE_PICKER) {
            mDate = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_TASK_DATE);
            setupTaskDate(mDate, mTime);

        }
        if (requestCode == REQUEST_CODE_TIME_PICKER) {
            mTime = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TASK_TIME);
            setupTaskTime(mTime);

        }
    }

    private void setupTaskDate(Date date, Date tempTime) {
        date.setHours(tempTime.getHours());
        date.setMinutes(tempTime.getMinutes());

        mTask.setDate(date);
        DateFormat df = new SimpleDateFormat("dd MMM yyyy");
        mButtonDate.setText(df.format(mTask.getDate()));
    }

    private void setupTaskTime(Date tempTime) {
        mDate.setHours(tempTime.getHours());
        mDate.setMinutes(tempTime.getMinutes());

        mTask.setDate(mDate);

        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        mButtonTime.setText(dateFormat.format(mTask.getDate()));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(INSTANCE_TASK_TIME, mTime);
        outState.putSerializable(INSTANCE_TASK_DATE, mDate);
    }
}
