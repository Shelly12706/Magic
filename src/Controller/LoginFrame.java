package Controller;

import model.Student;
import model.Teacher;
import service.StudentService;
import service.TeacherService;
import service.impl.StudentServiceImpl;
import service.impl.TeacherServiceImpl;

import javax.swing.*;
import java.awt.*;

// LoginFrame: 學生/老師登入介面
public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;

    private StudentService studentService = new StudentServiceImpl();
    private TeacherService teacherService = new TeacherServiceImpl();

    public LoginFrame() {
    	getContentPane().setBackground(Color.BLACK);
    	getContentPane().setForeground(Color.BLACK);
        setTitle("魔法學院登入");
        setSize(631, 454);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

        // 帳號
        JLabel label = new JLabel("帳號:");
        label.setBounds(128, 203, 66, 35);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("微軟正黑體", Font.BOLD | Font.ITALIC, 16));
        getContentPane().add(label);
        usernameField = new JTextField();
        usernameField.setBackground(Color.BLACK);
        usernameField.setForeground(Color.WHITE);
        usernameField.setBounds(366, 203, 157, 35);
        usernameField.setHorizontalAlignment(SwingConstants.CENTER);
        usernameField.setFont(new Font("微軟正黑體", Font.BOLD, 16));
        getContentPane().add(usernameField);

        // 密碼
        JLabel label_1 = new JLabel("密碼:");
        label_1.setBounds(128, 248, 47, 35);
        label_1.setForeground(Color.WHITE);
        label_1.setFont(new Font("微軟正黑體", Font.BOLD | Font.ITALIC, 16));
        getContentPane().add(label_1);
        passwordField = new JPasswordField();
        passwordField.setForeground(Color.WHITE);
        passwordField.setBackground(Color.BLACK);
        passwordField.setBounds(366, 248, 157, 35);
        passwordField.setHorizontalAlignment(SwingConstants.CENTER);
        passwordField.setFont(new Font("微軟正黑體", Font.BOLD, 16));
        getContentPane().add(passwordField);

        // 身份選擇
        JLabel label_2 = new JLabel("身份:");
        label_2.setBounds(128, 289, 44, 35);
        label_2.setForeground(Color.WHITE);
        label_2.setFont(new Font("微軟正黑體", Font.BOLD | Font.ITALIC, 16));
        getContentPane().add(label_2);
        roleBox = new JComboBox<>(new String[]{"學生", "老師"});
        roleBox.setBackground(Color.BLACK);
        roleBox.setForeground(Color.WHITE);
        roleBox.setBounds(366, 293, 151, 35);
        roleBox.setFont(new Font("微軟正黑體", Font.BOLD | Font.ITALIC, 16));
        getContentPane().add(roleBox);

        // 登入按鈕
        JButton loginBtn = new JButton("登入");
        loginBtn.setBackground(Color.BLACK);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBounds(76, 338, 166, 43);
        loginBtn.setFont(new Font("微軟正黑體", Font.BOLD | Font.ITALIC, 16));
        loginBtn.addActionListener(e -> doLogin());
        getContentPane().add(loginBtn);

        // 學生註冊按鈕
        JButton registerBtn = new JButton("學生註冊");
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setBackground(Color.BLACK);
        registerBtn.setBounds(366, 338, 157, 43);
        registerBtn.setFont(new Font("微軟正黑體", Font.BOLD | Font.ITALIC, 16));
        registerBtn.addActionListener(e -> new RegisterFrame().setVisible(true));
        getContentPane().add(registerBtn);
        
        JLabel magic = new JLabel("");
        magic.setBounds(20, 10, 597, 185);
        magic.setIcon(new ImageIcon(LoginFrame.class.getResource("/magic4.png")));
        getContentPane().add(magic);
        
        
    }

    // 登入邏輯
    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = (String) roleBox.getSelectedItem();

        if ("學生".equals(role)) {
            Student student = studentService.login(username, password); // login方法需比對hash
            if (student != null) {
                JOptionPane.showMessageDialog(this, "學生登入成功！學號：" + student.getStudentNo());
                new MainFrame(student); // 登入後進入 MainFrame
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "學生帳號不存在或密碼錯誤！請先註冊");
            }
        } else if ("老師".equals(role)) {
            Teacher teacher = teacherService.login(username, password);
            if (teacher != null) {
                JOptionPane.showMessageDialog(this, "老師登入成功！");
                new MainFrame(teacher); // 登入後進入 MainFrame
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "老師帳號不存在或密碼錯誤！");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
