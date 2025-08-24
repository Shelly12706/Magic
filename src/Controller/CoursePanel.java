package Controller;

import model.Course;
import model.Enrollment;
import model.Student;
import model.Teacher;
import service.CourseService;
import service.EnrollmentService;
import service.TeacherService;
import service.impl.CourseServiceImpl;
import service.impl.EnrollmentServiceImpl;
import service.impl.TeacherServiceImpl;
import java.awt.Font;
import java.awt.Color;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class CoursePanel extends JPanel {
    private Student student;
    private JTable courseTable;
    private DefaultTableModel courseTableModel;
    private String mode; // "選課" or "我的成績"

    private CourseService courseService = new CourseServiceImpl();
    private EnrollmentService enrollmentService = new EnrollmentServiceImpl();
    private TeacherService teacherService = new TeacherServiceImpl();

    private Runnable enrollmentRefreshListener;
    private Runnable backToLoginListener;
    private JTextArea infoArea;
    private JComboBox<String> filterCombo; // 全部課程 / 依老師篩選

    public CoursePanel(Student student, String mode) {
        this.student = student;
        this.mode = mode;

        setLayout(new BorderLayout(10, 10));

        // ===== 上方篩選 ComboBox =====
        filterCombo = new JComboBox<>();
        filterCombo.addItem("全部課程");
        for (Teacher t : teacherService.getAllTeachers()) {
            filterCombo.addItem(t.getName());
        }
        filterCombo.addActionListener(e -> loadCourses());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("篩選老師: "));
        topPanel.add(filterCombo);
        add(topPanel, BorderLayout.NORTH);

        // ===== 課程表格 =====
        String[] columns;
        if ("選課".equals(mode)) {
            columns = new String[]{"課程代碼", "課程名稱", "老師", "學分", "星期", "時間段"};
        } else {
            columns = new String[]{"課程代碼", "課程名稱", "老師", "學分", "星期", "時間段", "成績"};
        }
        courseTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        courseTable = new JTable(courseTableModel);
        courseTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // ===== 美化表格 =====
        courseTable.setRowHeight(30);
        courseTable.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        courseTable.setSelectionBackground(new Color(135, 206, 250));
        courseTable.setSelectionForeground(Color.BLACK);
        courseTable.getTableHeader().setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));
        courseTable.getTableHeader().setBackground(new Color(220, 220, 220));

        add(new JScrollPane(courseTable), BorderLayout.CENTER);

        // ===== 下方按鈕列 =====
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        if ("選課".equals(mode)) {
            JButton enrollBtn = new JButton("選課");
            enrollBtn.addActionListener(e -> {
                enrollSelectedCourses();
                loadCourses();
            });

            JButton dropBtn = new JButton("退選");
            dropBtn.addActionListener(e -> {
                dropCourse();
                loadCourses();
            });

            JButton confirmBtn = new JButton("確認選課");
            confirmBtn.addActionListener(e -> confirmEnrollment());

            btnPanel.add(enrollBtn);
            btnPanel.add(dropBtn);
            btnPanel.add(confirmBtn);
        }

        JButton refreshBtn = new JButton("刷新課程列表");
        refreshBtn.addActionListener(e -> loadCourses());

        JButton exportBtn = new JButton("匯出報表");
        exportBtn.addActionListener(e -> exportCoursesExcelWithDialog());

        JButton logoutBtn = new JButton("登出");
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this, "要登出嗎？", "確認登出", JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION && backToLoginListener != null) {
                backToLoginListener.run();
            }
        });

        btnPanel.add(refreshBtn);
        btnPanel.add(exportBtn);
        btnPanel.add(logoutBtn);

        add(btnPanel, BorderLayout.SOUTH);

        // ===== infoArea =====
        infoArea = new JTextArea(12, 25);
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));
        infoArea.setBorder(BorderFactory.createTitledBorder("已選課程"));
        add(new JScrollPane(infoArea), BorderLayout.EAST);

        loadCourses();
    }

    public void setEnrollmentRefreshListener(Runnable listener) {
        this.enrollmentRefreshListener = listener;
    }

    public void setBackToLoginListener(Runnable listener) {
        this.backToLoginListener = listener;
    }

    // ===== 載入課程表 =====
    public void loadCourses() {
        courseTableModel.setRowCount(0);

        List<Course> courses;
        if ("選課".equals(mode)) {
            String filter = (String) filterCombo.getSelectedItem();
            if ("全部課程".equals(filter)) {
                courses = courseService.getAllCourses();
            } else {
                courses = courseService.getAllCourses().stream()
                        .filter(c -> {
                            Teacher t = teacherService.getTeacherByNo(c.getTeacherNo());
                            return t != null && t.getName().equals(filter);
                        }).collect(Collectors.toList());
            }
        } else {
            List<Enrollment> myEnrollments = enrollmentService.getEnrollmentsByStudent(student.getStudentNo());
            courses = myEnrollments.stream()
                    .map(e -> courseService.getCourseById(e.getCourseId()))
                    .collect(Collectors.toList());
        }

        int totalCredits = 0;
        for (Course c : courses) {
            Teacher t = teacherService.getTeacherByNo(c.getTeacherNo());
            String teacherName = t != null ? t.getName() : "未知";

            Object[] row;
            if ("選課".equals(mode)) {
                row = new Object[]{
                        c.getCourseCode(), c.getName(), teacherName,
                        c.getCredits(), c.getDayOfWeek(), c.getTimeSlot()
                };
                totalCredits += c.getCredits();
            } else {
                Enrollment e = enrollmentService.getEnrollmentsByStudent(student.getStudentNo()).stream()
                        .filter(en -> en.getCourseId() == c.getCourseId())
                        .findFirst().orElse(null);
                Double score = e != null ? e.getScore() : null;
                row = new Object[]{
                        c.getCourseCode(), c.getName(), teacherName,
                        c.getCredits(), c.getDayOfWeek(), c.getTimeSlot(),
                        score != null ? score : "未評分"
                };
                totalCredits += c.getCredits();
            }
            courseTableModel.addRow(row);
        }

        // 總學分列
        if ("選課".equals(mode)) {
            courseTableModel.addRow(new Object[]{"", "", "總學分", totalCredits, "", ""});
        } else {
            courseTableModel.addRow(new Object[]{"", "", "總學分", totalCredits, "", "", ""});
        }

        updateSelectedCoursesArea();
    }

    private void updateSelectedCoursesArea() {
        List<Enrollment> myEnrollments = enrollmentService.getEnrollmentsByStudent(student.getStudentNo());
        if (myEnrollments.isEmpty()) {
            infoArea.setText("尚未選課");
            return;
        }

        StringBuilder sb = new StringBuilder("📚 已選課程：\n");
        int totalCredits = 0;
        for (Enrollment e : myEnrollments) {
            Course c = courseService.getCourseById(e.getCourseId());
            Teacher t = teacherService.getTeacherByNo(c.getTeacherNo());
            sb.append(c.getCourseCode())
              .append(" - ").append(c.getName())
              .append(" (老師: ").append(t != null ? t.getName() : "未知")
              .append(", 學分: ").append(c.getCredits());
            if ("我的成績".equals(mode)) {
                sb.append(", 成績: ").append(e.getScore() != null ? e.getScore() : "未評分");
            }
            sb.append(")\n");
            totalCredits += c.getCredits();
        }
        sb.append("總學分: ").append(totalCredits);
        infoArea.setText(sb.toString());
    }

    // ===== 多選課程加入 =====
    private void enrollSelectedCourses() {
        int[] rows = courseTable.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "請選擇至少一堂課！");
            return;
        }

        List<Course> allCourses = courseService.getAllCourses();
        List<Enrollment> myEnrollments = enrollmentService.getEnrollmentsByStudent(student.getStudentNo());
        boolean anySuccess = false;

        for (int row : rows) {
            if (row >= courseTableModel.getRowCount() - 1) continue; // skip total row
            String code = (String) courseTableModel.getValueAt(row, 0);
            Course course = allCourses.stream()
                    .filter(c -> c.getCourseCode().equals(code))
                    .findFirst().orElse(null);
            if (course == null) continue;

            boolean conflict = myEnrollments.stream().anyMatch(e -> {
                Course c = courseService.getCourseById(e.getCourseId());
                return c.getDayOfWeek() == course.getDayOfWeek() &&
                        c.getTimeSlot().equals(course.getTimeSlot());
            });
            if (conflict) {
                JOptionPane.showMessageDialog(this, "課程衝堂：" + course.getName());
                continue;
            }

            Enrollment e = new Enrollment();
            e.setStudentNo(student.getStudentNo());
            e.setCourseId(course.getCourseId());
            boolean success = enrollmentService.enrollStudent(e);
            if (success) {
                myEnrollments.add(e);
                anySuccess = true;
            }
        }

        updateSelectedCoursesArea();
        if (enrollmentRefreshListener != null) enrollmentRefreshListener.run();

        if (anySuccess) {
            JOptionPane.showMessageDialog(this, "選課完成！");
        } else {
            JOptionPane.showMessageDialog(this, "沒有成功選課！");
        }
    }

    private void confirmEnrollment() {
        List<Enrollment> myEnrollments = enrollmentService.getEnrollmentsByStudent(student.getStudentNo());
        int totalCredits = myEnrollments.stream()
                .mapToInt(e -> courseService.getCourseById(e.getCourseId()).getCredits())
                .sum();

        if (totalCredits < 15) {
            JOptionPane.showMessageDialog(this, "選課總學分不足 15 分，無法確認！");
        } else {
            JOptionPane.showMessageDialog(this, "選課已確認！總學分: " + totalCredits);
        }
    }

    private void dropCourse() {
        int row = courseTable.getSelectedRow();
        if (row < 0 || row >= courseTableModel.getRowCount() - 1) {
            JOptionPane.showMessageDialog(this, "請選擇課程！");
            return;
        }

        String code = (String) courseTableModel.getValueAt(row, 0);
        Course course = courseService.getAllCourses().stream()
                .filter(c -> c.getCourseCode().equals(code)).findFirst().orElse(null);
        if (course == null) return;

        List<Enrollment> myEnrollments = enrollmentService.getEnrollmentsByStudent(student.getStudentNo());
        Enrollment target = myEnrollments.stream()
                .filter(e -> e.getCourseId() == course.getCourseId())
                .findFirst().orElse(null);
        if (target == null) {
            JOptionPane.showMessageDialog(this, "尚未選課！");
            return;
        }

        boolean success = enrollmentService.dropCourse(target.getEnrollId());
        JOptionPane.showMessageDialog(this, success ? "退選成功！" : "退選失敗！");
        updateSelectedCoursesArea();
        if (enrollmentRefreshListener != null) enrollmentRefreshListener.run();
    }

    private void exportCoursesExcelWithDialog() {
        List<Enrollment> myEnrollments = enrollmentService.getEnrollmentsByStudent(student.getStudentNo());
        if (myEnrollments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "尚無已選課紀錄！");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("匯出 Excel");
        fileChooser.setSelectedFile(new File(student.getStudentNo() + "_課程紀錄.xls"));
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) return;
        File fileToSave = fileChooser.getSelectedFile();

        try (Workbook workbook = new HSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("課程資料");
            Row header = sheet.createRow(0);

            String[] columns = {"課程代碼", "課程名稱", "老師", "學分", "星期", "時間段"};
            if ("我的成績".equals(mode)) {
                columns = new String[]{"課程代碼", "課程名稱", "老師", "學分", "星期", "時間段", "成績"};
            }
            for (int i = 0; i < columns.length; i++) header.createCell(i).setCellValue(columns[i]);

            int rowIdx = 1, totalCredits = 0;
            for (Enrollment e : myEnrollments) {
                Course c = courseService.getCourseById(e.getCourseId());
                Teacher t = teacherService.getTeacherByNo(c.getTeacherNo());
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(c.getCourseCode());
                row.createCell(1).setCellValue(c.getName());
                row.createCell(2).setCellValue(t != null ? t.getName() : "未知");
                row.createCell(3).setCellValue(c.getCredits());
                row.createCell(4).setCellValue(c.getDayOfWeek());
                row.createCell(5).setCellValue(c.getTimeSlot());
                if ("我的成績".equals(mode)) {
                    row.createCell(6).setCellValue(e.getScore() != null ? e.getScore() : 0);
                }
                totalCredits += c.getCredits();
            }

            Row totalRow = sheet.createRow(rowIdx);
            totalRow.createCell(2).setCellValue("總學分");
            totalRow.createCell(3).setCellValue(totalCredits);

            for (int i = 0; i < columns.length; i++) sheet.autoSizeColumn(i);

            try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
                workbook.write(fos);
            }
            JOptionPane.showMessageDialog(this, "匯出完成: " + fileToSave.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "匯出失敗！");
        }
    }
}
