package com.trantanthanh.student_management.dto;

import com.google.firebase.firestore.DocumentId;
import com.trantanthanh.student_management.model.User;

import java.util.Date;

public class LoginHistoryDTO {
    @DocumentId
    private String id;
    private Date loginTime;
    private User user;

    public LoginHistoryDTO(String id, Date loginTime, User user) {
        this.id = id;
        this.loginTime = loginTime;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
