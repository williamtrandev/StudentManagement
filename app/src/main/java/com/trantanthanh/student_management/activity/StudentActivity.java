package com.trantanthanh.student_management.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.trantanthanh.student_management.R;
import com.trantanthanh.student_management.adapter.StudentAdapter;
import com.trantanthanh.student_management.adapter.UserAdapter;
import com.trantanthanh.student_management.component.LoadingDialog;
import com.trantanthanh.student_management.databinding.ActivityStudentBinding;
import com.trantanthanh.student_management.firestore.StudentFirestore;
import com.trantanthanh.student_management.model.Student;
import com.trantanthanh.student_management.model.User;
import com.trantanthanh.student_management.utils.RealPath;
import com.trantanthanh.student_management.utils.StudentSearch;
import com.trantanthanh.student_management.utils.StudentSorters;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class StudentActivity extends AppCompatActivity {

    private ActivityStudentBinding binding;

    private StudentAdapter studentAdapter;

    private List<Student> originalList;

    private StudentFirestore studentFirestore;

    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        studentFirestore = new StudentFirestore();
        int isAccepted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (isAccepted != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        isAccepted = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (isAccepted != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
        binding.toolbar.iconBack.setOnClickListener(v -> {
            finish();
        });
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userRole = sharedPreferences.getString("userRole", "defaultRole"); // defaultRole là giá trị mặc định nếu không tìm thấy giá trị trong SharedPreferences
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
        studentAdapter.setStudentFirestore(studentFirestore);
        studentAdapter.setUserRole(userRole);
        binding.rcvStudent.setAdapter(studentAdapter);
        binding.rcvStudent.setLayoutManager(new LinearLayoutManager(this));
        StudentFirestore studentFirestore = new StudentFirestore();
        CompletableFuture<List<Student>> result = studentFirestore.getAll();
        result.thenAccept(studentList -> {
            // Cập nhật dữ liệu mới lấy được vào RecyclerView Adapter
            runOnUiThread(() -> {
                originalList = new ArrayList<>();
                originalList.addAll(studentList);
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

        binding.toolbar.settingToolbar.setOnClickListener(v -> {
            showPopupMenu(v);
        });
        
        binding.imgReload.setOnClickListener(v -> {
            studentAdapter.setStudentList(originalList);
            studentAdapter.notifyDataSetChanged();
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "PERMISSION REQUIRED FOR READ", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "PERMISSION REQUIRED WRITE", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ActivityResultLauncher<Intent> pickFileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            // Sử dụng URI để lấy đường dẫn thực tế của tệp
                            String filePath = RealPath.getRealPath(StudentActivity.this, uri);
                            CompletableFuture<Boolean> check = studentFirestore.insertStudentByFile(filePath);
                            LoadingDialog loadingDialog = new LoadingDialog(this);
                            loadingDialog.show();
                            // Xử lý kết quả khi hoàn thành
                            check.thenAccept(isSuccess -> {
                                if(isSuccess) {
                                    loadingDialog.dismiss();
                                    Toast.makeText(StudentActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    loadingDialog.dismiss();
                                    Toast.makeText(StudentActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            });

    private void pickExcelFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        pickFileLauncher.launch(intent);
    }
    private void showPermission() {
        new MaterialAlertDialogBuilder(StudentActivity.this)
                .setTitle("Bạn không có quyền")
                .setMessage("Chức năng này bạn không được quyền sử dụng")
                .setNeutralButton("Xác nhận", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.options_menu_student, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.add_student_by_file) {
                if(userRole.equalsIgnoreCase("Nhân viên")) {
                    showPermission();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (!Environment.isExternalStorageManager()) {
                            Log.i("=== IS EXTERNALSTORAGEMANAGER ===", "TRUE");
                            // Request the MANAGE_EXTERNAL_STORAGE permission
                            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                            startActivity(intent);
                        }
                    }
                    pickExcelFile();
                }

            } else if(item.getItemId() == R.id.export_student) {
                if(userRole.equalsIgnoreCase("Nhân viên")) {
                    showPermission();
                } else {
                    boolean isSucces = studentFirestore.exportStudentToFile(studentAdapter.getStudentList());
                    if(isSucces) {
                        Toast.makeText(StudentActivity.this, "Xuất file thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(StudentActivity.this, "Xuất file thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

            } else if(item.getItemId() == R.id.sort_student) {
                showBottomDialog();
            } else if(item.getItemId() == R.id.search_student) {
                showDialogSearch();
            } else {
                if(userRole.equalsIgnoreCase("Nhân viên")) {
                    showPermission();
                } else {
                    showDialogAdd();
                }

            }
            return true;
        });

        popupMenu.show();
    }

    private void showDialogAdd() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.dialog_add_student);

        FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setPeekHeight(getResources().getDisplayMetrics().heightPixels);
        }
        Student student = new Student();
        Button btnSave = bottomSheetDialog.findViewById(R.id.btnSave);
        EditText edtStudentName = bottomSheetDialog.findViewById(R.id.edtStudentName);
        EditText edtStudentBirthdate = bottomSheetDialog.findViewById(R.id.edtStudentBirthdate);
        RadioGroup rgClass = bottomSheetDialog.findViewById(R.id.rgClass);
        RadioGroup rgGender = bottomSheetDialog.findViewById(R.id.rgGender);
        rgClass.setOnCheckedChangeListener((ground, checkedId) -> {
            if(checkedId != -1) {
                RadioButton radioButton = bottomSheetDialog.findViewById(checkedId);
                if(radioButton != null) {
                    String selectedValue = radioButton.getText().toString();
                    int className = Integer.parseInt(selectedValue.replace("Lớp ", ""));
                    student.setClassName(className);
                }
            }
        });

        rgGender.setOnCheckedChangeListener((ground, checkedId) -> {
            if(checkedId != -1) {
                RadioButton radioButton = bottomSheetDialog.findViewById(checkedId);
                if(radioButton != null) {
                    String selectedValue = radioButton.getText().toString();
                    int gender = 1;
                    if(selectedValue.equalsIgnoreCase("Nam")) {
                        gender = 1;
                    } else {
                        gender = 0;
                    }
                    student.setGender(gender);
                }
            }
        });
        btnSave.setOnClickListener(v -> {
            String name = edtStudentName.getText().toString();
            String birthdate = edtStudentBirthdate.getText().toString();
            student.setName(name);
            student.setBirthdate(birthdate);
            CompletableFuture<Boolean> result = studentFirestore.insert(student);
            LoadingDialog loadingDialog = new LoadingDialog(StudentActivity.this);
            loadingDialog.show();
            result.thenAccept(isSuccess -> {
                if(isSuccess) {
                    loadingDialog.dismiss();
                    Toast.makeText(StudentActivity.this, "Thêm thành công", Toast.LENGTH_LONG).show();
                } else {
                    loadingDialog.dismiss();
                    Toast.makeText(StudentActivity.this, "Thêm thất bại", Toast.LENGTH_LONG).show();
                }
            });
        });
        bottomSheetDialog.show();
    }

    private void showBottomDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.dialog_sort);

        FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setPeekHeight(getResources().getDisplayMetrics().heightPixels);
        }

        ImageView imgName = bottomSheetDialog.findViewById(R.id.imgName);
        ImageView imgBirthdate = bottomSheetDialog.findViewById(R.id.imgBirthdate);
        ImageView imgClass = bottomSheetDialog.findViewById(R.id.imgClass);

        boolean[] checkArr = new boolean[3];
        for (int i = 0; i < checkArr.length; i++) {
            checkArr[i] = false;
        }
        StudentSorters studentSorters = new StudentSorters();
        List<Student> originalList = new ArrayList<>();
        originalList.addAll(studentAdapter.getStudentList());
        imgName.setOnClickListener(v -> {
            if(checkArr[0]) {
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_sort_name);
                imgName.setImageDrawable(drawable);

                studentSorters.unsortBy(studentSorters.compareByName());
                studentSorters.applySort(studentAdapter.getStudentList());
                // Nếu không còn so sánh nào nữa -> rollback về mảng cũ
                if(studentSorters.comparators.size() == 0) {
                    studentAdapter.setStudentList(originalList);
                    studentAdapter.notifyDataSetChanged();
                }

            } else {
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_sort_name_active);
                imgName.setImageDrawable(drawable);

                studentSorters.sortBy(studentSorters.compareByName());
                studentSorters.applySort(studentAdapter.getStudentList());
            }
            studentAdapter.notifyDataSetChanged();
            checkArr[0] = !checkArr[0];
        });

        imgBirthdate.setOnClickListener(v -> {
            if(checkArr[1] == true) {
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_sort_birthdate);
                imgBirthdate.setImageDrawable(drawable);

                studentSorters.unsortBy(studentSorters.compareByBirthdate());
                studentSorters.applySort(studentAdapter.getStudentList());

                // Nếu không còn so sánh nào nữa -> rollback về mảng cũ
                if(studentSorters.comparators.size() == 0) {
                    studentAdapter.setStudentList(originalList);
                    studentAdapter.notifyDataSetChanged();
                }

            } else {
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_sort_birthdate_active);
                imgBirthdate.setImageDrawable(drawable);

                studentSorters.sortBy(studentSorters.compareByBirthdate());
                studentSorters.applySort(studentAdapter.getStudentList());

            }
            studentAdapter.notifyDataSetChanged();
            checkArr[1] = !checkArr[1];
        });

        imgClass.setOnClickListener(v -> {
            if(checkArr[2] == true) {
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_sort_class);
                imgClass.setImageDrawable(drawable);

                studentSorters.unsortBy(studentSorters.compareByClassName());
                studentSorters.applySort(studentAdapter.getStudentList());
                // Nếu không còn so sánh nào nữa -> rollback về mảng cũ
                if(studentSorters.comparators.size() == 0) {
                    studentAdapter.setStudentList(originalList);
                    studentAdapter.notifyDataSetChanged();
                }

            } else {
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_sort_class_active);
                imgClass.setImageDrawable(drawable);

                studentSorters.sortBy(studentSorters.compareByClassName());
                studentSorters.applySort(studentAdapter.getStudentList());

            }

            studentAdapter.notifyDataSetChanged();
            checkArr[2] = !checkArr[2];
        });



        bottomSheetDialog.show();
    }

    private void showDialogSearch() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.dialog_search);

        FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setPeekHeight(getResources().getDisplayMetrics().heightPixels);
        }

        EditText edtSearch = bottomSheetDialog.findViewById(R.id.edtSearch);
        Button btnSearch = bottomSheetDialog.findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(v -> {
            String name = edtSearch.getText().toString();
            List<Student> searchList = StudentSearch.search(name, studentAdapter.getStudentList());
            studentAdapter.setStudentList(searchList);
            studentAdapter.notifyDataSetChanged();
        });

        bottomSheetDialog.show();
    }

}