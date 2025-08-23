package dao;

import java.util.List;
import model.Enrollment;

public interface EnrollmentDao {
    boolean create(Enrollment enrollment);
    Enrollment findById(int enrollId);
    List<Enrollment> findAll();
    List<Enrollment> findByStudentNo(String studentNo);
    List<Enrollment> findByCourseId(int courseId);
    boolean update(Enrollment enrollment);
    boolean delete(int enrollId);
}
