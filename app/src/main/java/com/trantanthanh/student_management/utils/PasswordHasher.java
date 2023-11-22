package com.trantanthanh.student_management.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    public static String hashPassword(String password) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        return hashedPassword;
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}