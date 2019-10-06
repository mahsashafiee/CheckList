package com.example.checklist.model;

import android.content.Context;
import android.content.DialogInterface;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checklist.R;
import com.example.checklist.controller.EditTaskFragment;
import com.example.checklist.controller.TaskListFragment;
import com.example.checklist.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.TaskViewHolder> implements Filterable {

    private static final String TAG_EDIT_TASK = "editTaskFragment";
    private static final int REQUEST_CODE_EDIT_TASK = 2;
    private List<Task> mTasks;
    private List<Task> mTasksClone;
    private Context mContext;
    private State mState;
    private UUID mUserId;
    private TaskListFragment mParentFragment;

    public TaskRecyclerViewAdapter(Context context, TaskListFragment fragment, List<Task> tasks, UUID userId) {
        mContext = context;
        mParentFragment = fragment;
        mTasks = tasks;
        this.mUserId = userId;
        if (tasks != null) {
            mTasksClone = new ArrayList<>(tasks);
            mState = tasks.get(0).getState();
        }

    }

    public void setTasks(List<Task> tasks) {
        mTasks = tasks;
    }

    public boolean isAdmin() {
        User user = Repository.getInstance(mContext.getApplicationContext()).getUser(mUserId);
        if (user.getUsername().equals("admin") && user.getPassword().equals(Hash.MD5("123456")))
            return true;
        return false;
    }


    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, final int position) {

        holder.bind(mTasks.get(position), position);


        holder.mIvTaskDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                builder1.setMessage("Are you sure you want to delete " + mTasks.get(position).getTitle() + "?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Repository.getInstance(mContext.getApplicationContext()).deleteTask(mTasks.get(position).getID());
                                mParentFragment.updateUI();
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        });
        holder.mIvTaskEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditTaskFragment editTaskFragment = EditTaskFragment.newInstance(mTasks.get(position).getID());
                editTaskFragment.setTargetFragment(mParentFragment, REQUEST_CODE_EDIT_TASK);
                editTaskFragment.show(mParentFragment.getFragmentManager(), TAG_EDIT_TASK);
            }
        });


    }

    @Override
    public int getItemCount() {
        if (mTasks != null)
            return mTasks.size();
        return 0;
    }

    Filter taskFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<Task> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {

                if (isAdmin())
                    mTasksClone = Repository.getInstance(mContext.getApplicationContext()).getTasks(mState);
                else
                    mTasksClone = Repository.getInstance(mContext.getApplicationContext()).getTasks(mUserId, mState);

                filteredList.addAll(mTasksClone);
            } else {
                String filter = constraint.toString().toLowerCase().trim();
                for (Task task : mTasksClone) {
                    if (task.getTitle().toLowerCase().contains(filter)) {
                        filteredList.add(task);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (mTasks != null) {
                mTasks.clear();

                mTasks.addAll((List) results.values);

            }
            notifyDataSetChanged();
        }
    };

    @Override
    public Filter getFilter() {
        return taskFilter;
    }


    public class TaskViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvTaskTitle, mTvTaskDescription, mTvTaskDate, mTvIcon, mTvTaskUsername;
        private ImageView mIvTaskDelete, mIvTaskEdit;
        private CardView mCardView;
        private ConstraintLayout mParentLayout;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            mTvTaskTitle = itemView.findViewById(R.id.item_task_title);
            mTvTaskUsername = itemView.findViewById(R.id.item_task_username);
            mCardView = itemView.findViewById(R.id.item_card_view);
            mTvTaskDate = itemView.findViewById(R.id.item_task_date);
            mTvIcon = itemView.findViewById(R.id.item_task_icon);
            mTvTaskDescription = itemView.findViewById(R.id.item_task_description);
            mIvTaskEdit = itemView.findViewById(R.id.item_task_edit);
            mIvTaskDelete = itemView.findViewById(R.id.item_task_delete);
            mParentLayout = itemView.findViewById(R.id.item_parent_layout);

        }

        public void bind(Task task, int position) {

            mTvTaskTitle.setText(task.getTitle());
            mTvTaskDescription.setText(task.getDescription());
            mTvTaskDate.setText(task.getSimpleDate() + "  " + task.getSimpleTime());
            UUID userID = Repository.getInstance(mContext.getApplicationContext()).getUsername(task.getID());
            if (isAdmin())
                mTvTaskUsername.setText("(" + Repository.getInstance(mContext.getApplicationContext()).getUser(userID).getUsername() + ")");

            if (!task.getTitle().isEmpty())
                mTvIcon.setText(task.getTitle().charAt(0) + "");

            if (position % 2 != 0) {
                if (task.getState().equals(State.DOING)) {
                    mCardView.setBackgroundResource(R.drawable.background_odd_item);
                    mTvTaskTitle.setPaintFlags(mTvTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    mTvIcon.setBackgroundResource(R.drawable.task_icon_doing);
                } else if (mTasks.get(position).getState().equals(State.DONE)) {
                    mCardView.setBackgroundResource(R.drawable.background_odd_item);
                    mTvTaskTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    mTvIcon.setBackgroundResource(R.drawable.task_icon_done);

                } else {
                    mCardView.setBackgroundResource(R.drawable.background_odd_item);
                    mTvTaskTitle.setPaintFlags(mTvTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    mTvIcon.setBackgroundResource(R.drawable.task_icon_to_do);
                }

            } else {
                if (task.getState().equals(State.DOING)) {
                    mCardView.setBackgroundResource(R.drawable.background_even_item);
                    mTvTaskTitle.setPaintFlags(mTvTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    mTvIcon.setBackgroundResource(R.drawable.task_icon_doing);
                } else if (mTasks.get(position).getState().equals(State.DONE)) {
                    mCardView.setBackgroundResource(R.drawable.background_even_item);
                    mTvTaskTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    mTvIcon.setBackgroundResource(R.drawable.task_icon_done);

                } else {
                    mCardView.setBackgroundResource(R.drawable.background_even_item);
                    mTvTaskTitle.setPaintFlags(mTvTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    mTvIcon.setBackgroundResource(R.drawable.task_icon_to_do);
                }
            }
        }
    }

}
