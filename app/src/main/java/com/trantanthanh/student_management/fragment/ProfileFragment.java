package com.trantanthanh.student_management.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.trantanthanh.student_management.activity.DetailUserActivity;
import com.trantanthanh.student_management.activity.LoginActivity;
import com.trantanthanh.student_management.databinding.FragmentHomeBinding;
import com.trantanthanh.student_management.databinding.FragmentProfileBinding;
import com.trantanthanh.student_management.firestore.UserFirestore;
import com.trantanthanh.student_management.model.User;

import java.util.Calendar;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    private User userUpdate;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        UserFirestore userFirestore = new UserFirestore();
        Bundle args = getArguments();
        if(args != null) {
            User user = args.getParcelable("user");
            userUpdate = new User(user);
            binding.edtNameDetail.setText(user.getName());
            binding.edtBirthdate.setText(user.getBirthdate());
            binding.edtPhone.setText(user.getPhone());
            binding.edtStatus.setText(user.getStatus());
            if(user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                Picasso.get().load(user.getAvatar()).into(binding.avatarUser);
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
                    binding.edtNameDetail.setText(updatedUser.getName());
                    binding.edtBirthdate.setText(updatedUser.getBirthdate());
                    binding.edtPhone.setText(updatedUser.getPhone());
                    binding.edtStatus.setText(updatedUser.getStatus());
                    Picasso.get().load(updatedUser.getAvatar()).into(binding.avatarUser);

                }
            });
        }
        ActivityResultLauncher<String> pickImagesFromGallery = registerForActivityResult(new ActivityResultContracts.GetContent()
                , result -> {
                    if (result != null){
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

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (dialogV, selectedYear, selectedMonth, selectedDay) -> {
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

        binding.btnUpdateProfile.setOnClickListener(v -> {
            String name = binding.edtNameDetail.getText().toString();
            String birthday = binding.edtBirthdate.getText().toString();
            String phone = binding.edtPhone.getText().toString();
            userUpdate.setName(name);
            userUpdate.setBirthdate(birthday);
            userUpdate.setPhone(phone);
            CompletableFuture<Boolean> result = userFirestore.updateUser(userUpdate);
            result.thenAccept(isSuccess -> {
                if(isSuccess) {
                    new MaterialAlertDialogBuilder(getContext())
                            .setTitle("Thành công")
                            .setMessage("Cập nhật thông tin thành công")
                            .setNeutralButton("Ok", (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .show();
                } else {
                    new MaterialAlertDialogBuilder(getContext())
                            .setTitle("Thất bại")
                            .setMessage("Cập nhật thông tin không thành công")
                            .setNeutralButton("Xác nhận", (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .show();
                }
            });
        });

        binding.btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });
        return view;
    }


}