package com.trantanthanh.student_management.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;

public class User implements Parcelable {
    @DocumentId
    private String id;
    private String avatar;
    private String name;
    private String password;
    private String phone;
    private String role;
    private String status;

    private String birthdate;

    private Uri imgUri;

    public User() {

    }
    public User(String phone, String password) {
        this.password = password;
        this.phone = phone;
    }

    public User(User user) {
        this.id = user.id;
        this.avatar = user.avatar;
        this.name = user.name;
        this.password = user.password;
        this.phone = user.phone;
        this.role = user.role;
        this.status = user.status;
        this.birthdate = user.birthdate;
        this.imgUri = user.imgUri;
    }

    public User(String avatar, String name, String password,
                String phone, String role, String status, String birthdate) {
        this.avatar = avatar;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.birthdate = birthdate;
    }

    protected User(Parcel in) {
        id = in.readString();
        avatar = in.readString();
        name = in.readString();
        password = in.readString();
        phone = in.readString();
        role = in.readString();
        status = in.readString();
        birthdate = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public Uri getImgUri() {
        return imgUri;
    }

    public void setImgUri(Uri imgUri) {
        this.imgUri = imgUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(avatar);
        parcel.writeString(name);
        parcel.writeString(password);
        parcel.writeString(phone);
        parcel.writeString(role);
        parcel.writeString(status);
        parcel.writeString(birthdate);
    }
}
