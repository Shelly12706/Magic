package util;

import java.text.DecimalFormat;

public class CodeGenerator {
    public static String generateStudentNo(int id) { return "S" + new DecimalFormat("0000").format(id); }
    public static String generateTeacherNo(int id) { return "T" + new DecimalFormat("0000").format(id); }
    public static String generateCourseNo(int id) { return "C" + new DecimalFormat("0000").format(id); }
}
