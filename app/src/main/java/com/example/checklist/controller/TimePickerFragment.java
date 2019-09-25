package com.example.checklist.controller;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import com.example.checklist.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimePickerFragment extends DialogFragment {

    public static final String ARGS_TASK_TIME = "crimeTime";
    public static final String EXTRA_TASK_TIME = "crimeHour";

    TimePicker mTimePicker;
    Date TaskTime;

    public static TimePickerFragment newInstance(Date date) {

        Bundle args = new Bundle();
        args.putSerializable(ARGS_TASK_TIME, date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public TimePickerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TaskTime = (Date) getArguments().getSerializable(ARGS_TASK_TIME);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater
                .inflate(R.layout.fragment_time, null, false);

        mTimePicker = view.findViewById(R.id.time_picker);
        initTimePicker();

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendResult();
                    }
                })
                .setView(view)
                .create();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initTimePicker() {

        DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
        String strDate = dateFormat.format(TaskTime);

        String[] time = strDate.split(":");

        mTimePicker.setHour(Integer.parseInt(time[0]));
        mTimePicker.setMinute(Integer.parseInt(time[1]));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void sendResult() {

        TaskTime.setHours(mTimePicker.getHour());
        TaskTime.setMinutes(mTimePicker.getMinute());

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TASK_TIME, TaskTime);

        Fragment fragment = getTargetFragment();
        fragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
