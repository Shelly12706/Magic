package dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import dao.CourseDao;
import model.Course;
import util.DbConnection;

public class CourseDaoImpl implements CourseDao {

    @Override
    public boolean create(Course course) {
        String sql = "INSERT INTO course(name, credits, day_of_week, time_slot, teacher_no, course_code) VALUES(?,?,?,?,?,?)";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // 暫時給空字串，稍後更新
            ps.setString(1, course.getName());
            ps.setInt(2, course.getCredits());
            ps.setInt(3, course.getDayOfWeek());
            ps.setString(4, course.getTimeSlot());
            ps.setString(5, course.getTeacherNo());
            ps.setString(6, ""); // 先空白

            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    course.setCourseId(newId);

                    // 用 courseId 生成 courseCode
                    String generatedCode = String.format("C%04d", newId);
                    course.setCourseCode(generatedCode);

                    // 更新 course_code 欄位
                    try (PreparedStatement ps2 = conn.prepareStatement(
                            "UPDATE course SET course_code=? WHERE course_id=?")) {
                        ps2.setString(1, generatedCode);
                        ps2.setInt(2, newId);
                        ps2.executeUpdate();
                    }
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Course findById(int courseId) {
        String sql = "SELECT * FROM course WHERE course_id=?";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Course> findAll() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM course";
        try (Connection conn = DbConnection.getDb();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean update(Course course) {
        String sql = "UPDATE course SET name=?, credits=?, day_of_week=?, time_slot=?, teacher_no=? WHERE course_id=?";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, course.getName());
            ps.setInt(2, course.getCredits());
            ps.setInt(3, course.getDayOfWeek());
            ps.setString(4, course.getTimeSlot());
            ps.setString(5, course.getTeacherNo());
            ps.setInt(6, course.getCourseId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int courseId) {
        String sql = "DELETE FROM course WHERE course_id=?";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Course> findByTeacherNo(String teacherNo) {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM course WHERE teacher_no=?";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, teacherNo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private Course mapRow(ResultSet rs) throws SQLException {
        return new Course(
                rs.getInt("course_id"),
                rs.getString("course_code"),
                rs.getString("name"),
                rs.getInt("credits"),
                rs.getInt("day_of_week"),
                rs.getString("time_slot"),
                rs.getString("teacher_no")
        );
    }
}
