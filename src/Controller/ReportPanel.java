package Controller;

import model.Course;
import model.Enrollment;
import service.CourseService;
import service.EnrollmentService;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import java.awt.Font;
import java.awt.Color;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class ReportPanel extends JPanel {

    private String teacherNo;
    private String studentNo;

    private CourseService courseService;
    private EnrollmentService enrollmentService;

    private static final DecimalFormat oneDecimal = new DecimalFormat("0.0");

    // ===== 老師端：持久化 Dataset / ChartPanel，避免每次重建導致不即時刷新 =====
    private final DefaultCategoryDataset teacherCourseDataset = new DefaultCategoryDataset();
    private final DefaultCategoryDataset teacherPassRateDataset = new DefaultCategoryDataset();
    private final DefaultPieDataset teacherScoreDataset = new DefaultPieDataset();

    private ChartPanel teacherCoursePanel;
    private ChartPanel teacherPassPanel;
    private ChartPanel teacherScorePanel;

    public ReportPanel(String teacherNo, String studentNo,
                       CourseService courseService,
                       EnrollmentService enrollmentService) {
        this.teacherNo = teacherNo;
        this.studentNo = studentNo;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        initUI();
    }

    private void initUI() {
        setLayout(new GridLayout(1, teacherNo != null ? 3 : 1));
        refresh();
    }

    public void refresh() {
        removeAll();

        if (teacherNo != null) {
            loadTeacherCharts();
        } else if (studentNo != null) {
            loadStudentChart();
        }

        revalidate();
        repaint();
        updateUI(); // 確保立即刷新
    }

    private void loadTeacherCharts() {
        // 先清 Dataset
        teacherCourseDataset.clear();
        teacherPassRateDataset.clear();
        teacherScoreDataset.clear();

        List<Course> courses = courseService.getCoursesByTeacher(teacherNo);

        for (Course c : courses) {
            List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourse(c.getCourseId());
            String key = c.getCourseCode() + " (" + c.getName() + ")";

            int count = enrollments.size();
            teacherCourseDataset.addValue(count, "學生人數", key);

            long passCount = enrollments.stream()
                    .filter(e -> e.getScore() != null && e.getScore() >= 60)
                    .count();
            double passRate = enrollments.isEmpty() ? 0 : (double) passCount / enrollments.size() * 100.0;
            // 直接放 double，顯示格式交給 renderer
            teacherPassRateDataset.addValue(passRate, "通過率", key);

            double totalScore = enrollments.stream()
                    .filter(e -> e.getScore() != null)
                    .mapToDouble(Enrollment::getScore)
                    .sum();
            double avgScore = enrollments.isEmpty() ? 0 : totalScore / enrollments.size();
            teacherScoreDataset.setValue(key, avgScore);
        }

        // 第一次建立圖；之後只更新 dataset（上面已清空並重塞資料）
        if (teacherCoursePanel == null) {
            // 課程學生數 BarChart
            JFreeChart barChart = ChartFactory.createBarChart("課程學生數統計", "課程", "學生數", teacherCourseDataset);
            CategoryPlot barPlot = barChart.getCategoryPlot();
            barPlot.getDomainAxis().setCategoryLabelPositions(
                    org.jfree.chart.axis.CategoryLabelPositions.createUpRotationLabelPositions(getRotationAngle(courses.size()))
            );
            BarRenderer barRenderer = (BarRenderer) barPlot.getRenderer();
            barRenderer.setDefaultItemLabelsVisible(true);
            barRenderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
            barRenderer.setDefaultItemLabelFont(new Font("Microsoft JhengHei", Font.PLAIN, 12));
            barRenderer.setSeriesPaint(0, Color.GREEN); // 課程學生數改成綠色

            // 課程通過率 BarChart
            JFreeChart passChart = ChartFactory.createBarChart("課程通過率 (%)", "課程", "通過率", teacherPassRateDataset);
            CategoryPlot passPlot = passChart.getCategoryPlot();
            passPlot.getDomainAxis().setCategoryLabelPositions(
                    org.jfree.chart.axis.CategoryLabelPositions.createUpRotationLabelPositions(getRotationAngle(courses.size()))
            );
            passPlot.getRangeAxis().setRange(0, 100); // 固定 0~100，視覺更直覺
            BarRenderer passRenderer = (BarRenderer) passPlot.getRenderer();
            passRenderer.setDefaultItemLabelsVisible(true);
            passRenderer.setDefaultItemLabelGenerator(
                    new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0.0"))
            );
            passRenderer.setDefaultItemLabelFont(new Font("Microsoft JhengHei", Font.PLAIN, 12));

            // 課程平均分數 PieChart
            JFreeChart pieChart = ChartFactory.createPieChart("課程平均分數", teacherScoreDataset, true, true, false);
            PiePlot piePlot = (PiePlot) pieChart.getPlot();
            piePlot.setLabelFont(new Font("Microsoft JhengHei", Font.PLAIN, 12));
            // 顯示到一位小數
            piePlot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1}", new DecimalFormat("0.0"), new DecimalFormat("0.0")));
            piePlot.setToolTipGenerator(new StandardPieToolTipGenerator("{0}: {1}"));

            setChartFont(barChart);
            setChartFont(passChart);
            setChartFont(pieChart);

            teacherCoursePanel = new ChartPanel(barChart);
            teacherPassPanel = new ChartPanel(passChart);
            teacherScorePanel = new ChartPanel(pieChart);
        } else {
            // 如果圖已建立，只需要更新類別標籤角度與通過率軸（避免課程數改變時標籤擠在一起）
            JFreeChart barChart = teacherCoursePanel.getChart();
            CategoryPlot barPlot = (CategoryPlot) barChart.getPlot();
            barPlot.getDomainAxis().setCategoryLabelPositions(
                    org.jfree.chart.axis.CategoryLabelPositions.createUpRotationLabelPositions(getRotationAngle(courses.size()))
            );

            JFreeChart passChart = teacherPassPanel.getChart();
            CategoryPlot passPlot = (CategoryPlot) passChart.getPlot();
            passPlot.getDomainAxis().setCategoryLabelPositions(
                    org.jfree.chart.axis.CategoryLabelPositions.createUpRotationLabelPositions(getRotationAngle(courses.size()))
            );
            passPlot.getRangeAxis().setRange(0, 100);
        }

        add(teacherCoursePanel);
        add(teacherPassPanel);
        add(teacherScorePanel);
    }

    private void loadStudentChart() {
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(studentNo);

        DefaultCategoryDataset studentTrend = new DefaultCategoryDataset();
        for (Enrollment e : enrollments) {
            if (e.getScore() != null) {
                Course c = courseService.getCourseById(e.getCourseId());
                studentTrend.addValue(e.getScore(), "成績", c.getName());
            }
        }

        JFreeChart lineChart = ChartFactory.createLineChart("個人成績趨勢", "課程", "分數", studentTrend);
        CategoryPlot plot = lineChart.getCategoryPlot();
        LineAndShapeRenderer lineRenderer = new LineAndShapeRenderer();
        lineRenderer.setDefaultShapesVisible(true);
        lineRenderer.setDefaultItemLabelsVisible(true);
        lineRenderer.setDefaultItemLabelFont(new Font("Microsoft JhengHei", Font.PLAIN, 12));
        lineRenderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        lineRenderer.setSeriesPaint(0, Color.BLUE);
        plot.setRenderer(lineRenderer);
        plot.getRangeAxis().setRange(0, 100);

        plot.getDomainAxis().setCategoryLabelPositions(
                org.jfree.chart.axis.CategoryLabelPositions.createUpRotationLabelPositions(getRotationAngle(studentTrend.getColumnCount()))
        );

        setChartFont(lineChart);

        add(new ChartPanel(lineChart));
    }

    private double getRotationAngle(int categoryCount) {
        if (categoryCount > 10) return Math.PI / 2; // 90度
        if (categoryCount > 5) return Math.PI / 4;  // 45度
        return 0;
    }

    private void setChartFont(JFreeChart chart) {
        Font titleFont = new Font("Microsoft JhengHei", Font.BOLD, 18);
        Font axisFont = new Font("Microsoft JhengHei", Font.PLAIN, 14);
        Font legendFont = new Font("Microsoft JhengHei", Font.PLAIN, 12);

        chart.setTitle(new TextTitle(chart.getTitle().getText(), titleFont));
        if (chart.getLegend() != null) chart.getLegend().setItemFont(legendFont);

        Plot plot = chart.getPlot();
        if (plot instanceof CategoryPlot) {
            CategoryPlot cp = (CategoryPlot) plot;
            cp.getDomainAxis().setLabelFont(axisFont);
            cp.getDomainAxis().setTickLabelFont(axisFont);
            cp.getRangeAxis().setLabelFont(axisFont);
            cp.getRangeAxis().setTickLabelFont(axisFont);
        } else if (plot instanceof PiePlot) {
            PiePlot pp = (PiePlot) plot;
            pp.setLabelFont(axisFont);
        }
    }

    // ===== 匯出 Excel =====
    public void exportExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("選擇匯出 Excel 檔案位置");
        fileChooser.setSelectedFile(new File("report.xls"));
        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getName().endsWith(".xls")) fileToSave = new File(fileToSave.getAbsolutePath() + ".xls");

        try (Workbook workbook = new HSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("成績報表");
            CellStyle style = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font font = workbook.createFont();
            font.setFontName("Microsoft JhengHei");
            style.setFont(font);

            Row header = sheet.createRow(0);
            String[] headers = {"學號", "課程", "分數"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(style);
            }

            List<Enrollment> enrollments;
            if (teacherNo != null) {
                enrollments = new ArrayList<>();
                for (Course c : courseService.getCoursesByTeacher(teacherNo)) {
                    enrollments.addAll(enrollmentService.getEnrollmentsByCourse(c.getCourseId()));
                }
            } else {
                enrollments = enrollmentService.getEnrollmentsByStudent(studentNo);
            }

            int rowIdx = 1;
            for (Enrollment e : enrollments) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(e.getStudentNo());
                Course c = courseService.getCourseById(e.getCourseId());
                row.createCell(1).setCellValue(c.getName());
                row.createCell(2).setCellValue(e.getScore() != null ? e.getScore() : 0);
            }

            for (int i = 0; i < 3; i++) sheet.autoSizeColumn(i);

            try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
                workbook.write(fos);
            }
            JOptionPane.showMessageDialog(this, "Excel 匯出成功：" + fileToSave.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "匯出失敗：" + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
