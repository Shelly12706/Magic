package dao;

import java.util.List;
import model.Course;

public interface CourseDao {
    boolean create(Course course);
    Course findById(int courseId);
    List<Course> findAll();
    boolean update(Course course);
    boolean delete(int courseId);
    List<Course> findByTeacherNo(String teacherNo);
}
