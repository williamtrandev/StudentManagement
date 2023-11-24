package com.trantanthanh.student_management.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;
import com.trantanthanh.student_management.R;
import com.trantanthanh.student_management.activity.DetailStudentActivity;
import com.trantanthanh.student_management.databinding.StudentItemBinding;
import com.trantanthanh.student_management.databinding.UserItemBinding;
import com.trantanthanh.student_management.model.Student;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder>{

    private List<Student> studentList;
    private Context context;

    public StudentAdapter(List<Student> studentList) {
        this.studentList = studentList;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        StudentItemBinding studentItemBinding = StudentItemBinding.inflate(layoutInflater, parent, false);

        return new StudentAdapter.ViewHolder(studentItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student student = studentList.get(position);
        if(student == null) return;
        holder.tvStudent.setText(student.getName());
        holder.tvClass.setText("Lớp " + student.getClassName());
        holder.tvBirthdate.setText(student.getBirthdate());
        if(student.getGender() == 0) {
            holder.imgGender.setImageResource(R.drawable.ic_baseline_female_24);
        } else {
            holder.imgGender.setImageResource(R.drawable.ic_baseline_male_24);
        }
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailStudentActivity.class);
            intent.putExtra("student", student);
            holder.itemView.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        if(studentList == null) return 0;
        return studentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvStudent, tvClass, tvBirthdate;
        public ImageView imgGender;
        public ShapeableImageView imgStudent;
        public CardView cardView;
        StudentItemBinding studentItemBinding;
        public ViewHolder(StudentItemBinding studentItemBinding) {
            super(studentItemBinding.getRoot());
            this.studentItemBinding = studentItemBinding;
            tvStudent = studentItemBinding.tvStudent;
            tvClass = studentItemBinding.tvClass;
            tvBirthdate = studentItemBinding.tvUserRole;
            imgGender = studentItemBinding.imgGender;
            imgStudent = studentItemBinding.imgStudent;
            cardView = studentItemBinding.cardView;
        }

    }
}
