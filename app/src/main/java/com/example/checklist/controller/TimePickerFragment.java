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

import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimePickerFragment extends DialogFragment {

    public static final String ARGS_TASK_TIME = "crimeTime";
    public static final String EXTRA_TASK_TIME = "crimeHour";

    private TimePicker mTimePicker;
    private Date TaskTime;
    private Calendar mCalendar;

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
        mCalendar = Calendar.getInstance();
        mCalendar.setTime(TaskTime);

        if (mCalendar.get(Calendar.HOUR_OF_DAY) == 12)
            mCalendar.setTime(new Date());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater
                .inflate(R.layout.fragment_time_picker, null, false);

        mTimePicker = view.findViewById(R.id.time_picker);
        initTimePicker();

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> sendResult())
                .setView(view)
                .create();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initTimePicker() {


        if (Build.VERSION.SDK_INT < 23) {

            mTimePicker.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
            mTimePicker.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
        }else {

            mTimePicker.setHour(mCalendar.get(Calendar.HOUR_OF_DAY));
            mTimePicker.setMinute(mCalendar.get(Calendar.MINUTE));

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void sendResult() {

        Calendar calendar = Calendar.getInstance();
        int getHour;
        int getMinute;

        if (Build.VERSION.SDK_INT < 23) {
            getHour = mTimePicker.getCurrentHour();
            getMinute = mTimePicker.getCurrentMinute();

            calendar.set(Calendar.HOUR_OF_DAY, mTimePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, mTimePicker.getCurrentMinute());
        } else {
            getHour = mTimePicker.getHour();
            getMinute = mTimePicker.getMinute();

            calendar.set(Calendar.HOUR_OF_DAY, mTimePicker.getHour());
            calendar.set(Calendar.MINUTE, mTimePicker.getMinute());
        }

        TaskTime.setHours(getHour);
        TaskTime.setMinutes(getMinute);

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TASK_TIME, TaskTime);

        Fragment fragment = getTargetFragment();
        fragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}
