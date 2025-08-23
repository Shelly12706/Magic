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
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class ReportPanel extends JPanel {
    private String teacherNo;
    private String studentNo;

    private CourseService courseService;
    private EnrollmentService enrollmentService;

    // 小數點格式
    private static final DecimalFormat oneDecimal = new DecimalFormat("0.0");

    // 統一建構子：老師或學生皆可
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
        setLayout(new GridLayout(1, teacherNo != null ? 3 : 1)); // 老師三張圖，學生一張圖
        loadCourses(); // 初始化圖表
    }

    public void loadCourses() {
        removeAll();

        List<Course> courses;
        if (teacherNo != null) {
            courses = courseService.getCoursesByTeacher(teacherNo);
        } else if (studentNo != null) {
            List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(studentNo);
            courses = enrollments.stream()
                    .map(e -> courseService.getCourseById(e.getCourseId()))
                    .collect(Collectors.toList());
        } else {
            courses = new ArrayList<>();
        }

        // ===== 老師端報表 =====
        if (teacherNo != null) {
            DefaultCategoryDataset courseDataset = new DefaultCategoryDataset();
            DefaultCategoryDataset passRateDataset = new DefaultCategoryDataset();
            DefaultPieDataset scoreDataset = new DefaultPieDataset();

            for (Course c : courses) {
                List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourse(c.getCourseId());
                int count = enrollments.size();
                courseDataset.addValue(count, "學生人數", c.getCourseCode() + " (" + c.getName() + ")");

                long passCount = enrollments.stream()
                        .filter(e -> e.getScore() != null && e.getScore() >= 60)
                        .count();
                double passRate = enrollments.isEmpty() ? 0 : (double) passCount / enrollments.size() * 100;
                passRateDataset.addValue(Double.parseDouble(oneDecimal.format(passRate)), "通過率", c.getCourseCode() + " (" + c.getName() + ")");

                double totalScore = enrollments.stream()
                        .filter(e -> e.getScore() != null)
                        .mapToDouble(Enrollment::getScore)
                        .sum();
                double avgScore = enrollments.isEmpty() ? 0 : totalScore / enrollments.size();
                scoreDataset.setValue(c.getCourseCode() + " (" + c.getName() + ")", Double.parseDouble(oneDecimal.format(avgScore)));
            }

            // ===== BarChart: 課程學生數統計 =====
            JFreeChart barChart = ChartFactory.createBarChart("課程學生數統計", "課程", "學生數", courseDataset);
            CategoryPlot barPlot = barChart.getCategoryPlot();
            barPlot.getDomainAxis().setCategoryLabelPositions(
                    org.jfree.chart.axis.CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 4)
            );
            org.jfree.chart.renderer.category.BarRenderer renderer = (org.jfree.chart.renderer.category.BarRenderer) barPlot.getRenderer();
            renderer.setDefaultItemLabelsVisible(true);
            renderer.setDefaultItemLabelFont(new java.awt.Font("Microsoft JhengHei", java.awt.Font.PLAIN, 12));
            renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
            // 顏色自動對比（高紅低綠）
            for (int i = 0; i < courseDataset.getColumnCount(); i++) {
                Number value = courseDataset.getValue(0, i);
                if (value != null) {
                    double v = value.doubleValue();
                    renderer.setSeriesPaint(0, new java.awt.Color((int) Math.min(255, v * 20), (int) Math.max(0, 255 - v * 20), 0));
                }
            }

            // ===== BarChart: 課程通過率 =====
            JFreeChart passChart = ChartFactory.createBarChart("課程通過率 (%)", "課程", "通過率", passRateDataset);
            CategoryPlot passPlot = passChart.getCategoryPlot();
            passPlot.getDomainAxis().setCategoryLabelPositions(
                    org.jfree.chart.axis.CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 4)
            );
            org.jfree.chart.renderer.category.BarRenderer passRenderer = (org.jfree.chart.renderer.category.BarRenderer) passPlot.getRenderer();
            passRenderer.setDefaultItemLabelsVisible(true);
            passRenderer.setDefaultItemLabelFont(new java.awt.Font("Microsoft JhengHei", java.awt.Font.PLAIN, 12));
            passRenderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());

            // ===== PieChart: 平均分數 =====
            JFreeChart pieChart = ChartFactory.createPieChart("課程平均分數", scoreDataset, true, true, false);
            PiePlot piePlot = (PiePlot) pieChart.getPlot();
            piePlot.setLabelFont(new java.awt.Font("Microsoft JhengHei", java.awt.Font.PLAIN, 12));
            piePlot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1}"));
            piePlot.setToolTipGenerator(new StandardPieToolTipGenerator("{0}: {1}"));

            setChartFont(barChart);
            setChartFont(passChart);
            setChartFont(pieChart);

            add(new ChartPanel(barChart));
            add(new ChartPanel(passChart));
            add(new ChartPanel(pieChart));
        }

        // ===== 學生端折線圖 =====
        if (studentNo != null) {
            DefaultCategoryDataset studentTrend = new DefaultCategoryDataset();
            for (Course c : courses) {
                Enrollment e = enrollmentService.getEnrollmentsByStudent(studentNo).stream()
                        .filter(en -> en.getCourseId() == c.getCourseId())
                        .findFirst().orElse(null);
                if (e != null && e.getScore() != null) {
                    studentTrend.addValue(e.getScore(), "成績", c.getName());
                }
            }

            JFreeChart lineChart = ChartFactory.createLineChart("個人成績趨勢", "課程", "分數", studentTrend);

            CategoryPlot plot = lineChart.getCategoryPlot();
            org.jfree.chart.renderer.category.LineAndShapeRenderer lineRenderer =
                    new org.jfree.chart.renderer.category.LineAndShapeRenderer();
            lineRenderer.setDefaultShapesVisible(true);
            lineRenderer.setDefaultItemLabelsVisible(true);
            lineRenderer.setDefaultItemLabelFont(new java.awt.Font("Microsoft JhengHei", java.awt.Font.PLAIN, 12));
            lineRenderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
            lineRenderer.setDefaultToolTipGenerator(new org.jfree.chart.labels.StandardCategoryToolTipGenerator(
                    "{1}: {2}", java.text.NumberFormat.getInstance()
            ));
            lineRenderer.setSeriesPaint(0, java.awt.Color.BLUE);
            plot.setRenderer(lineRenderer);
            plot.getRangeAxis().setRange(0, 100); // 固定 Y 軸 0-100

            setChartFont(lineChart);

            add(new ChartPanel(lineChart));
        }

        revalidate();
        repaint();
    }

    // ===== 設定 JFreeChart 中文字型 =====
    private void setChartFont(JFreeChart chart) {
        java.awt.Font titleFont = new java.awt.Font("Microsoft JhengHei", java.awt.Font.BOLD, 18);
        java.awt.Font axisFont = new java.awt.Font("Microsoft JhengHei", java.awt.Font.PLAIN, 14);
        java.awt.Font legendFont = new java.awt.Font("Microsoft JhengHei", java.awt.Font.PLAIN, 12);

        chart.setTitle(new TextTitle(chart.getTitle().getText(), titleFont));
        if (chart.getLegend() != null) {
            chart.getLegend().setItemFont(legendFont);
        }

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

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) return;

        File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getName().endsWith(".xls")) {
            fileToSave = new File(fileToSave.getAbsolutePath() + ".xls");
        }

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
                List<Course> coursesList = courseService.getCoursesByTeacher(teacherNo);
                for (Course c : coursesList) {
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

                Cell cell = row.createCell(2);
                if (e.getScore() != null) {
                    cell.setCellValue(e.getScore().doubleValue());
                } else {
                    cell.setCellValue("");
                }
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
