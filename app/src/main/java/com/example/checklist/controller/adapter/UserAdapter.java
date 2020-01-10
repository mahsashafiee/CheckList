package com.example.checklist.controller.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checklist.R;
import com.example.checklist.controller.EditUserFragment;
import com.example.checklist.controller.TaskPagerActivity;
import com.example.checklist.controller.UserListFragment;
import com.example.checklist.model.User;
import com.example.checklist.repository.Repository;
import com.example.checklist.utils.PictureUtils;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {

    private static final int REQUEST_CODE_EDIT_USER = 2;

    private List<User> mUserList;
    private Activity mActivity;
    private UserListFragment mParentFragment;

    public UserAdapter(Activity activity, UserListFragment parent, List<User> userList) {
        mUserList = userList;
        mParentFragment = parent;
        mActivity = activity;
    }

    public void setUserList(List<User> userList) {
        mUserList = userList;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        holder.bind(mActivity, mUserList.get(position));
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private User mUser;
        private CircleImageView mProfileImage;
        private TextView mUsername, mTaskCount;
        private ImageButton mButtonDelete, mButtonEdit;

        UserHolder(@NonNull View itemView) {
            super(itemView);
            mProfileImage = itemView.findViewById(R.id.profile_image);
            mTaskCount = itemView.findViewById(R.id.task_numbers);
            mUsername = itemView.findViewById(R.id.username);
            mButtonDelete = itemView.findViewById(R.id.item_user_delete);
            mButtonEdit = itemView.findViewById(R.id.item_user_edit);

            itemView.setOnClickListener(this);

            mButtonDelete.setOnClickListener(v -> {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(mActivity);
                builder1.setMessage("Are you sure you want to delete " + mUser.getUsername() + "?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        (dialog, id) -> {

                            Repository.getInstance(mActivity).deleteUser(mUser.get_id());
                            mUserList = Repository.getInstance(mActivity).getUsers();
                            notifyDataSetChanged();
                            dialog.cancel();
                        });

                builder1.setNegativeButton(
                        "No",
                        (dialog, id) -> dialog.cancel());

                AlertDialog alert11 = builder1.create();
                alert11.show();
            });

            mButtonEdit.setOnClickListener(v -> {
                EditUserFragment editUserFragment = EditUserFragment.newInstance(mUser.get_id());
                editUserFragment.setTargetFragment(mParentFragment, REQUEST_CODE_EDIT_USER);
                editUserFragment.show(mParentFragment.getFragmentManager(), EditUserFragment.TAG);
            });
        }

        void bind(Activity activity, User user) {
            mUser = user;
            File photoFile = Repository.getInstance(activity).getPhotoFile(mUser);
            Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getAbsolutePath(), activity);
            if (bitmap != null)
                mProfileImage.setImageBitmap(bitmap);
            mUsername.setText(mUser.getUsername());
            int itemCount = Repository.getInstance(activity).getUserTaskNumber(mUser.get_id());
            String items = activity.getResources()
                    .getQuantityString(R.plurals.item_number, itemCount, itemCount);
            mTaskCount.setText(items);
        }

        @Override
        public void onClick(View v) {
            mActivity.startActivity(TaskPagerActivity.newIntent(mActivity, mUser.get_id(), false));
            mActivity.finish();
        }
    }
}
