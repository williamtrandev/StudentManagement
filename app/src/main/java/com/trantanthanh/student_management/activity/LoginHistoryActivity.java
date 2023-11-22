package com.trantanthanh.student_management.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;

import com.trantanthanh.student_management.R;
import com.trantanthanh.student_management.adapter.LoginHistoryAdapter;
import com.trantanthanh.student_management.databinding.ActivityLoginBinding;
import com.trantanthanh.student_management.databinding.ActivityLoginHistoryBinding;
import com.trantanthanh.student_management.dto.LoginHistoryDTO;
import com.trantanthanh.student_management.firestore.LoginHistoryFirestore;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LoginHistoryActivity extends AppCompatActivity {
    private ActivityLoginHistoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.iconBack.setOnClickListener(v -> {
            finish();
        });
        LoginHistoryFirestore loginHistoryFirestore = new LoginHistoryFirestore();
        binding.rcvHistory.setAdapter(new LoginHistoryAdapter(null));
        binding.rcvHistory.setLayoutManager(new LinearLayoutManager(this));
        CompletableFuture<List<LoginHistoryDTO>> result = loginHistoryFirestore.getAll();
        result.thenAccept(historyList -> {
            // Cập nhật dữ liệu mới lấy được vào RecyclerView Adapter
            runOnUiThread(() -> {
                binding.rcvHistory.setAdapter(new LoginHistoryAdapter(historyList));
                Log.d("History", historyList.get(0).getLoginTime().toString());
                binding.rcvHistory.getAdapter().notifyDataSetChanged();
            });
        }).exceptionally(ex -> {
            // Xử lý ngoại lệ nếu có lỗi xảy ra khi lấy dữ liệu từ Firestore
            ex.printStackTrace();
            return null;
        });
    }
}