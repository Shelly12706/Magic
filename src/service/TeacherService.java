package service;

import java.util.List;
import model.Teacher;

public interface TeacherService {
    boolean createTeacher(Teacher teacher);
    Teacher getTeacherByNo(String teacherNo);
    List<Teacher> getAllTeachers();
    boolean updateTeacher(Teacher teacher);
    boolean deleteTeacher(String teacherNo);
    Teacher login(String username, String password);
}
