package com.trantanthanh.student_management.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.trantanthanh.student_management.R;
import com.trantanthanh.student_management.adapter.CertificateAdapter;
import com.trantanthanh.student_management.component.LoadingDialog;
import com.trantanthanh.student_management.databinding.ActivityCertificatesBinding;
import com.trantanthanh.student_management.firestore.StudentFirestore;
import com.trantanthanh.student_management.model.Certificate;
import com.trantanthanh.student_management.model.Student;
import com.trantanthanh.student_management.utils.RealPath;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CertificatesActivity extends AppCompatActivity {

    private ActivityCertificatesBinding binding;

    private StudentFirestore studentFirestore;

    private CertificateAdapter certificateAdapter;

    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCertificatesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        studentFirestore = new StudentFirestore();
        binding.toolbar.iconBack.setOnClickListener(v -> {
            finish();
        });
        Intent intent = getIntent();

        studentId = intent.getStringExtra("studentId");

        certificateAdapter = new CertificateAdapter(null);
        certificateAdapter.setStudentFirestore(studentFirestore);
        certificateAdapter.setStudentId(studentId);
        binding.rcvCerts.setAdapter(certificateAdapter);
        binding.rcvCerts.setLayoutManager(new LinearLayoutManager(this));
        StudentFirestore studentFirestore = new StudentFirestore();
        CompletableFuture<List<Certificate>> result = studentFirestore.getAllCertificate(studentId);
        result.thenAccept(certificateList -> {
            // Cập nhật dữ liệu mới lấy được vào RecyclerView Adapter
            runOnUiThread(() -> {
                certificateAdapter.setCertificateList(certificateList);
                binding.rcvCerts.getAdapter().notifyDataSetChanged();
            });
        });
        CollectionReference certificateRef = FirebaseFirestore.getInstance()
                                    .collection("student").document(studentId)
                                    .collection("certificateList");
        certificateRef.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                // Xử lý lỗi khi lắng nghe
                return;
            }
            if (querySnapshot != null) {
                List<Certificate> certificateList = new ArrayList<>();
                for (QueryDocumentSnapshot document : querySnapshot) {
                    Certificate certificate = document.toObject(Certificate.class);
                    certificateList.add(certificate);
                }
                certificateAdapter.setCertificateList(certificateList);
                certificateAdapter.notifyDataSetChanged();
            }
        });

        binding.toolbar.settingToolbar.setOnClickListener(v -> {
//            showDialogAdd();
            showPopupMenu(v);
        });
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
                            String filePath = RealPath.getRealPath(CertificatesActivity.this, uri);
                            CompletableFuture<Boolean> check = studentFirestore.insertCertificateByFile(filePath, studentId);
                            LoadingDialog loadingDialog = new LoadingDialog(this);
                            loadingDialog.show();
                            // Xử lý kết quả khi hoàn thành
                            check.thenAccept(isSuccess -> {
                                if(isSuccess) {
                                    loadingDialog.dismiss();
                                    Toast.makeText(CertificatesActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    loadingDialog.dismiss();
                                    Toast.makeText(CertificatesActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            });

    private void pickExcelFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("*/*");
//        String[] mimeTypes = {"application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI)
        pickFileLauncher.launch(intent);
    }
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.options_menu_certificate, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.add_certificate_by_file) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!Environment.isExternalStorageManager()) {
                        Log.i("=== IS EXTERNALSTORAGEMANAGER ===", "TRUE");
                        // Request the MANAGE_EXTERNAL_STORAGE permission
                        Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        startActivity(intent);
                    }
                }
                pickExcelFile();
            } else if(item.getItemId() == R.id.add_certificate) {
                showDialogAdd();
            } else if(item.getItemId() == R.id.export_certificate) {
                boolean isSuccess = studentFirestore.exportCertificate(certificateAdapter.getCertificateList());
                if(isSuccess) {
                    Toast.makeText(CertificatesActivity.this, "Xuất file thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CertificatesActivity.this, "Xuất file thất bại", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        });

        popupMenu.show();
    }

    private void showDialogAdd() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.dialog_add_certificate);

        FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setPeekHeight(getResources().getDisplayMetrics().heightPixels);
        }
        Certificate certificate = new Certificate();
        Button btnAddCert = bottomSheetDialog.findViewById(R.id.btnAddCert);
        RadioGroup rgName = bottomSheetDialog.findViewById(R.id.rgName);
        EditText edtIssueDate = bottomSheetDialog.findViewById(R.id.edtIssueDate);
        EditText edtExpiryDate = bottomSheetDialog.findViewById(R.id.edtExpiryDate);
        EditText edtIssuedBy = bottomSheetDialog.findViewById(R.id.edtIssuedBy);
        rgName.setOnCheckedChangeListener((ground, checkedId) -> {
            if(checkedId != -1) {
                RadioButton radioButton = bottomSheetDialog.findViewById(checkedId);
                if(radioButton != null) {
                    String selectedValue = radioButton.getText().toString();
                    certificate.setName(selectedValue);
                }
            }
        });


        btnAddCert.setOnClickListener(v -> {
            certificate.setIssueDate(edtIssueDate.getText().toString());
            certificate.setExpiryDate(edtExpiryDate.getText().toString());
            certificate.setIssuedBy(edtIssuedBy.getText().toString());
            CompletableFuture<Boolean> result = studentFirestore.insertCertificate(certificate, studentId);
            LoadingDialog loadingDialog = new LoadingDialog(CertificatesActivity.this);
            loadingDialog.show();
            result.thenAccept(isSuccess -> {
                if(isSuccess) {
                    loadingDialog.dismiss();
                    Toast.makeText(CertificatesActivity.this, "Thêm thành công", Toast.LENGTH_LONG).show();
                } else {
                    loadingDialog.dismiss();
                    Toast.makeText(CertificatesActivity.this, "Thêm thất bại", Toast.LENGTH_LONG).show();
                }
            });
        });
        bottomSheetDialog.show();
    }
}