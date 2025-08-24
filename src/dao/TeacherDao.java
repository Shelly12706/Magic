package dao;

import java.util.List;
import model.Teacher;

public interface TeacherDao {
    boolean create(Teacher teacher);
    Teacher findByNo(String teacherNo);
    List<Teacher> findAll();
    boolean update(Teacher teacher);
    boolean delete(String teacherNo);
    Teacher findByUsername(String username);
}
