package com.trantanthanh.student_management.model;

import com.google.firebase.firestore.DocumentId;

import java.util.Date;

public class LoginHistory {
    @DocumentId
    private String id;
    private Date loginTime;

    private String userId;

    public LoginHistory(Date loginTime, String userId) {
        this.loginTime = loginTime;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
