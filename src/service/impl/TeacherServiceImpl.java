package service.impl;

import java.util.List;
import dao.TeacherDao;
import dao.impl.TeacherDaoImpl;
import model.Teacher;
import service.TeacherService;
import util.PasswordUtil;

public class TeacherServiceImpl implements TeacherService {
    private TeacherDao teacherDao = new TeacherDaoImpl();

    @Override
    public boolean createTeacher(Teacher teacher) {
        // 註冊時記得把明碼轉成 hash 再存
        teacher.setPassword(PasswordUtil.hash(teacher.getPassword()));
        return teacherDao.create(teacher);
    }

    @Override
    public Teacher getTeacherByNo(String teacherNo) {
        return teacherDao.findByNo(teacherNo);
    }

    @Override
    public List<Teacher> getAllTeachers() {
        return teacherDao.findAll();
    }

    @Override
    public boolean updateTeacher(Teacher teacher) {
        // 更新密碼時一樣要存 hash
        teacher.setPassword(PasswordUtil.hash(teacher.getPassword()));
        return teacherDao.update(teacher);
    }

    @Override
    public boolean deleteTeacher(String teacherNo) {
        return teacherDao.delete(teacherNo);
    }

    @Override
    public Teacher login(String username, String password) {
        Teacher teacher = teacherDao.findByUsername(username);
        if (teacher != null) {
            // 用 PasswordUtil 驗證輸入密碼與 DB hash
            if (PasswordUtil.verify(password, teacher.getPassword())) {
                return teacher;
            }
        }
        return null;
    }
}
