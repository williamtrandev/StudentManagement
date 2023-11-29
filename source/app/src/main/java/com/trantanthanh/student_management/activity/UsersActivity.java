package com.trantanthanh.student_management.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.trantanthanh.student_management.adapter.UserAdapter;
import com.trantanthanh.student_management.component.LoadingDialog;
import com.trantanthanh.student_management.databinding.ActivityUsersBinding;
import com.trantanthanh.student_management.firestore.UserFirestore;
import com.trantanthanh.student_management.model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class UsersActivity extends AppCompatActivity {
    private ActivityUsersBinding binding;
    private UserAdapter userAdapter;
    private UserFirestore userFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        userFirestore = new UserFirestore();
        binding.toolbar.iconBack.setOnClickListener(v -> {
            finish();
        });
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userRole = sharedPreferences.getString("userRole", "defaultRole"); // defaultRole là giá trị mặc định nếu không tìm thấy giá trị trong SharedPreferences

        binding.toolbar.settingToolbar.setOnClickListener(v -> {
            if (userRole.equals("Admin")) {
                openAddDialog();
            } else {
                new MaterialAlertDialogBuilder(UsersActivity.this)
                        .setTitle("Cảnh báo")
                        .setMessage("Bạn không có quyền truy cập tính năng này")
                        .setNeutralButton("Xác nhận", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
        });

        userAdapter = new UserAdapter(null);
        userAdapter.setContext(this);
        userAdapter.setUserFirestore(userFirestore);
        binding.rcvUsers.setAdapter(userAdapter);
        binding.rcvUsers.setLayoutManager(new LinearLayoutManager(this));
        UserFirestore userFirestore = new UserFirestore();
        CompletableFuture<List<User>> result = userFirestore.getAll();
        result.thenAccept(userList -> {
            // Cập nhật dữ liệu mới lấy được vào RecyclerView Adapter
            runOnUiThread(() -> {
                userAdapter.setUserList(userList);
                binding.rcvUsers.getAdapter().notifyDataSetChanged();
            });
        }).exceptionally(ex -> {
            // Xử lý ngoại lệ nếu có lỗi xảy ra khi lấy dữ liệu từ Firestore
            ex.printStackTrace();
            return null;
        });

        CollectionReference userRef = FirebaseFirestore.getInstance().collection("user");
        userRef.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                // Xử lý lỗi khi lắng nghe
                return;
            }
            if (querySnapshot != null) {
                List<User> updatedUserList = new ArrayList<>();
                for (QueryDocumentSnapshot document : querySnapshot) {
                    User updatedUser = document.toObject(User.class);
                    updatedUserList.add(updatedUser);
                }
                userAdapter.setUserList(updatedUserList);
                binding.rcvUsers.getAdapter().notifyDataSetChanged();
            }
        });
    }

    private void openAddDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.dialog_add_user);

        User user = new User();
        FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setPeekHeight(getResources().getDisplayMetrics().heightPixels);
        }
        Button btnSave = bottomSheetDialog.findViewById(R.id.btnDetail);
        EditText edtName = bottomSheetDialog.findViewById(R.id.edtNameAdd);
        EditText edtPhone = bottomSheetDialog.findViewById(R.id.edtPhoneAdd);
        EditText edtBirthdate = bottomSheetDialog.findViewById(R.id.edtBirthdateAdd);
        edtBirthdate.setOnClickListener(v -> {
            AtomicReference<Integer> year = new AtomicReference<>();
            AtomicReference<Integer> month = new AtomicReference<>();
            AtomicReference<Integer> day = new AtomicReference<>();

            Calendar calendar = Calendar.getInstance();
            year.set(calendar.get(Calendar.YEAR));
            month.set(calendar.get(Calendar.MONTH));
            day.set(calendar.get(Calendar.DAY_OF_MONTH));

            DatePickerDialog datePickerDialog = new DatePickerDialog(UsersActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        year.set(selectedYear);
                        month.set(selectedMonth);
                        day.set(selectedDay);

                        Log.d("SelectedDate", "Year: " + year + ", Month: " + month + ", Day: " + day);

                        // Cập nhật EditText khi chọn ngày
                        if (day.get() != null && month.get() != null && year.get() != null) {
                            String selectedDate = day.get() + "/" + (month.get() + 1) + "/" + year.get(); // Tháng trong Calendar bắt đầu từ 0
                            edtBirthdate.setText(selectedDate);
                            user.setBirthdate(selectedDate);
                        }
                    }, year.get(), month.get(), day.get());

            datePickerDialog.setOnCancelListener(dialogInterface -> {
                year.set(null);
                month.set(null);
                day.set(null);
            });

            datePickerDialog.show();
        });

        RadioGroup radioGroup = bottomSheetDialog.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Lấy ID của RadioButton được chọn
            RadioButton selectedRadioButton = bottomSheetDialog.findViewById(checkedId);
            if (selectedRadioButton != null) {
                String selectedOption = selectedRadioButton.getText().toString();
                Log.d("SelectedOption", selectedOption);
                user.setRole(selectedOption);
            }
        });
        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString();
            String phone = edtPhone.getText().toString();
            user.setName(name);
            user.setPhone(phone);
            user.setPassword(phone);
            user.setStatus("Normal");
            LoadingDialog loadingDialog = new LoadingDialog(UsersActivity.this);
            loadingDialog.show();

            CompletableFuture<Boolean> result = userFirestore.createNewUser(user);
            result.thenAccept(isSuccess -> {
                if(isSuccess) {
                    bottomSheetDialog.dismiss();
                    loadingDialog.dismiss();
                    Toast.makeText(UsersActivity.this, "Thêm thành công", Toast.LENGTH_LONG).show();
                } else {
                    bottomSheetDialog.dismiss();
                    loadingDialog.dismiss();
                    Toast.makeText(UsersActivity.this, "Thêm thất bại", Toast.LENGTH_LONG).show();
                }
            });
        });

        bottomSheetDialog.show();
    }
}