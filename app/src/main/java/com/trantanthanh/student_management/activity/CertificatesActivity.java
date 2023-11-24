package com.trantanthanh.student_management.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.trantanthanh.student_management.R;
import com.trantanthanh.student_management.adapter.CertificateAdapter;
import com.trantanthanh.student_management.databinding.ActivityCertificatesBinding;
import com.trantanthanh.student_management.firestore.StudentFirestore;
import com.trantanthanh.student_management.model.Certificate;
import com.trantanthanh.student_management.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CertificatesActivity extends AppCompatActivity {

    private ActivityCertificatesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCertificatesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.iconBack.setOnClickListener(v -> {
            finish();
        });
        Intent intent = getIntent();

        String studentId = intent.getStringExtra("studentId");

        CertificateAdapter certificateAdapter = new CertificateAdapter(null);
        binding.rcvCerts.setAdapter(certificateAdapter);
        binding.rcvCerts.setLayoutManager(new LinearLayoutManager(this));
        StudentFirestore studentFirestore = new StudentFirestore();
        CompletableFuture<List<Certificate>> result = studentFirestore.getAllCertificate(studentId);
        result.thenAccept(certificateList -> {
            // Cập nhật dữ liệu mới lấy được vào RecyclerView Adapter
            runOnUiThread(() -> {
                certificateAdapter.setCertificateList(certificateList);
                binding.rcvCerts.getAdapter().notifyDataSetChanged();
            });
        });
        DocumentReference studentDoc = FirebaseFirestore.getInstance()
                                    .collection("student").document(studentId);
        studentDoc.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                // Xử lý lỗi khi lắng nghe
                return;
            }
            if (querySnapshot != null && querySnapshot.exists()) {
                Student updatedStudent = querySnapshot.toObject(Student.class);
                certificateAdapter.setCertificateList(updatedStudent.getCertificateList());
            }
        });
    }
}