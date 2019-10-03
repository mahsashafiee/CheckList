package com.example.checklist.model;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checklist.R;
import com.example.checklist.controller.EditTaskFragment;
import com.example.checklist.controller.MainActivity;
import com.example.checklist.controller.TaskListFragment;
import com.example.checklist.repository.Repository;

import java.util.List;
import java.util.UUID;

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.TaskViewHolder> {

    private static final String TAG_EDIT_TASK = "editTaskFragment";
    private static final int REQUEST_CODE_EDIT_TASK = 2;
    private List<Task> mTasks;
    private Context mContext;
    private TaskListFragment mParentFragment;

    public TaskRecyclerViewAdapter(Context context, TaskListFragment fragment, List<Task> tasks) {
        mContext = context;
        mParentFragment = fragment;
        mTasks = tasks;
    }

    public void setTasks(List<Task> tasks) {
        mTasks = tasks;
    }

    @Override
    public int getItemViewType(int position) {

        if (position % 2 != 0) {
            if (mTasks.get(position).getState().equals(State.DOING))
                return 10;
            else if (mTasks.get(position).getState().equals(State.DONE))
                return 11;
            return 12;
        } else {
            if (mTasks.get(position).getState().equals(State.DOING))
                return 20;
            else if (mTasks.get(position).getState().equals(State.DONE))
                return 21;
            return 22;
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, final int position) {

        holder.mTvTaskTitle.setText(mTasks.get(position).getTitle());
        holder.mTvTaskDescription.setText(mTasks.get(position).getDescription());
        holder.mTvTaskDate.setText(mTasks.get(position).getSimpleDate() + "  " + mTasks.get(position).getSimpleTime());
        UUID userID = Repository.getInstance(mContext.getApplicationContext()).getUsername(mTasks.get(position).getID());
        holder.mTvTaskUsername.setText("("+Repository.getInstance(mContext.getApplicationContext()).getUser(userID).getUsername()+")");
        if (!mTasks.get(position).getTitle().isEmpty())
            holder.mTvIcon.setText(mTasks.get(position).getTitle().charAt(0) + "");


        switch (holder.getItemViewType()) {
            case 10: {
                holder.mCardView.setBackgroundResource(R.drawable.background_odd_item);
                holder.mTvTaskTitle.setPaintFlags(holder.mTvTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                holder.mTvIcon.setBackgroundResource(R.drawable.task_icon_doing);
                break;
            }
            case 11: {
                holder.mCardView.setBackgroundResource(R.drawable.background_odd_item);
                holder.mTvTaskTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.mTvIcon.setBackgroundResource(R.drawable.task_icon_done);
                break;
            }
            case 12: {
                holder.mCardView.setBackgroundResource(R.drawable.background_odd_item);
                holder.mTvTaskTitle.setPaintFlags(holder.mTvTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                holder.mTvIcon.setBackgroundResource(R.drawable.task_icon_to_do);
                break;
            }
            case 20: {
                holder.mCardView.setBackgroundResource(R.drawable.background_even_item);
                holder.mTvTaskTitle.setPaintFlags(holder.mTvTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                holder.mTvIcon.setBackgroundResource(R.drawable.task_icon_doing);
                break;
            }
            case 21: {
                holder.mCardView.setBackgroundResource(R.drawable.background_even_item);
                holder.mTvTaskTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.mTvIcon.setBackgroundResource(R.drawable.task_icon_done);
                break;
            }
            case 22: {
                holder.mCardView.setBackgroundResource(R.drawable.background_even_item);
                holder.mTvTaskTitle.setPaintFlags(holder.mTvTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                holder.mTvIcon.setBackgroundResource(R.drawable.task_icon_to_do);
                break;
            }
        }
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
    }
}
