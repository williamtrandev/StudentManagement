package com.trantanthanh.student_management.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trantanthanh.student_management.databinding.LoginHistoryItemBinding;
import com.trantanthanh.student_management.dto.LoginHistoryDTO;
import com.trantanthanh.student_management.utils.ChangeStringFormat;

import java.util.List;

public class LoginHistoryAdapter extends RecyclerView.Adapter<LoginHistoryAdapter.ViewHolder> {
    private List<LoginHistoryDTO>loginHistoryList;

    public LoginHistoryAdapter(List<LoginHistoryDTO> loginHistoryList) {
        this.loginHistoryList = loginHistoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LoginHistoryItemBinding loginHistoryItemBinding = LoginHistoryItemBinding.inflate(layoutInflater, parent, false);

        return new ViewHolder(loginHistoryItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LoginHistoryDTO loginHistory = loginHistoryList.get(position);
        if(loginHistory == null) return;
//        holder.imgUser.setImageURI();
        holder.tvUser.setText(loginHistory.getUser().getName());
        String timeLoginVn = ChangeStringFormat.formatToVietnameseDate(loginHistory.getLoginTime());
        holder.tvTimeLogin.setText(timeLoginVn);
    }

    @Override
    public int getItemCount() {
        if(loginHistoryList == null) return 0;
        return loginHistoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUser, tvTimeLogin;
        public ImageView imgUser;
        LoginHistoryItemBinding loginHistoryItemBinding;
        public ViewHolder(LoginHistoryItemBinding loginHistoryItemBinding) {
            super(loginHistoryItemBinding.getRoot());
            this.loginHistoryItemBinding = loginHistoryItemBinding;
            tvUser = loginHistoryItemBinding.tvUser;
            tvTimeLogin = loginHistoryItemBinding.tvTimeLogin;
            imgUser = loginHistoryItemBinding.imgUser;
        }

    }
}
