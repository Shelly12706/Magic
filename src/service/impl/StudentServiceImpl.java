package service.impl;

import java.util.List;
import dao.StudentDao;
import dao.impl.StudentDaoImpl;
import model.Student;
import service.StudentService;
import util.PasswordUtil;

public class StudentServiceImpl implements StudentService {
    private final StudentDao studentDao = new StudentDaoImpl();

    @Override
    public boolean createStudent(Student student) {
        return studentDao.create(student);
    }

    @Override
    public Student getStudentByNo(String studentNo) {
        return studentDao.findByNo(studentNo);
    }

    @Override
    public Student getStudentByUsername(String username) {
        return studentDao.findByUsername(username);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentDao.findAll();
    }

    @Override
    public boolean updateStudent(Student student) {
        return studentDao.update(student);
    }

    @Override
    public boolean deleteStudent(String studentNo) {
        return studentDao.delete(studentNo);
    }

    @Override
    public Student login(String username, String password) {
        Student student = studentDao.findByUsername(username);
        if (student != null && PasswordUtil.verify(password, student.getPassword())) {
            return student;
        }
        return null;
    }

    @Override
    public int getNextId() {
        return studentDao.getNextId(); // 取得 student 表最大流水號 +1
    }
}
