package com.trantanthanh.student_management.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.trantanthanh.student_management.R;
import com.trantanthanh.student_management.component.LoadingDialog;
import com.trantanthanh.student_management.databinding.ActivityDetailStudentBinding;
import com.trantanthanh.student_management.firestore.StudentFirestore;
import com.trantanthanh.student_management.model.Student;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class DetailStudentActivity extends AppCompatActivity {
    private ActivityDetailStudentBinding binding;
    private Student updatedStudent;

    private StudentFirestore studentFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        studentFirestore = new StudentFirestore();
        Intent intent = getIntent();
        Student student = intent.getParcelableExtra("student");
        updatedStudent = new Student(student);
        binding.edtStudentName.setText(student.getName());
        binding.edtStudentBirthdate.setText(student.getBirthdate());
        if(student.getGender() == 1) {
            binding.rbMale.setChecked(true);
        } else {
            binding.rbFemale.setChecked(true);
        }

        switch (student.getClassName()) {
            case 6:
                binding.rb6.setChecked(true);
                break;
            case 7:
                binding.rb7.setChecked(true);
                break;
            case 8:
                binding.rb8.setChecked(true);
                break;
            case 9:
                binding.rb9.setChecked(true);
                break;
        }
        binding.toolbar.iconBack.setOnClickListener(v -> {
            finish();
        });

        binding.rgClass.setOnCheckedChangeListener((ground, checkedId) -> {
            if(checkedId != -1) {
                RadioButton radioButton = findViewById(checkedId);
                if(radioButton != null) {
                    String selectedValue = radioButton.getText().toString();
                    int className = Integer.parseInt(selectedValue.replace("Lớp ", ""));
                    updatedStudent.setClassName(className);
                }
            }
        });

        binding.rgGender.setOnCheckedChangeListener((ground, checkedId) -> {
            if(checkedId != -1) {
                RadioButton radioButton = findViewById(checkedId);
                if(radioButton != null) {
                    String selectedValue = radioButton.getText().toString();
                    int gender = 1;
                    if(selectedValue.equalsIgnoreCase("Nam")) {
                        gender = 1;
                    } else {
                        gender = 0;
                    }
                    updatedStudent.setGender(gender);
                }
            }
        });

        binding.btnSave.setOnClickListener(v -> {
            String name = binding.edtStudentName.getText().toString();
            updatedStudent.setName(name);
            CompletableFuture<Boolean> result = studentFirestore.update(updatedStudent);
            LoadingDialog loadingDialog = new LoadingDialog(DetailStudentActivity.this);
            loadingDialog.show();
            result.thenAccept(isSuccess -> {
                if(isSuccess) {
                    loadingDialog.dismiss();
                    Toast.makeText(DetailStudentActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_LONG).show();
                } else {
                    loadingDialog.dismiss();
                    Toast.makeText(DetailStudentActivity.this, "Cập nhật thông tin thất bại", Toast.LENGTH_LONG).show();
                }
            });
        });

        binding.btnDetail.setOnClickListener(v -> {
            Intent intentCert = new Intent(DetailStudentActivity.this, CertificatesActivity.class);
            intentCert.putExtra("studentId", student.getId());
            startActivity(intentCert);
        });
    }
}