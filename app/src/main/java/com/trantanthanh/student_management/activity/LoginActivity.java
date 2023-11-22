package com.trantanthanh.student_management.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
//            User user = new User(null, "Thành Thứ Nhất", "thanhtran123", "0907640698", "Manager", "Normal");
//            userCollection.whereEqualTo("phone", user.getPhone())
//                    .get()
//                    .addOnSuccessListener(querySnapshot -> {
//                        if (!querySnapshot.isEmpty()) {
//                            // Số điện thoại đã tồn tại, không cho tạo tài khoản mới
//
//                        } else {
//                            // Số điện thoại chưa tồn tại, tạo tài khoản mới
//
//                            //user.setPassword(PasswordHasher.hashPassword(user.getPassword()));
//                            userCollection.add(user)
//                                    .addOnSuccessListener(documentReference -> {
//                                        // Tài khoản mới đã được tạo thành công
//                                        String userId = documentReference.getId(); // Lấy ID của tài khoản vừa được tạo
//                                        new MaterialAlertDialogBuilder(LoginActivity.this)
//                                        .setTitle("Đăng nhập thất bại")
//                                        .setMessage("Số điện thoại hoặc mật khẩu không đúng")
//                                        .setNeutralButton("Đăng nhập lại", (dialog, which) -> {
//                                            dialog.dismiss();
//                                        })
//                                        .show();
//                                    })
//                                    .addOnFailureListener(e -> {
//                                        // Xử lý khi có lỗi xảy ra trong quá trình tạo tài khoản
//
//                                    });
//                        }
//                    })
//                    .addOnFailureListener(e -> {
//                        // Xử lý khi có lỗi xảy ra trong quá trình truy vấn Firestore
//
//                    });



//            CompletableFuture<Boolean> created = userRepository.createNewUser(user);
////            created.thenAccept(isSuccess -> {
////                if (isSuccess) {
////                    // Xử lý khi tạo người dùng thành công
////                    new MaterialAlertDialogBuilder(LoginActivity.this)
////                            .setTitle("Đăng nhập thất bại")
////                            .setMessage("Số điện thoại hoặc mật khẩu không đúng")
////                            .setNeutralButton("Đăng nhập lại", (dialog, which) -> {
////                                dialog.dismiss();
////                            })
////                            .show();
////                } else {
////                    // Xử lý khi tạo người dùng thất bại
//                }
//
//            });
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
                    loginHistoryFirestore.save(result.getId());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("user", result);
                    startActivity(intent);
                }
            });
        });
    }
}