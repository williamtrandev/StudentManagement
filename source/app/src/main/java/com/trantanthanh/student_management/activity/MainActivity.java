package com.trantanthanh.student_management.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.trantanthanh.student_management.R;
import com.trantanthanh.student_management.databinding.ActivityMainBinding;
import com.trantanthanh.student_management.fragment.HomeFragment;
import com.trantanthanh.student_management.fragment.ProfileFragment;
import com.trantanthanh.student_management.model.User;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Lấy user gửi từ login activity gửi qua
        Intent intent = getIntent();
        User user = intent.getParcelableExtra("user");

        // Home Fragment
        Bundle homeBundle = new Bundle();
        homeBundle.putParcelable("user", user);
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(homeBundle);

        // Profile Fragment
        Bundle profileBundle = new Bundle();
        profileBundle.putParcelable("user", user);
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setArguments(profileBundle);


        // Hiển thị home đầu tiên
        replaceFragment(homeFragment);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                replaceFragment(homeFragment);
            } else {
                replaceFragment(profileFragment);
            }
            return true;
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}