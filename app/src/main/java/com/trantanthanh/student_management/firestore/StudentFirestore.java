package com.trantanthanh.student_management.firestore;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.trantanthanh.student_management.model.Certificate;
import com.trantanthanh.student_management.model.Student;
import com.trantanthanh.student_management.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class StudentFirestore {
    private final FirebaseFirestore db;
    private final CollectionReference studentCollection;

    public StudentFirestore() {
        db = FirebaseFirestore.getInstance();
        studentCollection = db.collection("student");
    }

    public CompletableFuture<List<Student>> getAll() {
        CompletableFuture<List<Student>> result = new CompletableFuture<>();
        studentCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Student> studentList = new ArrayList<>();
                    // Dùng để tránh trường hợp chưa lấy dữ liệu hết đã trả về kết quả
                    AtomicInteger tasksCount = new AtomicInteger(queryDocumentSnapshots.size());

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Student student = doc.toObject(Student.class);
                        studentList.add(student);
                        if (tasksCount.decrementAndGet() == 0) {
                            // Sắp xếp theo lớp
                            Collections.sort(studentList, Comparator.comparingInt(Student::getClassName));
                            result.complete(studentList);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    result.completeExceptionally(e);
                });
        return result;
    }


    public CompletableFuture<Boolean> update(Student student) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        DocumentReference studentRef = studentCollection.document(student.getId());
        if(student != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", student.getName());
            updates.put("birthdate", student.getBirthdate());
            updates.put("gender", student.getGender());
            updates.put("className", student.getClassName());
            studentRef.update(updates)
                    .addOnSuccessListener(aVoid -> {
                        // Cập nhật thành công
                        result.complete(true);
                    })
                    .addOnFailureListener(e -> {
                        // Xử lý lỗi khi cập nhật thất bại
                        result.completeExceptionally(e);
                    });
        }
        return result;
    }

    public CompletableFuture<List<Certificate>> getAllCertificate(String idStudent) {
        CompletableFuture<List<Certificate>> result = new CompletableFuture<>();
        CollectionReference certificateCollection = studentCollection.document(idStudent)
                                    .collection("certificateList");
        certificateCollection.get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Certificate> certificateList = new ArrayList<>();
                // Dùng để tránh trường hợp chưa lấy dữ liệu hết đã trả về kết quả
                AtomicInteger tasksCount = new AtomicInteger(queryDocumentSnapshots.size());
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    Certificate cert = doc.toObject(Certificate.class);
                    certificateList.add(cert);
                    if (tasksCount.decrementAndGet() == 0) {
                        result.complete(certificateList);
                    }
                }
            })
            .addOnFailureListener(e -> {
                result.completeExceptionally(e);
            });

        return result;
    }
}
