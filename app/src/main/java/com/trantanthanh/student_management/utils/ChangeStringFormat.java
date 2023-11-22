package com.trantanthanh.student_management.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChangeStringFormat {
    public static String formatToVietnameseDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", new Locale("vi", "VN"));
        return sdf.format(date);
    }

    public static String formatToVietnameseRole(String role) {
        if(role.equalsIgnoreCase("Admin")) {
            return "Admin";
        } else if(role.equalsIgnoreCase("Manager")) {
            return "Quản lý";
        } else {
            return "Nhân viên";
        }
    }
}
