package com.trantanthanh.student_management.firestore;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.trantanthanh.student_management.dto.LoginHistoryDTO;
import com.trantanthanh.student_management.model.LoginHistory;
import com.trantanthanh.student_management.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class LoginHistoryFirestore {
    private final FirebaseFirestore db;
    private final CollectionReference historyCollection;

    public LoginHistoryFirestore() {
        db = FirebaseFirestore.getInstance();
        historyCollection = db.collection("loginHistory");
    }

    public void save(String userId) {
        LoginHistory historyLogin = new LoginHistory(new Date(), userId);
        historyCollection.add(historyLogin);
    }

    public CompletableFuture<List<LoginHistoryDTO>> getAll() {
        CompletableFuture<List<LoginHistoryDTO>> future = new CompletableFuture<>();
        historyCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<LoginHistoryDTO> loginHistories = new ArrayList<>();
                    // Dùng để tránh trường hợp chưa lấy dữ liệu hết đã trả về kết quả
                    AtomicInteger tasksCount = new AtomicInteger(queryDocumentSnapshots.size());
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Date loginTime = doc.getDate("loginTime");
                        String userId = doc.getString("userId");
                        DocumentReference userRef = db.collection("user").document(userId);
                        userRef.get()
                                .addOnSuccessListener(userDocumentSnapshot  -> {
                                    User user = userDocumentSnapshot.toObject(User.class);
                                    LoginHistoryDTO loginHistoryDTO = new LoginHistoryDTO(doc.getId(), loginTime, user);
                                    loginHistories.add(loginHistoryDTO);
                                    Log.d("HISFIRE", loginTime.toString());

                                    // Giảm số lượng tác vụ cần hoàn thành và kiểm tra khi nào hoàn thành hết
                                    if (tasksCount.decrementAndGet() == 0) {
                                        // Sắp xếp theo thời gian đăng nhập
                                        Collections.sort(loginHistories, (lhs, rhs) -> rhs.getLoginTime().compareTo(lhs.getLoginTime()));
                                        future.complete(loginHistories);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    future.completeExceptionally(e); // Hoàn thành CompletableFuture với lỗi nếu có lỗi xảy ra
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    future.completeExceptionally(e);
                });
        return future;
    }

}
