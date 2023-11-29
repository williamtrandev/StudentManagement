package com.trantanthanh.student_management.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.trantanthanh.student_management.activity.LoginActivity;
import com.trantanthanh.student_management.databinding.CertificateItemBinding;
import com.trantanthanh.student_management.databinding.StudentItemBinding;
import com.trantanthanh.student_management.firestore.StudentFirestore;
import com.trantanthanh.student_management.model.Certificate;

import java.util.List;

public class CertificateAdapter extends RecyclerView.Adapter<CertificateAdapter.ViewHolder>{

    private List<Certificate> certificateList;

    private Context context;

    private StudentFirestore studentFirestore;

    private String studentId;

    public CertificateAdapter(List<Certificate> certificateList) {
        this.certificateList = certificateList;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setStudentFirestore(StudentFirestore studentFirestore) {
        this.studentFirestore = studentFirestore;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        CertificateItemBinding certificateItemBinding = CertificateItemBinding.inflate(layoutInflater, parent, false);

        return new CertificateAdapter.ViewHolder(certificateItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Certificate certificate = certificateList.get(position);
        if(certificate == null) return;
        holder.tvNameCert.setText("Loại chứng chỉ: " + certificate.getName());
        holder.tvIssueDate.setText("Ngày cấp: " + certificate.getIssueDate());
        holder.tvExpiryDate.setText("Ngày hết hạn: " + certificate.getExpiryDate());
        holder.tvIssueBy.setText("Đơn vị cấp: " + certificate.getIssuedBy());
        holder.imgDelete.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(holder.itemView.getContext())
                    .setTitle("Xác nhận xóa chứng chỉ")
                    .setMessage("Xóa chứng chỉ này sẽ không thể hoàn tác")
                    .setNeutralButton("Xác nhận", (dialog, which) -> {
                        studentFirestore.deleteCertificate(certificate.getId(), studentId);
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        if(certificateList == null) return 0;
        return certificateList.size();
    }

    public void setCertificateList(List<Certificate> certificateList) {
        this.certificateList = certificateList;
    }

    public List<Certificate> getCertificateList() {
        return this.certificateList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNameCert, tvIssueDate, tvExpiryDate, tvIssueBy;
        public ImageView imgDelete;
        CertificateItemBinding certificateItemBinding;
        public ViewHolder(CertificateItemBinding certificateItemBinding) {
            super(certificateItemBinding.getRoot());
            this.certificateItemBinding = certificateItemBinding;
            tvNameCert = certificateItemBinding.tvNameCert;
            tvIssueDate = certificateItemBinding.tvIssueDate;
            tvExpiryDate = certificateItemBinding.tvExpiryDate;
            tvIssueBy = certificateItemBinding.tvIssueBy;
            imgDelete = certificateItemBinding.imgDelete;
        }

    }
}
