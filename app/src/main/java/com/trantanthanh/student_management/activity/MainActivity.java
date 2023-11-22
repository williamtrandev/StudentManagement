package com.trantanthanh.student_management.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.trantanthanh.student_management.R;
import com.trantanthanh.student_management.databinding.ActivityMainBinding;
import com.trantanthanh.student_management.fragment.AddFragment;
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

        // Hiển thị home đầu tiên
        replaceFragment(homeFragment);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                replaceFragment(homeFragment);
            } else {
                replaceFragment(new ProfileFragment());
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