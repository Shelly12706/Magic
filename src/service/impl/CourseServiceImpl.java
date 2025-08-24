package service.impl;

import java.util.List;

import dao.CourseDao;
import dao.impl.CourseDaoImpl;
import model.Course;
import service.CourseService;

public class CourseServiceImpl implements CourseService {
    private CourseDao courseDao = new CourseDaoImpl();

    @Override
    public boolean createCourse(Course course) {
        return courseDao.create(course);
    }

    @Override
    public Course getCourseById(int courseId) {
        return courseDao.findById(courseId);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseDao.findAll();
    }

    @Override
    public boolean updateCourse(Course course) {
        return courseDao.update(course);
    }

    @Override
    public boolean deleteCourse(int courseId) {
        return courseDao.delete(courseId);
    }

    // 🔥 需要在 CourseDao 增加此方法
    @Override
    public List<Course> getCoursesByTeacher(String teacherNo) {
        return courseDao.findByTeacherNo(teacherNo);
    }
}
