package com.example.checklist.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checklist.R;
import com.example.checklist.controller.EditTaskFragment;
import com.example.checklist.controller.TaskListFragment;
import com.example.checklist.repository.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> implements Filterable {
    private static final String TAG_EDIT_TASK = "editTaskFragment";
    private static final int REQUEST_CODE_EDIT_TASK = 2;
    private List<Task> mTasks;
    private List<Task> mTasksFull;
    private Context mContext;
    private TaskListFragment mParentFragment;

    public TaskAdapter(Context context, TaskListFragment fragment, List<Task> tasks) {
        mContext = context;
        mParentFragment = fragment;
        mTasks = tasks;
        if (tasks != null) {
            mTasksFull = new ArrayList<>(tasks);
        }

    }

    public void setTasks(List<Task> tasks) {
        mTasks = tasks;
        if (tasks != null)
            mTasksFull = new ArrayList<>(tasks);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(mTasks.get(position));
    }


    @Override
    public int getItemCount() {
        if (mTasks != null)
            return mTasks.size();
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                List<Task> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(mTasksFull);

                } else {
                    String filter = constraint.toString().toLowerCase().trim();
                    for (Task task : mTasksFull) {
                        if (task.getTitle().toLowerCase().contains(filter) ||
                                task.getDescription().toLowerCase().contains(filter) ||
                                task.getSimpleDate().toLowerCase().contains(filter) ||
                                task.getSimpleTime().toLowerCase().contains(filter)) {
                            filteredList.add(task);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (mTasks != null)
                    mTasks.clear();
                else
                    mTasks = new ArrayList<>();

                if (results.values != null)
                    mTasks.addAll((Collection<? extends Task>) results.values);
                notifyDataSetChanged();
            }
        };
    }


    public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTvTaskTitle, mTvTaskDescription, mTvTaskDate;
        private CardView mCardView;
        private Task mTask;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            mTvTaskTitle = itemView.findViewById(R.id.item_task_title);
            mCardView = itemView.findViewById(R.id.item_card_view);
            mTvTaskDate = itemView.findViewById(R.id.item_task_date);
            ImageView shareTask = itemView.findViewById(R.id.item_task_share);
            mTvTaskDescription = itemView.findViewById(R.id.item_task_description);
            ImageView deleteTask = itemView.findViewById(R.id.item_task_delete);

            itemView.setOnClickListener(this);

            deleteTask.setOnClickListener(v -> {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                builder1.setMessage("Are you sure you want to delete " + mTask.getTitle() + "?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        (dialog, id) -> {
                            Repository.getInstance(mContext.getApplicationContext()).deleteTask(mTask.getUUID());
                            mParentFragment.onTaskUpdate(mTask.getUser_id(), mTask.getState());
                            dialog.cancel();
                        });

                builder1.setNegativeButton(
                        "No",
                        (dialog, id) -> dialog.cancel());

                AlertDialog alert11 = builder1.create();
                alert11.show();
            });

            shareTask.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, mTask.getTitle());
                intent.putExtra(Intent.EXTRA_TEXT, mTask.getDescription());
                intent = Intent.createChooser(intent, mContext.getResources().getString(R.string.send_task));
                mContext.startActivity(intent);
            });

        }

        @SuppressLint("SetTextI18n")
        void bind(final Task task) {

            mTask = task;

            mTvTaskTitle.setText(task.getTitle());
            mTvTaskDescription.setText(task.getDescription());
            mTvTaskDate.setText(task.getSimpleDate() + "  " + task.getSimpleTime());

            if (task.getState().equals(State.DOING)) {
                mCardView.setBackgroundResource(R.drawable.task_icon_doing);
                mTvTaskTitle.setPaintFlags(mTvTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

            } else if (task.getState().equals(State.DONE)) {
                mCardView.setBackgroundResource(R.drawable.task_icon_done);
                mTvTaskTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

            } else {
                mCardView.setBackgroundResource(R.drawable.task_icon_to_do);
                mTvTaskTitle.setPaintFlags(mTvTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

        }

        @Override
        public void onClick(View v) {
            EditTaskFragment editTaskFragment = EditTaskFragment.newInstance(mTask.getUUID());
            editTaskFragment.setTargetFragment(mParentFragment, REQUEST_CODE_EDIT_TASK);
            editTaskFragment.show(mParentFragment.getFragmentManager(), TAG_EDIT_TASK);
        }
    }

}
