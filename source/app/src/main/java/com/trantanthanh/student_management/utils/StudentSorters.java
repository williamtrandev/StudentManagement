package com.trantanthanh.student_management.utils;

import com.trantanthanh.student_management.model.Student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StudentSorters {
    public List<Comparator<Student>> comparators = new ArrayList<>();
    public Comparator<Student> compareByName() {
        return Comparator.comparing(Student::getName);
    }
    public Comparator<Student> compareByClassName() {
        return Comparator.comparing(Student::getClassName);
    }
    public static Comparator<Student> compareByBirthdate() {
        return Comparator.comparing(student -> DateConvert.parseDate(student.getBirthdate()));
    }
    public void sortBy(Comparator<Student> comparator) {
        comparators.add(comparator);
    }
    public void unsortBy(Comparator<Student> comparator) {
        comparators.remove(comparator);
    }

    public void clearSort() {
        comparators.clear();
    }

    public void applySort(List<Student> studentList) {
        for(Comparator<Student> comparator : comparators) {
            Collections.sort(studentList, comparator);
        }
    }
}
