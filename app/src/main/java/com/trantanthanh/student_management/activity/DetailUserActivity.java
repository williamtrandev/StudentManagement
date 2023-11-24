package com.trantanthanh.student_management.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;
import com.trantanthanh.student_management.databinding.ActivityDetailUserBinding;
import com.trantanthanh.student_management.firestore.UserFirestore;
import com.trantanthanh.student_management.model.User;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class DetailUserActivity extends AppCompatActivity {

    private ActivityDetailUserBinding binding;
    private Uri imgURI;

    private User userUpdate;
    private Map<String, String> stateMap;
    private Map<String, String> stateMapVi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UserFirestore userFirestore = new UserFirestore();
        binding.toolbar.iconBack.setOnClickListener(v -> {
            finish();
        });
        stateMap = new HashMap<>();
        stateMap.put("Normal", "Block");
        stateMap.put("Block", "Normal");
        stateMapVi = new HashMap<>();
        stateMapVi.put("Mở khóa", "Khóa");
        stateMapVi.put("Khóa", "Mở khóa");
        Intent intent = getIntent();
        User user = intent.getParcelableExtra("user");
        userUpdate = new User(user);
        binding.edtNameDetail.setText(user.getName());
        binding.edtBirthdate.setText(user.getBirthdate());
        binding.edtPhone.setText(user.getPhone());
        binding.edtStatus.setText(user.getStatus());
        if(user.getAvatar() != null && user.getAvatar() != "") {
            Picasso.get().load(user.getAvatar()).into(binding.avatarUser);
        }
        String btnBlockUnBlock = user.getStatus().equalsIgnoreCase("Normal")
                                    ? "Khóa" : "Mở khóa";
        binding.btnBlock.setText(btnBlockUnBlock);
        AtomicReference<String> preState = new AtomicReference<>(user.getStatus());
        AtomicReference<String> preStateVi = new AtomicReference<>(btnBlockUnBlock);

        binding.btnBlock.setOnClickListener(v -> {
            CompletableFuture<Boolean> result = userFirestore.updateStatus(user.getId(), stateMap.get(preState.get()));
            result.thenAccept(isSuccess -> {
                if(isSuccess) {
                    // Cập nhật UI và giá trị trong AtomicReference
                    binding.edtStatus.setText(stateMap.get(preState.get()));
                    binding.btnBlock.setText(stateMapVi.get(preStateVi.get()));


                    // Hiển thị thông báo thành công
                    new MaterialAlertDialogBuilder(DetailUserActivity.this)
                            .setTitle("Thành công")
                            .setMessage(preStateVi.get() + " thành công")
                            .setNeutralButton("Xác nhận", (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .show();

                    preState.set(stateMap.get(preState.get()));
                    preStateVi.set(stateMapVi.get(preStateVi.get()));
                } else {
                    // Hiển thị thông báo thất bại
                    new MaterialAlertDialogBuilder(DetailUserActivity.this)
                            .setTitle("Thất bại")
                            .setMessage(preStateVi.get() + " thất bại")
                            .setNeutralButton("Xác nhận", (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .show();
                }
            });
        });

        ActivityResultLauncher<String> pickImagesFromGallery = registerForActivityResult(new ActivityResultContracts.GetContent()
                , result -> {
                    if (result != null){
                        imgURI = result;
                        binding.avatarUser.setImageURI(result);
                        userUpdate.setImgUri(result);
                    }

                });
        binding.avatarUser.setOnClickListener(v -> {
            pickImagesFromGallery.launch("image/*");
        });


        binding.edtNameDetail.setOnLongClickListener(v -> {
            binding.edtNameDetail.setFocusable(true);
            binding.edtNameDetail.setFocusableInTouchMode(true);
            binding.edtNameDetail.requestFocus();
            return true;
        });

        binding.edtBirthdate.setOnLongClickListener(v -> {
            AtomicReference<Integer> year = new AtomicReference<>();
            AtomicReference<Integer> month = new AtomicReference<>();
            AtomicReference<Integer> day = new AtomicReference<>();

            Calendar calendar = Calendar.getInstance();
            year.set(calendar.get(Calendar.YEAR));
            month.set(calendar.get(Calendar.MONTH));
            day.set(calendar.get(Calendar.DAY_OF_MONTH));

            DatePickerDialog datePickerDialog = new DatePickerDialog(DetailUserActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        year.set(selectedYear);
                        month.set(selectedMonth);
                        day.set(selectedDay);

                        Log.d("SelectedDate", "Year: " + year + ", Month: " + month + ", Day: " + day);

                        // Cập nhật EditText khi chọn ngày
                        if (day.get() != null && month.get() != null && year.get() != null) {
                            String selectedDate = day.get() + "/" + (month.get() + 1) + "/" + year.get(); // Tháng trong Calendar bắt đầu từ 0
                            binding.edtBirthdate.setText(selectedDate);
                            userUpdate.setBirthdate(selectedDate);
                        }
                    }, year.get(), month.get(), day.get());

            datePickerDialog.setOnCancelListener(dialogInterface -> {
                year.set(null);
                month.set(null);
                day.set(null);
            });

            datePickerDialog.show();
            return true;
        });

        binding.edtPhone.setOnLongClickListener(v -> {
            binding.edtPhone.setFocusable(true);
            binding.edtPhone.setFocusableInTouchMode(true);
            binding.edtPhone.requestFocus();
            return true;
        });

        binding.btnUpdateInfo.setOnClickListener(v -> {
            String name = binding.edtNameDetail.getText().toString();
            String birthday = binding.edtBirthdate.getText().toString();
            String phone = binding.edtPhone.getText().toString();
            userUpdate.setName(name);
            userUpdate.setBirthdate(birthday);
            userUpdate.setPhone(phone);
            CompletableFuture<Boolean> result = userFirestore.updateUser(userUpdate);
            result.thenAccept(isSuccess -> {
                if(isSuccess) {
                    new MaterialAlertDialogBuilder(DetailUserActivity.this)
                            .setTitle("Thành công")
                            .setMessage("Cập nhật thông tin thành công")
                            .setNeutralButton("Ok", (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .show();
                } else {
                    new MaterialAlertDialogBuilder(DetailUserActivity.this)
                            .setTitle("Thất bại")
                            .setMessage("Cập nhật thông tin không thành công")
                            .setNeutralButton("Xác nhận", (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .show();
                }
            });
        });
    }


}