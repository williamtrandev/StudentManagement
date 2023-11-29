package com.trantanthanh.student_management.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;
import com.trantanthanh.student_management.activity.DetailUserActivity;
import com.trantanthanh.student_management.databinding.UserItemBinding;
import com.trantanthanh.student_management.firestore.StudentFirestore;
import com.trantanthanh.student_management.firestore.UserFirestore;
import com.trantanthanh.student_management.model.User;
import com.trantanthanh.student_management.utils.ChangeStringFormat;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private List<User> userList;
    private Context context;

    private UserFirestore userFirestore;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setUserFirestore(UserFirestore userFirestore) {
        this.userFirestore = userFirestore;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        UserItemBinding userItemBinding = UserItemBinding.inflate(layoutInflater, parent, false);

        return new UserAdapter.ViewHolder(userItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        if(user == null) return;
        holder.tvUser.setText(user.getName());
        holder.tvUserRole.setText(user.getRole());
        if(user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            Picasso.get().load(user.getAvatar()).into(holder.imgUser);
        }
        holder.imgDeleteUser.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(holder.itemView.getContext())
                    .setTitle("Xác nhận xoá User này không")
                    .setMessage("Xóa sẽ không thể hoàn tác")
                    .setNeutralButton("Xác nhận", (dialog, which) -> {
                        userFirestore.deleteUser(user.getId());
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailUserActivity.class);
            intent.putExtra("user", user);
            holder.itemView.getContext().startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        if(userList == null) return 0;
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUser, tvUserRole;
        public ShapeableImageView imgUser;
        public CardView cardView;
        public ImageView imgDeleteUser;
        UserItemBinding userItemBinding;
        public ViewHolder(UserItemBinding userItemBinding) {
            super(userItemBinding.getRoot());
            this.userItemBinding = userItemBinding;
            tvUser = userItemBinding.tvUser;
            tvUserRole = userItemBinding.tvUserRole;
            imgUser = userItemBinding.imgUser;
            cardView = userItemBinding.cardView;
            imgDeleteUser = userItemBinding.imgDeleteUser;
        }

    }
}
