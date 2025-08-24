package dao;

import java.util.List;
import model.Student;

public interface StudentDao {
	boolean create(Student student);
    Student findByNo(String studentNo);
    Student findByUsername(String username);
    List<Student> findAll();
    boolean update(Student student);
    boolean delete(String studentNo);

    int getNextId();
}
