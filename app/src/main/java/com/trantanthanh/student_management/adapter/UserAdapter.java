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

import com.trantanthanh.student_management.activity.DetailUserActivity;
import com.trantanthanh.student_management.databinding.UserItemBinding;
import com.trantanthanh.student_management.model.User;
import com.trantanthanh.student_management.utils.ChangeStringFormat;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private List<User> userList;
    private Context context;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public void setContext(Context context) {
        this.context = context;
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
//        holder.imgUser.setImageURI();
        holder.tvUser.setText(user.getName());
        String userRole = ChangeStringFormat.formatToVietnameseRole(user.getRole());
        holder.tvUserRole.setText(userRole);
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
        public ImageView imgUser;
        public CardView cardView;
        UserItemBinding userItemBinding;
        public ViewHolder(UserItemBinding userItemBinding) {
            super(userItemBinding.getRoot());
            this.userItemBinding = userItemBinding;
            tvUser = userItemBinding.tvUser;
            tvUserRole = userItemBinding.tvUserRole;
            imgUser = userItemBinding.imgUser;
            cardView = userItemBinding.cardView;
        }

    }
}
