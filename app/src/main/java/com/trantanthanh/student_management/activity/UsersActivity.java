package com.trantanthanh.student_management.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.trantanthanh.student_management.R;
import com.trantanthanh.student_management.adapter.LoginHistoryAdapter;
import com.trantanthanh.student_management.adapter.UserAdapter;
import com.trantanthanh.student_management.databinding.ActivityUsersBinding;
import com.trantanthanh.student_management.dto.LoginHistoryDTO;
import com.trantanthanh.student_management.firestore.UserFirestore;
import com.trantanthanh.student_management.model.User;

import java.util.ArrayList;
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

        CollectionReference userRef = FirebaseFirestore.getInstance().collection("user");
        userRef.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                // Xử lý lỗi khi lắng nghe
                return;
            }
            if (querySnapshot != null) {
                List<User> updatedUserList = new ArrayList<>();
                for (QueryDocumentSnapshot document : querySnapshot) {
                    User updatedUser = document.toObject(User.class);
                    updatedUserList.add(updatedUser);
                }
                userAdapter.setUserList(updatedUserList);
                binding.rcvUsers.getAdapter().notifyDataSetChanged();
            }
        });
    }


}