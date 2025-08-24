package dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import dao.EnrollmentDao;
import model.Enrollment;
import util.DbConnection;

public class EnrollmentDaoImpl implements EnrollmentDao {

    @Override
    public boolean create(Enrollment enrollment) {
        String sql = "INSERT INTO enrollment(student_no, course_id, score, score_time) VALUES(?,?,?,?)";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // 1. 設定學生編號與課程編號
            ps.setString(1, enrollment.getStudentNo());
            ps.setInt(2, enrollment.getCourseId());

            // 2. 設定分數
            if (enrollment.getScore() != null) {
                ps.setDouble(3, enrollment.getScore());
            } else {
                ps.setNull(3, Types.DECIMAL);
            }

            // 3. 設定 score_time，如果沒有給就用現在時間
            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(4, enrollment.getScoreTime() != null ? enrollment.getScoreTime() : now);

            // 4. 執行新增
            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) enrollment.setEnrollId(rs.getInt(1));
                // 將實際存入的 score_time 更新回物件
                if (enrollment.getScoreTime() == null) enrollment.setScoreTime(now);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Enrollment findById(int enrollId) {
        String sql = "SELECT * FROM enrollment WHERE enroll_id=?";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Enrollment> findAll() {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT * FROM enrollment";
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
    public List<Enrollment> findByStudentNo(String studentNo) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT * FROM enrollment WHERE student_no=?";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentNo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Enrollment> findByCourseId(int courseId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT * FROM enrollment WHERE course_id=?";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean update(Enrollment enrollment) {
        String sql = "UPDATE enrollment SET student_no=?, course_id=?, score=?, score_time=? WHERE enroll_id=?";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, enrollment.getStudentNo());
            ps.setInt(2, enrollment.getCourseId());

            // 設定分數
            if (enrollment.getScore() != null) {
                ps.setDouble(3, enrollment.getScore());
            } else {
                ps.setNull(3, Types.DECIMAL);
            }

            // 自動更新 score_time 為現在時間
            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(4, now);
            enrollment.setScoreTime(now);

            ps.setInt(5, enrollment.getEnrollId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int enrollId) {
        String sql = "DELETE FROM enrollment WHERE enroll_id=?";
        try (Connection conn = DbConnection.getDb();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 將 ResultSet 轉成 Enrollment 物件
     */
    private Enrollment mapRow(ResultSet rs) throws SQLException {
        Enrollment e = new Enrollment();
        e.setEnrollId(rs.getInt("enroll_id"));
        e.setStudentNo(rs.getString("student_no"));
        e.setCourseId(rs.getInt("course_id"));
        Object scoreObj = rs.getObject("score");
        e.setScore(scoreObj != null ? rs.getDouble("score") : null);
        e.setEnrollTime(rs.getTimestamp("enroll_time"));
        e.setScoreTime(rs.getTimestamp("score_time"));
        return e;
    }
}
