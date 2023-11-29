package com.trantanthanh.student_management.utils;

import com.trantanthanh.student_management.model.Student;

import java.util.List;
import java.util.stream.Collectors;

public class StudentSearch {
    public static List<Student> search(String name, List<Student> studentList) {
        return studentList.stream()
                .filter(student -> student.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }
}
