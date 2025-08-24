package service;

import java.util.List;
import model.Enrollment;

public interface EnrollmentService {
    boolean enrollStudent(Enrollment enrollment);          // 選課 (含衝堂檢查)
    boolean dropCourse(int enrollId);                     // 退選
    Enrollment getEnrollmentById(int enrollId);
    List<Enrollment> getAllEnrollments();
    List<Enrollment> getEnrollmentsByStudent(String studentNo);
    List<Enrollment> getEnrollmentsByCourse(int courseId);
    boolean updateEnrollmentScore(int enrollId, Double score);
}

