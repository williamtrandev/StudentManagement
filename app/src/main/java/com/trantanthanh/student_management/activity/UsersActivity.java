package com.trantanthanh.student_management.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.trantanthanh.student_management.R;
import com.trantanthanh.student_management.adapter.LoginHistoryAdapter;
import com.trantanthanh.student_management.adapter.UserAdapter;
import com.trantanthanh.student_management.databinding.ActivityUsersBinding;
import com.trantanthanh.student_management.dto.LoginHistoryDTO;
import com.trantanthanh.student_management.firestore.UserFirestore;
import com.trantanthanh.student_management.model.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UsersActivity extends AppCompatActivity {
    private ActivityUsersBinding binding;
    private UserAdapter userAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        binding.toolbar.iconBack.setOnClickListener(v -> {
            finish();
        });
        userAdapter = new UserAdapter(null);
        userAdapter.setContext(this);
        binding.rcvUsers.setAdapter(userAdapter);
        binding.rcvUsers.setLayoutManager(new LinearLayoutManager(this));
        UserFirestore userFirestore = new UserFirestore();
        CompletableFuture<List<User>> result = userFirestore.getAll();
        result.thenAccept(userList -> {
            // Cập nhật dữ liệu mới lấy được vào RecyclerView Adapter
            runOnUiThread(() -> {
                userAdapter.setUserList(userList);
                binding.rcvUsers.getAdapter().notifyDataSetChanged();
            });
        }).exceptionally(ex -> {
            // Xử lý ngoại lệ nếu có lỗi xảy ra khi lấy dữ liệu từ Firestore
            ex.printStackTrace();
            return null;
        });
    }


}