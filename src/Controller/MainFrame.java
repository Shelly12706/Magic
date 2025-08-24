package Controller;

import model.Student;
import model.Teacher;
import service.CourseService;
import service.EnrollmentService;
import service.impl.CourseServiceImpl;
import service.impl.EnrollmentServiceImpl;

import javax.swing.*;

public class MainFrame extends JFrame {

    private JTabbedPane tabbedPane;

    // ===== 老師面板 =====
    private TeacherPanel teacherPanel;
    private ReportPanel teacherReportPanel;

    // ===== 學生面板 =====
    private CoursePanel coursePanelSelect;   // 選課
    private CoursePanel coursePanelGrade;    // 我的成績
    private ReportPanel studentReportPanel;

    // ===== Service 注入 =====
    private CourseService courseService = new CourseServiceImpl();
    private EnrollmentService enrollmentService = new EnrollmentServiceImpl();

    public MainFrame(Object user) {
        setTitle("魔法學院主畫面");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();

        if (user instanceof Teacher) {
            Teacher t = (Teacher) user;

            // 老師面板 + 報表
            teacherPanel = new TeacherPanel(t);
            teacherReportPanel = new ReportPanel(t.getTeacherNo(), null, courseService, enrollmentService);

            tabbedPane.addTab("教師面板", teacherPanel);
            tabbedPane.addTab("統計報表", teacherReportPanel);

            // 登出按鈕
            Runnable backToLogin = () -> {
                dispose();
                SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
            };
            teacherPanel.setBackToLoginListener(backToLogin);

        } else if (user instanceof Student) {
            Student s = (Student) user;

            // 學生選課/我的成績面板
            coursePanelSelect = new CoursePanel(s, "選課");
            coursePanelGrade = new CoursePanel(s, "我的成績");
            studentReportPanel = new ReportPanel(null, s.getStudentNo(), courseService, enrollmentService);

            tabbedPane.addTab("選課", coursePanelSelect);
            tabbedPane.addTab("我的成績", coursePanelGrade);
            tabbedPane.addTab("報表", studentReportPanel);

            // 選課後刷新課程表與報表
            coursePanelSelect.setEnrollmentRefreshListener(() -> refreshAfterEnrollment(s.getStudentNo()));

            // 登出按鈕
            Runnable backToLogin = () -> {
                dispose();
                SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
            };
            coursePanelSelect.setBackToLoginListener(backToLogin);
            coursePanelGrade.setBackToLoginListener(backToLogin);
        }

        add(tabbedPane);
        setVisible(true);
    }

    // ===== 學生選課後刷新課程與報表 =====
    private void refreshAfterEnrollment(String studentNo) {
        SwingUtilities.invokeLater(() -> {
            if (coursePanelSelect != null) coursePanelSelect.loadCourses();
            if (coursePanelGrade != null) coursePanelGrade.loadCourses();
            if (studentReportPanel != null) studentReportPanel.refresh(); // 使用 public refresh()
            if (teacherReportPanel != null) teacherReportPanel.refresh(); // 老師報表刷新
        });
}
}