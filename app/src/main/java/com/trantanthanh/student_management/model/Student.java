package com.trantanthanh.student_management.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;

import java.util.List;

public class Student implements Parcelable {
    @DocumentId
    private String id;
    private String name;
    private String birthdate;
    private int gender;
    private int className;
    private List<Certificate> certificateList;

    public Student() {}

    public Student(String name, String birthdate, int gender, int className) {
        this.name = name;
        this.birthdate = birthdate;
        this.gender = gender;
        this.className = className;
    }

    public Student(String id, String name, String birthdate, int gender, int className, List<Certificate> certificateList) {
        this.id = id;
        this.name = name;
        this.birthdate = birthdate;
        this.gender = gender;
        this.className = className;
        this.certificateList = certificateList;
    }

    public Student(Student student) {
        this.id = student.id;
        this.name = student.name;
        this.birthdate = student.birthdate;
        this.gender = student.gender;
        this.className = student.className;
        this.certificateList = student.certificateList;
    }

    protected Student(Parcel in) {
        id = in.readString();
        name = in.readString();
        birthdate = in.readString();
        gender = in.readInt();
        className = in.readInt();
    }

    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getClassName() {
        return className;
    }

    public void setClassName(int className) {
        this.className = className;
    }

    public List<Certificate> getCertificateList() {
        return certificateList;
    }

    public void setCertificateList(List<Certificate> certificateList) {
        this.certificateList = certificateList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int i) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(birthdate);
        dest.writeInt(gender);
        dest.writeInt(className);
    }
}
