package com.trantanthanh.student_management.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.trantanthanh.student_management.R;
import com.trantanthanh.student_management.adapter.StudentAdapter;
import com.trantanthanh.student_management.adapter.UserAdapter;
import com.trantanthanh.student_management.databinding.ActivityStudentBinding;
import com.trantanthanh.student_management.firestore.StudentFirestore;
import com.trantanthanh.student_management.model.Student;
import com.trantanthanh.student_management.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class StudentActivity extends AppCompatActivity {

    private ActivityStudentBinding binding;

    private StudentAdapter studentAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.iconBack.setOnClickListener(v -> {
            finish();
        });

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userRole = sharedPreferences.getString("userRole", "defaultRole"); // defaultRole là giá trị mặc định nếu không tìm thấy giá trị trong SharedPreferences
        binding.toolbar.settingToolbar.setOnClickListener(v -> {
            if (userRole.equals("Nhân viên")) {
                new MaterialAlertDialogBuilder(StudentActivity.this)
                        .setTitle("Cảnh báo")
                        .setMessage("Bạn không có quyền truy cập tính năng này")
                        .setNeutralButton("Xác nhận", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            } else {

            }
        });

        studentAdapter = new StudentAdapter(null);
        studentAdapter.setContext(this);
        binding.rcvStudent.setAdapter(studentAdapter);
        binding.rcvStudent.setLayoutManager(new LinearLayoutManager(this));
        StudentFirestore studentFirestore = new StudentFirestore();
        CompletableFuture<List<Student>> result = studentFirestore.getAll();
        result.thenAccept(studentList -> {
            // Cập nhật dữ liệu mới lấy được vào RecyclerView Adapter
            runOnUiThread(() -> {
                studentAdapter.setStudentList(studentList);
                binding.rcvStudent.getAdapter().notifyDataSetChanged();
            });
        }).exceptionally(ex -> {
            // Xử lý ngoại lệ nếu có lỗi xảy ra khi lấy dữ liệu từ Firestore
            ex.printStackTrace();
            return null;
        });

        CollectionReference studentRef = FirebaseFirestore.getInstance().collection("student");
        studentRef.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                // Xử lý lỗi khi lắng nghe
                return;
            }
            if (querySnapshot != null) {
                List<Student> updatedStudentList = new ArrayList<>();
                for (QueryDocumentSnapshot document : querySnapshot) {
                    Student updatedStudent = document.toObject(Student.class);
                    updatedStudentList.add(updatedStudent);
                }
                studentAdapter.setStudentList(updatedStudentList);
                binding.rcvStudent.getAdapter().notifyDataSetChanged();
            }
        });
    }
}