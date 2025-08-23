package Controller;

import model.Course;
import model.Enrollment;
import model.Student;
import model.Teacher;
import service.CourseService;
import service.EnrollmentService;
import service.StudentService;
import service.impl.CourseServiceImpl;
import service.impl.EnrollmentServiceImpl;
import service.impl.StudentServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.Font;
import java.awt.Color;
import java.awt.Component;
import java.io.FileOutputStream;
import java.util.List;
import java.awt.BorderLayout;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class TeacherPanel extends JPanel {

    private String teacherNo;

    private JTable courseTable;
    private DefaultTableModel courseTableModel;

    private JTable gradeTable;
    private DefaultTableModel gradeTableModel;

    private JTextArea infoArea;

    private CourseService courseService = new CourseServiceImpl();
    private EnrollmentService enrollmentService = new EnrollmentServiceImpl();
    private StudentService studentService = new StudentServiceImpl();

    private JButton backToLoginBtn;

    public TeacherPanel(Teacher teacher) {
        this.teacherNo = teacher.getTeacherNo();
        setLayout(new BorderLayout());

        // ===== infoArea 初始化 =====
        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(new JScrollPane(infoArea), BorderLayout.CENTER);

        // ===== 主分頁 =====
        JTabbedPane tabbedPane = new JTabbedPane();

        // ===== 課程管理 =====
        JPanel coursePanel = new JPanel(new BorderLayout());
        String[] courseCols = {"課程編號","課程名稱","學分","星期","時間段"};
        courseTableModel = new DefaultTableModel(courseCols,0){
            @Override
            public boolean isCellEditable(int row,int col){ return false; }
        };
        courseTable = new JTable(courseTableModel);
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 美化表格
        courseTable.setFont(new Font("微軟正黑體", Font.BOLD, 14));
        courseTable.setRowHeight(30);
        courseTable.getTableHeader().setFont(new Font("微軟正黑體", Font.BOLD, 15));
        courseTable.getTableHeader().setPreferredSize(
                new java.awt.Dimension(courseTable.getColumnModel().getTotalColumnWidth(), 30));
        courseTable.setGridColor(new Color(200,200,200));
        courseTable.setFillsViewportHeight(true);

        coursePanel.add(new JScrollPane(courseTable), BorderLayout.CENTER);

        JPanel courseBtnPanel = new JPanel();
        JButton addBtn = new JButton("新增課程");
        JButton editBtn = new JButton("修改課程");
        JButton deleteBtn = new JButton("刪除課程");
        JButton exportBtn = new JButton("匯出完整 Excel");
        JButton refreshBtn = new JButton("刷新列表");
        courseBtnPanel.add(addBtn);
        courseBtnPanel.add(editBtn);
        courseBtnPanel.add(deleteBtn);
        courseBtnPanel.add(exportBtn);
        courseBtnPanel.add(refreshBtn);
        coursePanel.add(courseBtnPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("課程管理", coursePanel);

        // ===== 成績管理 =====
        JPanel gradePanel = new JPanel(new BorderLayout());
        String[] gradeCols = {"課程編號","課程名稱","學生編號","學生姓名","分數"};
        gradeTableModel = new DefaultTableModel(gradeCols,0){
            @Override
            public boolean isCellEditable(int row,int col){ return col==4; }
        };
        gradeTable = new JTable(gradeTableModel){
            @Override
            public TableCellEditor getCellEditor(int row, int column){
                if(column==4){
                    SpinnerNumberModel model = new SpinnerNumberModel(0.0,0.0,100.0,1.0);
                    return new SpinnerEditor(model);
                }
                return super.getCellEditor(row,column);
            }
            @Override
            public TableCellRenderer getCellRenderer(int row,int column){
                if(column==4) return new SpinnerRenderer();
                return super.getCellRenderer(row,column);
            }
        };
        gradeTable.setRowHeight(30);

        gradePanel.add(new JScrollPane(gradeTable), BorderLayout.CENTER);

        JPanel gradeBtnPanel = new JPanel();
        JButton confirmGradeBtn = new JButton("確認成績");
        JButton batchGradeBtn = new JButton("批次修改成績");
        JButton exportGradeBtn = new JButton("匯出成績 Excel");
        JButton refreshGradeBtn = new JButton("刷新成績列表");
        gradeBtnPanel.add(confirmGradeBtn);
        gradeBtnPanel.add(batchGradeBtn);
        gradeBtnPanel.add(exportGradeBtn);
        gradeBtnPanel.add(refreshGradeBtn);
        gradePanel.add(gradeBtnPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("成績管理", gradePanel);

        // ===== 報表面板 =====
        JPanel reportPanel = new JPanel(new BorderLayout());
        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setLineWrap(true);
        reportArea.setWrapStyleWord(true);
        reportPanel.add(new JScrollPane(reportArea), BorderLayout.CENTER);

        JButton exportReportBtn = new JButton("匯出報表 Excel");
        JButton refreshReportBtn = new JButton("生成報表");
        JPanel reportBtnPanel = new JPanel();
        reportBtnPanel.add(refreshReportBtn);
        reportBtnPanel.add(exportReportBtn);
        reportPanel.add(reportBtnPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("報表", reportPanel);

        // ===== 使用 JSplitPane 分割主面板與 infoArea =====
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, infoPanel);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(1.0);
        add(splitPane, BorderLayout.CENTER);

        // ===== 下方返回登入 =====
        JPanel bottomPanel = new JPanel();
        backToLoginBtn = new JButton("返回登入");
        bottomPanel.add(backToLoginBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // ===== 按鈕事件 =====
        addBtn.addActionListener(e -> addCourse());
        editBtn.addActionListener(e -> editCourse());
        deleteBtn.addActionListener(e -> deleteCourse());
        exportBtn.addActionListener(e -> exportFullExcel());
        refreshBtn.addActionListener(e -> loadCourses());

        confirmGradeBtn.addActionListener(e -> confirmGrades());
        batchGradeBtn.addActionListener(e -> batchModifyGradesDialog());
        exportGradeBtn.addActionListener(e -> exportGradesExcel());
        refreshGradeBtn.addActionListener(e -> loadGrades());

        refreshReportBtn.addActionListener(e -> generateReport(reportArea));
        exportReportBtn.addActionListener(e -> exportReportExcel(reportArea));

        // ===== 初始載入 =====
        loadCourses();
        loadGrades();
    }

    public void setBackToLoginListener(Runnable listener){
        backToLoginBtn.addActionListener(e -> listener.run());
    }

    // ===== Spinner Editor & Renderer =====
    static class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {
        final JSpinner spinner;
        SpinnerEditor(SpinnerNumberModel model){
            spinner = new JSpinner(model);
            spinner.setFont(new Font("微軟正黑體", Font.BOLD, 14));
        }
        public Object getCellEditorValue(){ return spinner.getValue();}
        public Component getTableCellEditorComponent(JTable table,Object value,boolean isSelected,int row,int column){
            try{ spinner.setValue(value != null ? Double.parseDouble(value.toString()) : 0.0);}
            catch(Exception ex){ spinner.setValue(0.0);}
            return spinner;
        }
    }

    static class SpinnerRenderer extends JPanel implements TableCellRenderer {
        private final JSpinner spinner;
        public SpinnerRenderer() {
            setLayout(new BorderLayout());
            spinner = new JSpinner();
            spinner.setFont(new Font("微軟正黑體", Font.BOLD, 14));
            spinner.setBorder(null);
            add(spinner, BorderLayout.CENTER);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column){
            try{ spinner.setValue(value != null ? Double.parseDouble(value.toString()) : 0.0);}
            catch(Exception ex){ spinner.setValue(0.0);}
            if(isSelected) setBackground(table.getSelectionBackground());
            else setBackground(table.getBackground());
            return this;
        }
    }

    // ===== 載入課程 =====
    private void loadCourses(){
        List<Course> courses = courseService.getCoursesByTeacher(teacherNo);
        courseTableModel.setRowCount(0);
        for(Course c : courses){
            courseTableModel.addRow(new Object[]{
                    c.getCourseCode(),c.getName(),c.getCredits(),c.getDayOfWeek(),c.getTimeSlot()
            });
        }
        // 自動更新 infoArea
        updateInfoAreaWithGrades();
    }

    // ===== 載入成績 =====
    private void loadGrades(){
        List<Course> courses = courseService.getCoursesByTeacher(teacherNo);
        gradeTableModel.setRowCount(0);
        for(Course c: courses){
            List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourse(c.getCourseId());
            for(Enrollment e: enrollments){
                Student s = studentService.getStudentByNo(e.getStudentNo());
                gradeTableModel.addRow(new Object[]{
                        c.getCourseCode(),c.getName(),s.getStudentNo(),s.getName(),e.getScore()!=null? e.getScore():0
                });
            }
        }
        // 自動更新 infoArea
        updateInfoAreaWithGrades();
    }

    // ===== 更新 infoArea =====
    private void updateInfoAreaWithGrades(){
        List<Course> courses = courseService.getCoursesByTeacher(teacherNo);
        StringBuilder sb = new StringBuilder();
        sb.append("📌 教師課程與學生成績總覽\n====================\n");

        for(Course c : courses){
            sb.append(c.getName()).append(" (").append(c.getCourseCode()).append(")\n");
            List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourse(c.getCourseId());
            if(enrollments.isEmpty()){
                sb.append("  無學生選課\n");
            } else {
                for(Enrollment e : enrollments){
                    Student s = studentService.getStudentByNo(e.getStudentNo());
                    sb.append("  ").append(s.getStudentNo()).append(" ").append(s.getName())
                      .append(" : ").append(e.getScore() != null ? e.getScore() : 0).append("\n");
                }
            }
            sb.append("\n");
        }
        infoArea.setText(sb.toString());
    }

    // ===== 新增課程 =====
    private void addCourse(){
        JTextField nameField = new JTextField();
        JTextField creditsField = new JTextField();
        JTextField dayField = new JTextField();
        JTextField timeField = new JTextField();

        Object[] msg = {"課程名稱:",nameField,"學分:",creditsField,"星期(1-7):",dayField,"時間段:",timeField};
        int opt = JOptionPane.showConfirmDialog(this,msg,"新增課程",JOptionPane.OK_CANCEL_OPTION);
        if(opt==JOptionPane.OK_OPTION){
            try{
                Course c = new Course();
                c.setName(nameField.getText());
                c.setCredits(Integer.parseInt(creditsField.getText()));
                c.setDayOfWeek(Integer.parseInt(dayField.getText()));
                c.setTimeSlot(timeField.getText());
                c.setTeacherNo(teacherNo);

                // ==== 衝堂檢查 ====
                if(checkScheduleConflict(teacherNo, c.getDayOfWeek(), c.getTimeSlot())){
                    JOptionPane.showMessageDialog(this,"⚠ 新增失敗：課程時間與現有課程衝突！");
                    return;
                }

                boolean success = courseService.createCourse(c);
                JOptionPane.showMessageDialog(this,success?"新增成功":"新增失敗");
                loadCourses();
            }catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(this,"學分與星期必須為數字！");
            }
        }
    }

    // ===== 修改課程 =====
    private void editCourse(){
        int row = courseTable.getSelectedRow();
        if(row<0){ JOptionPane.showMessageDialog(this,"請選課程"); return; }
        String code = (String) courseTableModel.getValueAt(row,0);
        Course c = courseService.getAllCourses().stream()
                .filter(crs -> crs.getCourseCode().equals(code))
                .findFirst().orElse(null);
        if(c==null) return;

        JTextField nameField = new JTextField(c.getName());
        JTextField creditsField = new JTextField(String.valueOf(c.getCredits()));
        JTextField dayField = new JTextField(String.valueOf(c.getDayOfWeek()));
        JTextField timeField = new JTextField(c.getTimeSlot());

        Object[] msg = {"課程名稱:",nameField,"學分:",creditsField,"星期(1-7):",dayField,"時間段:",timeField};
        int opt = JOptionPane.showConfirmDialog(this,msg,"修改課程",JOptionPane.OK_CANCEL_OPTION);
        if(opt==JOptionPane.OK_OPTION){
            try{
                c.setName(nameField.getText());
                c.setCredits(Integer.parseInt(creditsField.getText()));
                c.setDayOfWeek(Integer.parseInt(dayField.getText()));
                c.setTimeSlot(timeField.getText());

                // ==== 衝堂檢查 (排除自己課程) ====
                if(checkScheduleConflict(teacherNo, c.getDayOfWeek(), c.getTimeSlot(), c.getCourseId())){
                    JOptionPane.showMessageDialog(this,"⚠ 修改失敗：課程時間與現有課程衝突！");
                    return;
                }

                boolean success = courseService.updateCourse(c);
                JOptionPane.showMessageDialog(this,success?"修改成功":"修改失敗");
                loadCourses();
                loadGrades();
            }catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(this,"學分與星期必須為數字！");
            }
        }
    }

    // ===== 刪除課程 =====
    private void deleteCourse(){
        int row = courseTable.getSelectedRow();
        if(row<0){ JOptionPane.showMessageDialog(this,"請選課程"); return; }
        String code = (String) courseTableModel.getValueAt(row,0);
        Course c = courseService.getAllCourses().stream()
                .filter(crs -> crs.getCourseCode().equals(code))
                .findFirst().orElse(null);
        if(c==null) return;

        int opt = JOptionPane.showConfirmDialog(this,"確定刪除課程？","刪除確認",JOptionPane.YES_NO_OPTION);
        if(opt==JOptionPane.YES_OPTION){
            boolean success = courseService.deleteCourse(c.getCourseId());
            JOptionPane.showMessageDialog(this,success?"刪除成功":"刪除失敗");
            loadCourses();
            loadGrades();
        }
    }

    // ===== 確認成績 =====
    private void confirmGrades() {
        int rowCount = gradeTable.getRowCount();
        boolean updated = false; // 記錄是否有更新分數

        for (int i = 0; i < rowCount; i++) {
            Object scoreObj = gradeTable.getValueAt(i, 4);
            if (scoreObj == null) continue;

            double score;
            try {
                score = Double.parseDouble(scoreObj.toString());
            } catch (NumberFormatException ex) {
                continue;
            }

            String studentNo = gradeTable.getValueAt(i, 2).toString();
            String courseCode = gradeTable.getValueAt(i, 0).toString();

            // 找到 courseId
            Course course = courseService.getCoursesByTeacher(teacherNo).stream()
                    .filter(c -> c.getCourseCode().equals(courseCode))
                    .findFirst().orElse(null);
            if (course == null) continue;

            // 找到 enrollment
            Enrollment enrollment = enrollmentService.getEnrollmentsByCourse(course.getCourseId()).stream()
                    .filter(e -> e.getStudentNo().equals(studentNo))
                    .findFirst().orElse(null);
            if (enrollment == null) continue;

            // 更新分數到資料庫
            boolean success = enrollmentService.updateEnrollmentScore(enrollment.getEnrollId(), score);
            if (success) updated = true;
        }

        if (updated) {
            // 即時刷新表格與 infoArea
            loadGrades();               // 重新載入 gradeTable
            updateInfoAreaWithGrades(); // 更新 infoArea
            JOptionPane.showMessageDialog(this, "分數已更新！");
        } else {
            JOptionPane.showMessageDialog(this, "沒有分數被更新！");
        }
    }



 // ===== 批次修改多課程、多學生成績 =====
    private void batchModifyGradesDialog(){
        List<Course> courses = courseService.getCoursesByTeacher(teacherNo);
        if(courses.isEmpty()){ JOptionPane.showMessageDialog(this,"沒有可修改的課程！"); return; }

        List<Student> students = studentService.getAllStudents();
        if(students.isEmpty()){ JOptionPane.showMessageDialog(this,"沒有學生資料！"); return; }

        // 課程列表多選
        DefaultListModel<String> courseModel = new DefaultListModel<>();
        for(Course c : courses) courseModel.addElement(c.getCourseCode() + " - " + c.getName());
        JList<String> courseList = new JList<>(courseModel);
        courseList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane courseScroll = new JScrollPane(courseList);
        courseScroll.setPreferredSize(new java.awt.Dimension(300,120));

        // 學生列表多選
        DefaultListModel<String> studentModel = new DefaultListModel<>();
        for(Student s : students) studentModel.addElement(s.getStudentNo() + " - " + s.getName());
        JList<String> studentList = new JList<>(studentModel);
        studentList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane studentScroll = new JScrollPane(studentList);
        studentScroll.setPreferredSize(new java.awt.Dimension(300,200));

        // 分數輸入
        SpinnerNumberModel scoreModel = new SpinnerNumberModel(0.0,0.0,100.0,1.0);
        JSpinner scoreSpinner = new JSpinner(scoreModel);

        Object[] msg = {
                "選擇課程(可多選):", courseScroll,
                "選擇學生(可多選):", studentScroll,
                "輸入分數:", scoreSpinner
        };

        int opt = JOptionPane.showConfirmDialog(this,msg,"批次修改多課程、多學生成績",JOptionPane.OK_CANCEL_OPTION);
        if(opt == JOptionPane.OK_OPTION){
            List<String> selectedCourses = courseList.getSelectedValuesList();
            List<String> selectedStudents = studentList.getSelectedValuesList();
            if(selectedCourses.isEmpty() || selectedStudents.isEmpty()){
                JOptionPane.showMessageDialog(this,"請至少選擇一門課程和一位學生！");
                return;
            }
            double score = (Double) scoreSpinner.getValue();

            // 批次更新
            for(String courseStr : selectedCourses){
                String courseCode = courseStr.split(" - ")[0];
                Course c = courses.stream().filter(cr -> cr.getCourseCode().equals(courseCode)).findFirst().orElse(null);
                if(c == null) continue;

                List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourse(c.getCourseId());
                for(String stuStr : selectedStudents){
                    String studentNo = stuStr.split(" - ")[0];
                    Enrollment e = enrollments.stream().filter(en -> en.getStudentNo().equals(studentNo)).findFirst().orElse(null);
                    if(e != null) enrollmentService.updateEnrollmentScore(e.getEnrollId(),score);
                }
            }
            JOptionPane.showMessageDialog(this,"批次修改完成！");
            loadGrades();
        }
    }

    // ===== 衝堂檢查 =====
    private boolean checkScheduleConflict(String teacherNo, int dayOfWeek, String timeSlot){
        List<Course> myCourses = courseService.getCoursesByTeacher(teacherNo);
        for(Course c : myCourses){
            if(c.getDayOfWeek() == dayOfWeek && c.getTimeSlot().equals(timeSlot)){
                return true;
            }
        }
        return false;
    }

    private boolean checkScheduleConflict(String teacherNo, int dayOfWeek, String timeSlot, int excludeCourseId){
        List<Course> myCourses = courseService.getCoursesByTeacher(teacherNo);
        for(Course c : myCourses){
            if(c.getCourseId() == excludeCourseId) continue;
            if(c.getDayOfWeek() == dayOfWeek && c.getTimeSlot().equals(timeSlot)){
                return true;
            }
        }
        return false;
    }

    // ===== 匯出 Excel =====
    private void exportFullExcel(){
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("Teacher_Course.xls"));
        int option = chooser.showSaveDialog(this);
        if(option==JFileChooser.APPROVE_OPTION){
            try(HSSFWorkbook workbook = new HSSFWorkbook()){
                Sheet sheet = workbook.createSheet("課程清單");
                Row header = sheet.createRow(0);
                String[] cols = {"課程編號","課程名稱","學分","星期","時間段"};
                for(int i=0;i<cols.length;i++) header.createCell(i).setCellValue(cols[i]);
                List<Course> courses = courseService.getCoursesByTeacher(teacherNo);
                for(int r=0;r<courses.size();r++){
                    Course c = courses.get(r);
                    Row row = sheet.createRow(r+1);
                    row.createCell(0).setCellValue(c.getCourseCode());
                    row.createCell(1).setCellValue(c.getName());
                    row.createCell(2).setCellValue(c.getCredits());
                    row.createCell(3).setCellValue(c.getDayOfWeek());
                    row.createCell(4).setCellValue(c.getTimeSlot());
                }
                try(FileOutputStream fos = new FileOutputStream(chooser.getSelectedFile())){
                    workbook.write(fos);
                }
                JOptionPane.showMessageDialog(this,"匯出完成！");
            }catch(Exception ex){
                JOptionPane.showMessageDialog(this,"匯出失敗: "+ex.getMessage());
            }
        }
    }

    private void exportGradesExcel(){
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("Teacher_Grades.xls")); // 預設檔名
        int option = chooser.showSaveDialog(this);
        if(option == JFileChooser.APPROVE_OPTION){
            try(HSSFWorkbook workbook = new HSSFWorkbook()){
                Sheet sheet = workbook.createSheet("成績清單");

                // ===== 建立表頭 =====
                Row header = sheet.createRow(0);
                for(int i=0; i<gradeTableModel.getColumnCount(); i++){
                    header.createCell(i).setCellValue(gradeTableModel.getColumnName(i));
                }

                // ===== 寫入資料 =====
                for(int r=0; r<gradeTableModel.getRowCount(); r++){
                    Row row = sheet.createRow(r+1);
                    for(int c=0; c<gradeTableModel.getColumnCount(); c++){
                        Object val = gradeTableModel.getValueAt(r,c);
                        if(val instanceof Number){
                            row.createCell(c).setCellValue(((Number) val).doubleValue());
                        } else {
                            row.createCell(c).setCellValue(val != null ? val.toString() : "");
                        }
                    }
                }

                // ===== 輸出檔案 =====
                try(FileOutputStream fos = new FileOutputStream(chooser.getSelectedFile())){
                    workbook.write(fos);
                }
                JOptionPane.showMessageDialog(this,"成績匯出完成！");
            } catch(Exception ex){
                JOptionPane.showMessageDialog(this,"匯出失敗: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void exportReportExcel(JTextArea reportArea){
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("Teacher_Report.xls"));
        int option = chooser.showSaveDialog(this);
        if(option==JFileChooser.APPROVE_OPTION){
            try(HSSFWorkbook workbook = new HSSFWorkbook()){
                Sheet sheet = workbook.createSheet("報表");
                String[] lines = reportArea.getText().split("\\r?\\n");
                for(int r=0;r<lines.length;r++){
                    Row row = sheet.createRow(r);
                    row.createCell(0).setCellValue(lines[r]);
                }
                sheet.autoSizeColumn(0);
                try(FileOutputStream fos = new FileOutputStream(chooser.getSelectedFile())){
                    workbook.write(fos);
                }
                JOptionPane.showMessageDialog(this,"報表匯出完成！");
            }catch(Exception ex){
                JOptionPane.showMessageDialog(this,"匯出失敗: "+ex.getMessage());
            }
        }
    }

    private void generateReport(JTextArea reportArea){
        // 自行實作報表內容
    
    	    StringBuilder sb = new StringBuilder();
    	    List<Course> courses = courseService.getCoursesByTeacher(teacherNo);
    	    sb.append("教師課程報表\n====================\n");
    	    for(Course c : courses){
    	        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourse(c.getCourseId());

    	        int studentCount = enrollments.size();
    	        double totalScore = 0;
    	        int scoreCount = 0;
    	        for(Enrollment e : enrollments){
    	            if(e.getScore()!=null){
    	                totalScore += e.getScore();
    	                scoreCount++;
    	            }
    	        }
    	        double avgScore = scoreCount>0 ? totalScore/scoreCount : 0;

    	        sb.append(c.getName())
    	          .append(" (").append(c.getCourseCode()).append("): ")
    	          .append(studentCount).append(" 位學生, 平均分數: ")
    	          .append(String.format("%.2f", avgScore))
    	          .append("\n");
    	    }
    	    reportArea.setText(sb.toString());
    	}
}
