package dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import dao.StudentDao;
import model.Student;
import util.DbConnection;

public class StudentDaoImpl implements StudentDao {

    @Override
    public boolean create(Student student) {
        String sql = "INSERT INTO student(student_no, username, name, phone, email, address, password) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getStudentNo());
            ps.setString(2, student.getUsername());
            ps.setString(3, student.getName());
            ps.setString(4, student.getPhone());
            ps.setString(5, student.getEmail());
            ps.setString(6, student.getAddress());
            ps.setString(7, student.getPassword());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return false;
    }

    @Override
    public Student findByNo(String studentNo) {
        String sql = "SELECT * FROM student WHERE student_no=?";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return null;
    }

    @Override
    public Student findByUsername(String username) {
        String sql = "SELECT * FROM student WHERE username=?";
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

    @Override
    public List<Student> findAll() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM student";
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
    public boolean update(Student student) {
        String sql = "UPDATE student SET username=?, name=?, phone=?, email=?, address=?, password=? WHERE student_no=?";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getUsername());
            ps.setString(2, student.getName());
            ps.setString(3, student.getPhone());
            ps.setString(4, student.getEmail());
            ps.setString(5, student.getAddress());
            ps.setString(6, student.getPassword());
            ps.setString(7, student.getStudentNo());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return false;
    }

    @Override
    public boolean delete(String studentNo) {
        String sql = "DELETE FROM student WHERE student_no=?";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentNo);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return false;
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        return new Student(
            rs.getInt("id"),
            rs.getString("student_no"),
            rs.getString("username"),
            rs.getString("name"),
            rs.getString("phone"),
            rs.getString("email"),
            rs.getString("address"),
            rs.getString("password"),
            rs.getTimestamp("create_time")
        );
    }

    @Override
    public int getNextId() {
        String sql = "SELECT MAX(id) FROM student"; 
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) + 1; 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1; // 空表時回傳 1
    }
}
