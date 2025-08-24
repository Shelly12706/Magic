package dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import dao.TeacherDao;
import model.Teacher;
import util.DbConnection;

public class TeacherDaoImpl implements TeacherDao {

    @Override
    public boolean create(Teacher teacher) {
        String sql = "INSERT INTO teacher(teacher_no, username, name, phone, password) VALUES(?,?,?,?,?)";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, teacher.getTeacherNo());
            ps.setString(2, teacher.getUsername());
            ps.setString(3, teacher.getName());
            ps.setString(4, teacher.getPhone());
            ps.setString(5, teacher.getPassword()); // 這裡應該存 hash
            return ps.executeUpdate() > 0;
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return false;
    }

    @Override
    public Teacher findByNo(String teacherNo) {
        String sql = "SELECT * FROM teacher WHERE teacher_no=?";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, teacherNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return null;
    }

    @Override
    public List<Teacher> findAll() {
        List<Teacher> list = new ArrayList<>();
        String sql = "SELECT * FROM teacher";
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
    public boolean update(Teacher teacher) {
        String sql = "UPDATE teacher SET username=?, name=?, phone=?, password=? WHERE teacher_no=?";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, teacher.getUsername());
            ps.setString(2, teacher.getName());
            ps.setString(3, teacher.getPhone());
            ps.setString(4, teacher.getPassword()); // 仍存 hash
            ps.setString(5, teacher.getTeacherNo());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return false;
    }

    @Override
    public boolean delete(String teacherNo) {
        String sql = "DELETE FROM teacher WHERE teacher_no=?";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, teacherNo);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return false;
    }

    @Override
    public Teacher findByUsername(String username) {
        String sql = "SELECT * FROM teacher WHERE username=?";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Teacher mapRow(ResultSet rs) throws SQLException {
        return new Teacher(
                rs.getInt("id"),
                rs.getString("teacher_no"),
                rs.getString("username"),
                rs.getString("name"),
                rs.getString("phone"),
                rs.getString("password"), // DB 裡的 hash
                rs.getTimestamp("create_time")
        );
    }
}
