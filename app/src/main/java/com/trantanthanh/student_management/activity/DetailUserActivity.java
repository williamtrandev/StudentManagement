package com.trantanthanh.student_management.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;

import com.trantanthanh.student_management.databinding.ActivityDetailUserBinding;
import com.trantanthanh.student_management.model.User;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class DetailUserActivity extends AppCompatActivity {

    private ActivityDetailUserBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.iconBack.setOnClickListener(v -> {
            finish();
        });
        Intent intent = getIntent();
        User user = intent.getParcelableExtra("user");
        binding.edtNameDetail.setText(user.getName());
        binding.edtBirthdate.setText(user.getBirthdate());
        binding.edtPhone.setText(user.getPhone());
        binding.edtStatus.setText(user.getStatus());

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

        });
    }
}