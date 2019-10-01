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

    public static final String ARGS_TASK_UUID_FROM_LIST = "args_task_uuid_from_list";
    public static final int REQUEST_CODE_DATE_PICKER = 0;
    public static final int REQUEST_CODE_TIME_PICKER = 1;
    public static final String TAG_DATE_PICKER = "DatePicker";
    public static final String TAG_TIME_PICKER = "TimePicker";

    private Task mTask;
    private Date mDate;
    private EditText mTaskTitle, mTaskDescription;
    private Button mButtonDate, mButtonTime;
    private Repository mRepository;
    private UUID mTaskId;
    private RadioButton mStateDone, mStateDoing, mStateToDo;
    private RadioGroup radioGroup;
    private DateFormat mFormat;


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

        mTaskId = ((UUID) getArguments().getSerializable(ARGS_TASK_UUID_FROM_LIST));

        mRepository = Repository.getInstance(getActivity().getApplicationContext());
        mTask = mRepository.getTask(mTaskId);
        mDate = mTask.getDate();

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

        return new AlertDialog.Builder(getActivity())
                .setPositiveButton(R.string.button_edit, new DialogInterface.OnClickListener() {
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
                datePickerFragment.setTargetFragment(EditTaskFragment.this, REQUEST_CODE_DATE_PICKER);
                datePickerFragment.show(getFragmentManager(), TAG_DATE_PICKER);

            }
        });

        mButtonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerFragment timePickerFragment = TimePickerFragment.newInstance(mDate);
                timePickerFragment.setTargetFragment(EditTaskFragment.this, REQUEST_CODE_TIME_PICKER);
                timePickerFragment.show(getFragmentManager(), TAG_TIME_PICKER);
            }
        });
    }

    private void TaskValidation() {


        if (!mTaskTitle.getText().toString().isEmpty() && !mTaskDescription.getText().toString().isEmpty()) {
            mTask.setDescription(mTaskDescription.getText().toString());
            mTask.setTitle(mTaskTitle.getText().toString());
            stateChangeListener();
            mRepository.updateTask(mTask);
            updateUI();
            return;
        } else if (mTaskTitle.getText().toString().isEmpty() && !(mTaskDescription.getText().toString().isEmpty())) {
            if (mTaskDescription.getText().toString().contains(" "))
                mTask.setTitle(mTaskDescription.getText().toString().substring(0, mTaskDescription.getText().toString().indexOf(' ')));
            mTask.setTitle(mTaskDescription.getText().toString());
            mTask.setDescription(mTaskDescription.getText().toString());
            stateChangeListener();
            mRepository.updateTask(mTask);
            updateUI();
            return;
        }


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


        mFormat = new SimpleDateFormat("dd MMM yyyy");
        mButtonDate.setText(mFormat.format(mTask.getDate()));

        mFormat = new SimpleDateFormat("hh:mm a");
        mButtonTime.setText(mFormat.format(mTask.getDate()));

        mTaskTitle.setText(mTask.getTitle());
        mTaskDescription.setText(mTask.getDescription());
        mStateDoing.setChecked(mTask.getState()==State.DOING);
        mStateDone.setChecked(mTask.getState()==State.DONE);
        mStateToDo.setChecked(mTask.getState()==State.TODO);
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
