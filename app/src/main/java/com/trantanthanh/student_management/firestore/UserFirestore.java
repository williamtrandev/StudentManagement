package com.trantanthanh.student_management.firestore;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.trantanthanh.student_management.dto.LoginHistoryDTO;
import com.trantanthanh.student_management.model.User;
import com.trantanthanh.student_management.utils.PasswordHasher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class UserFirestore {
    private final FirebaseFirestore db;
    private final CollectionReference userCollection;

    public UserFirestore() {
        db = FirebaseFirestore.getInstance();
        userCollection = db.collection("user");
    }

    public CompletableFuture<Boolean> createNewUser(User user) {

        CompletableFuture<Boolean> result = new CompletableFuture<>();

        // Kiểm tra xem số điện thoại đã tồn tại trong Firestore chưa
        userCollection.whereEqualTo("phone", user.getPhone())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Số điện thoại đã tồn tại, không cho tạo tài khoản mới
                            // Xử lý thông báo hoặc hành động phù hợp ở đây
                            result.complete(false);
                        } else {
                            // Số điện thoại chưa tồn tại, tạo tài khoản mới
                            user.setPassword(PasswordHasher.hashPassword(user.getPassword()));
                            userCollection.add(user)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            // Tài khoản mới đã được tạo thành công
                                            DocumentReference document = task1.getResult();
                                            String userId = document.getId(); // Lấy ID của tài khoản vừa được tạo
                                            // Thực hiện hành động sau khi tạo tài khoản thành công (nếu cần)
                                            result.complete(true);
                                        } else {
                                            // Xử lý khi có lỗi xảy ra trong quá trình tạo tài khoản
                                            Exception e = task1.getException();
                                            result.complete(false);
                                        }
                                    });
                        }
                    } else {
                        // Xử lý khi có lỗi xảy ra trong quá trình truy vấn Firestore
                        Exception e = task.getException();
                        result.complete(false);
                    }
                });

        return result;
    }



    public CompletableFuture<User> loginUser(String phone, String password) {

        CompletableFuture<User> result = new CompletableFuture<>();

        // Tìm người dùng với số điện thoại tương ứng
        userCollection.whereEqualTo("phone", phone)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Tìm thấy người dùng với số điện thoại đã nhập
                            DocumentSnapshot userDocument = querySnapshot.getDocuments().get(0);
                            User user = userDocument.toObject(User.class);

                            // Lấy mật khẩu đã hash từ cơ sở dữ liệu
                            String hashedPasswordFromDB = user.getPassword();

                            // So sánh mật khẩu đã nhập với mật khẩu đã hash trong cơ sở dữ liệu
                            if (PasswordHasher.verifyPassword(password, hashedPasswordFromDB)) {
                                // Mật khẩu đúng, trả về thông tin người dùng
                                result.complete(user);
                            } else {
                                // Sai thông tin đăng nhập, không thể đăng nhập
                                result.complete(null);
                            }
                        } else {
                            // Không tìm thấy người dùng với số điện thoại đã nhập
                            result.complete(null);
                        }
                    } else {
                        // Xử lý khi có lỗi xảy ra trong quá trình truy vấn Firestore
                        Exception e = task.getException();
                        result.complete(null);
                    }
                });

        return result;
    }

    public CompletableFuture<List<User>> getAll() {
        CompletableFuture<List<User>> result = new CompletableFuture<>();
        userCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> userList = new ArrayList<>();
                    // Dùng để tránh trường hợp chưa lấy dữ liệu hết đã trả về kết quả
                    AtomicInteger tasksCount = new AtomicInteger(queryDocumentSnapshots.size());

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Date loginTime = doc.getDate("loginTime");
                        String userId = doc.getString("userId");
                        User user = doc.toObject(User.class);
                        userList.add(user);
                        if (tasksCount.decrementAndGet() == 0) {
                            // Sắp xếp theo loại người dùng
                            Collections.sort(userList, (lu, ru) -> lu.getRole().compareTo(ru.getRole()));
                            result.complete(userList);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    result.completeExceptionally(e);
                });
        return result;
    }

    public CompletableFuture<Boolean> updateUser(User user) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();

        DocumentReference userRef = userCollection.document(user.getId());

        Map<String, Object> userMap = new HashMap<>();

        if (user.getName() != null) {
            userMap.put("name", user.getName());
        }
        if (user.getName() != null) {
            userMap.put("name", user.getName());
        }
        // Thêm các trường dữ liệu khác tương tự vào userMap

        if (!userMap.isEmpty()) {
            userRef
                    .update(userMap)
                    .addOnSuccessListener(aVoid -> {
                        // Cập nhật thành công
                        result.complete(true);
                    })
                    .addOnFailureListener(e -> {
                        // Xử lý lỗi khi cập nhật thất bại
                        result.completeExceptionally(e);
                    });
        } else {
            // Không có trường dữ liệu để cập nhật
            result.complete(true);
        }

        return result;
    }
}
