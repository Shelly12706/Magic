package service.impl;

import java.util.List;
import dao.EnrollmentDao;
import dao.impl.EnrollmentDaoImpl;
import model.Enrollment;
import model.Course;
import service.EnrollmentService;
import service.CourseService;
import service.impl.CourseServiceImpl;

public class EnrollmentServiceImpl implements EnrollmentService {
    private EnrollmentDao enrollmentDao = new EnrollmentDaoImpl();
    private CourseService courseService = new CourseServiceImpl();

    @Override
    public boolean enrollStudent(Enrollment enrollment) {
        // 1. 取得學生已有選課
        List<Enrollment> currentEnrollments = enrollmentDao.findByStudentNo(enrollment.getStudentNo());

        // 2. 取得要選課的課程資訊
        Course newCourse = courseService.getCourseById(enrollment.getCourseId());
        if (newCourse == null) return false;

        // 3. 衝堂檢查
        for (Enrollment e : currentEnrollments) {
            Course existingCourse = courseService.getCourseById(e.getCourseId());
            if (existingCourse.getDayOfWeek() == newCourse.getDayOfWeek() &&
                existingCourse.getTimeSlot().equals(newCourse.getTimeSlot())) {
                System.out.println("衝堂警告：選課時間與已有課程衝突！");
                return false;
            }
        }

        // 4. 選課成功，寫入 enrollment 表
        return enrollmentDao.create(enrollment);
    }

    @Override
    public boolean dropCourse(int enrollId) {
        return enrollmentDao.delete(enrollId);
    }

    @Override
    public Enrollment getEnrollmentById(int enrollId) {
        return enrollmentDao.findById(enrollId);
    }

    @Override
    public List<Enrollment> getAllEnrollments() {
        return enrollmentDao.findAll();
    }

    @Override
    public List<Enrollment> getEnrollmentsByStudent(String studentNo) {
        return enrollmentDao.findByStudentNo(studentNo);
    }

    @Override
    public List<Enrollment> getEnrollmentsByCourse(int courseId) {
        return enrollmentDao.findByCourseId(courseId);
    }

    @Override
    public boolean updateEnrollmentScore(int enrollId, Double score) {
        Enrollment enrollment = enrollmentDao.findById(enrollId);
        if (enrollment == null) return false;

        enrollment.setScore(score);
        // 自動更新 score_time
        enrollment.setScoreTime(new java.sql.Timestamp(System.currentTimeMillis()));

        return enrollmentDao.update(enrollment);
    }
}
