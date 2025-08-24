package service;

import java.util.List;
import model.Student;

public interface StudentService {
    boolean createStudent(Student student);
    Student getStudentByNo(String studentNo);
    Student getStudentByUsername(String username);
    List<Student> getAllStudents();
    boolean updateStudent(Student student);
    boolean deleteStudent(String studentNo);
    Student login(String username, String password);
    int getNextId();
}
