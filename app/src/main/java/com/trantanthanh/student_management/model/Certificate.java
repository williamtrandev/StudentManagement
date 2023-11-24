package com.trantanthanh.student_management.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;

public class Certificate implements Parcelable {
    @DocumentId
    private String id;
    private String name;
    private String issueDate;
    private String expiryDate;
    private String issuedBy;

    public Certificate() {}

    public Certificate(String name, String issueDate, String expiryDate, String issuedBy) {
        this.name = name;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.issuedBy = issuedBy;
    }

    public Certificate(String id, String name, String issueDate, String expiryDate, String issuedBy) {
        this.id = id;
        this.name = name;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.issuedBy = issuedBy;
    }

    protected Certificate(Parcel in) {
        id = in.readString();
        name = in.readString();
        issueDate = in.readString();
        expiryDate = in.readString();
        issuedBy = in.readString();
    }

    public static final Creator<Certificate> CREATOR = new Creator<Certificate>() {
        @Override
        public Certificate createFromParcel(Parcel in) {
            return new Certificate(in);
        }

        @Override
        public Certificate[] newArray(int size) {
            return new Certificate[size];
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

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int i) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(issueDate);
        dest.writeString(expiryDate);
        dest.writeString(issuedBy);
    }
}
