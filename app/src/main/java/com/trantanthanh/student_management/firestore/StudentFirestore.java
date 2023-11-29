package com.trantanthanh.student_management.firestore;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.trantanthanh.student_management.model.Certificate;
import com.trantanthanh.student_management.model.Student;
import com.trantanthanh.student_management.model.User;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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

    public CompletableFuture<Boolean> insert(Student student) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();

        studentCollection.add(student)
            .addOnSuccessListener(documentReference -> {
                // Xử lý khi việc thêm thành công -> tạo subcollection
                CollectionReference certificateCollection = documentReference.collection("certificateList");
                result.complete(true);
            })
            .addOnFailureListener(e -> {
                // Xử lý khi việc thêm thất bại
                result.completeExceptionally(e);
            });
        return result;
    }

    public CompletableFuture<Boolean> insertCertificate(Certificate certificate, String studentId) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        CollectionReference certificateRef = studentCollection.document(studentId).collection("certificateList");
        certificateRef.add(certificate)
            .addOnSuccessListener(documentReference -> {
                result.complete(true);
            })
            .addOnFailureListener(e -> {
                // Xử lý khi việc thêm thất bại
                result.completeExceptionally(e);
            });
        return result;
    }

    public CompletableFuture<Boolean> insertStudentByFile(String filePath) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            if (fileInputStream != null) {
                Workbook workbook = new XSSFWorkbook(fileInputStream);
                // Đọc sheet đầu tiên từ workbook
                Sheet sheet = workbook.getSheetAt(0);

                // Đọc dữ liệu từ từng dòng trong sheet và thêm học sinh vào Firestore
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) {
                        // Bỏ qua dòng tiêu đề (nếu cần)
                        continue;
                    }
                    Log.d("Excel", row.getCell(0).getStringCellValue());
                    String name = row.getCell(0).getStringCellValue();
                    String birthdate = row.getCell(1).getStringCellValue();
                    int gender = row.getCell(2).getStringCellValue().equalsIgnoreCase("Nam") ? 1 : 0;
                    int className = (int) row.getCell(3).getNumericCellValue();
                    // Tạo đối tượng Student từ dữ liệu trong dòng của Excel
                    Student student = new Student();
                    student.setName(name);
                    student.setBirthdate(birthdate);
                    student.setGender(gender);
                    student.setClassName(className);

                    // Thêm đối tượng Student vào Firestore
                    studentCollection.add(student)
                            .addOnSuccessListener(documentReference -> {
                                // Xử lý khi việc thêm thành công
                                // Có thể log hoặc xử lý tiếp theo ở đây
                            })
                            .addOnFailureListener(e -> {
                                // Xử lý khi việc thêm thất bại
                                result.completeExceptionally(e);
                            });
                }
                // Đóng workbook và file sau khi hoàn thành việc đọc
                workbook.close();
                fileInputStream.close();
                result.complete(true); // Đánh dấu hoàn thành khi đã đọc và thêm học sinh thành công
            } else {
                result.complete(false);
            }


        } catch (IOException e) {
            e.printStackTrace();
            result.completeExceptionally(e);
        }

        return result;
    }


    public boolean exportStudentToFile(List<Student> students) {
        String fileName = "students.xlsx";
        String filePath = Environment.getExternalStorageDirectory() + "/" + fileName;
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Students");
            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Tên");
            headerRow.createCell(1).setCellValue("Ngày sinh");
            headerRow.createCell(2).setCellValue("Giới tính");
            headerRow.createCell(3).setCellValue("Lớp");
            // Populate data rows
            int rowNum = 1;
            for (Student student : students) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(student.getName());
                row.createCell(1).setCellValue(student.getBirthdate());
                String gender = student.getGender() == 1 ? "Nam" : "Nữ";
                row.createCell(2).setCellValue(gender);
                row.createCell(3).setCellValue(student.getClassName() + "");
            }
            // Write to file
            File file = new File(filePath);
            FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            fileOut.close();
            return true;
            // Show a message or handle the success event
        } catch (IOException e) {
            e.printStackTrace();
            // Handle error
            return false;
        }
    }

    public CompletableFuture<Boolean> insertCertificateByFile(String filePath, String studentId) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            if (fileInputStream != null) {
                Workbook workbook = new XSSFWorkbook(fileInputStream);
                // Đọc sheet đầu tiên từ workbook
                Sheet sheet = workbook.getSheetAt(0);
                // Đọc dữ liệu từ từng dòng trong sheet và thêm certificate vào Firestore
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) {
                        // Bỏ qua dòng tiêu đề (nếu cần)
                        continue;
                    }
                    String name = row.getCell(0).getStringCellValue();
                    String issueDate = row.getCell(1).getStringCellValue();
                    String expiryDate = row.getCell(2).getStringCellValue();
                    String issuedBy = row.getCell(3).getStringCellValue();
                    // Tạo đối tượng Certificate từ dữ liệu trong dòng của Excel
                    Certificate certificate = new Certificate();
                    certificate.setName(name);
                    certificate.setIssueDate(issueDate);
                    certificate.setExpiryDate(expiryDate);
                    certificate.setIssuedBy(issuedBy);

                    // Thêm đối tượng vào Firestore
                    studentCollection.document(studentId).collection("certificateList").add(certificate)
                            .addOnSuccessListener(documentReference -> {
                                // Xử lý khi việc thêm thành công
                                // Có thể log hoặc xử lý tiếp theo ở đây
                            })
                            .addOnFailureListener(e -> {
                                // Xử lý khi việc thêm thất bại
                                result.completeExceptionally(e);
                            });
                }
                // Đóng workbook và file sau khi hoàn thành việc đọc
                workbook.close();
                fileInputStream.close();
                result.complete(true); // Đánh dấu hoàn thành khi đã đọc và thêm thành công
            } else {
                result.complete(false);
            }

        } catch (IOException e) {
            e.printStackTrace();
            result.completeExceptionally(e);
        }
        return result;
    }

    public boolean exportCertificate(List<Certificate> certificateList) {
        String fileName = "certificates.xlsx";
        String filePath = Environment.getExternalStorageDirectory() + "/" + fileName;

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Certificates");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Tên chứng chỉ");
            headerRow.createCell(1).setCellValue("Ngày thi");
            headerRow.createCell(2).setCellValue("Ngày hết hạn");
            headerRow.createCell(3).setCellValue("Đơn vị cấp");

            // Populate data rows
            int rowNum = 1;
            for (Certificate certificate : certificateList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(certificate.getName());
                row.createCell(1).setCellValue(certificate.getIssueDate());
                row.createCell(2).setCellValue(certificate.getExpiryDate());
                row.createCell(3).setCellValue(certificate.getIssuedBy());
            }

            // Write to file
            File file = new File(filePath);
            FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            fileOut.close();
            return true;
            // Show a message or handle the success event
        } catch (IOException e) {
            e.printStackTrace();
            // Handle error
            return false;
        }
    }

    public CompletableFuture<Boolean> deleteStudent(String studentId) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        studentCollection.document(studentId).delete()
                .addOnSuccessListener(aVoid -> {
                    result.complete(true);
                })
                .addOnFailureListener(e -> {
                    result.complete(false);
                });
        return result;
    }

    public CompletableFuture<Boolean> deleteCertificate(String id, String studentId) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        studentCollection.document(studentId).collection("certificateList").document(id).delete()
                .addOnSuccessListener(aVoid -> {
                    result.complete(true);
                })
                .addOnFailureListener(e -> {
                    result.complete(false);
                });
        return result;
    }
}
