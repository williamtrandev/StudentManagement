package com.trantanthanh.student_management.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.trantanthanh.student_management.databinding.ActivityLoginBinding;
import com.trantanthanh.student_management.firestore.LoginHistoryFirestore;
import com.trantanthanh.student_management.model.User;
import com.trantanthanh.student_management.firestore.UserFirestore;

import java.util.concurrent.CompletableFuture;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;

    private ImageView btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        btnLogin = binding.btnLogin;
        UserFirestore userFirestore = new UserFirestore();
        LoginHistoryFirestore loginHistoryFirestore = new LoginHistoryFirestore();
        btnLogin.setOnClickListener(v -> {
            String phone = binding.edtUsername.getText().toString();
            String password = binding.edtPassword.getText().toString();

            CompletableFuture<User> userLogin = userFirestore.loginUser(phone, password);

            userLogin.thenAccept(result -> {
                if(result == null) {
                    new MaterialAlertDialogBuilder(LoginActivity.this)
                            .setTitle("Đăng nhập thất bại")
                            .setMessage("Số điện thoại hoặc mật khẩu không đúng")
                            .setNeutralButton("Đăng nhập lại", (dialog, which) -> {
                                dialog.dismiss();
                            })
                    .show();
                } else {
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userRole", result.getRole());
                    editor.apply();
                    loginHistoryFirestore.save(result.getId());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("user", result);
                    startActivity(intent);
                }
            });
        });
    }
}