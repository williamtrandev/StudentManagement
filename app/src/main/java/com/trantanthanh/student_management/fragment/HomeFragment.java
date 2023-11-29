package com.trantanthanh.student_management.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.trantanthanh.student_management.R;
import com.trantanthanh.student_management.activity.LoginHistoryActivity;
import com.trantanthanh.student_management.activity.StudentActivity;
import com.trantanthanh.student_management.activity.UsersActivity;
import com.trantanthanh.student_management.databinding.FragmentHomeBinding;
import com.trantanthanh.student_management.model.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Bundle args = getArguments();
        User user = args.getParcelable("user");
        binding.tvName.setText(user.getName());
        binding.tvRole.setText(user.getRole());
        if(user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            Picasso.get().load(user.getAvatar()).into(binding.shapeableImageView);
        }

        DocumentReference userDoc = FirebaseFirestore.getInstance()
                .collection("user").document(user.getId());
        userDoc.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                // Xử lý lỗi khi lắng nghe
                return;
            }
            if (querySnapshot != null && querySnapshot.exists()) {
                User updatedUser = querySnapshot.toObject(User.class);
                binding.tvName.setText(updatedUser.getName());
                Picasso.get().load(updatedUser.getAvatar()).into(binding.shapeableImageView);
                binding.tvRole.setText(updatedUser.getRole());
            }
        });

        binding.cvUsers.setOnClickListener(v -> {
            if(user.getRole().equalsIgnoreCase("Admin")) {
                Intent intent = new Intent(getContext(), UsersActivity.class);
                startActivity(intent);
            } else {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Bạn không có quyền truy cập chức năng này")
                        .setMessage("Chỉ có Admin mới có quyền!")
                        .setNeutralButton("Xác nhận", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
        });

        binding.cvHistory.setOnClickListener(v -> {
            if(user.getRole().equalsIgnoreCase("Admin")) {
                Intent intent = new Intent(getContext(), LoginHistoryActivity.class);
                startActivity(intent);
            } else {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Bạn không có quyền truy cập chức năng này")
                        .setMessage("Chỉ có Admin mới có quyền!")
                        .setNeutralButton("Xác nhận", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }

        });

        binding.cvStudent.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), StudentActivity.class);
            startActivity(intent);
        });


        return view;
    }
}