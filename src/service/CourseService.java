package service;

import java.util.List;
import model.Course;

public interface CourseService {
    boolean createCourse(Course course);
    Course getCourseById(int courseId);
    List<Course> getAllCourses();
    boolean updateCourse(Course course);
    boolean deleteCourse(int courseId);

    
    List<Course> getCoursesByTeacher(String teacherNo);
}


