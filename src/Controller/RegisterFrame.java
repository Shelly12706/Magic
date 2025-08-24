package Controller;

import model.Student;
import service.StudentService;
import service.impl.StudentServiceImpl;
import util.CodeGenerator;
import util.PasswordUtil;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private JTextField usernameField, nameField, emailField, phoneField, addressField;
    private JPasswordField passwordField;
    private StudentService studentService = new StudentServiceImpl();

    public RegisterFrame() {
        setTitle("學生註冊");
        setSize(544, 472);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

        JLabel label = new JLabel("帳號:");
        label.setFont(new Font("微軟正黑體", Font.BOLD | Font.ITALIC, 16));
        label.setBounds(54, 9, 142, 44);
        getContentPane().add(label); usernameField = new JTextField(); 
 usernameField.setHorizontalAlignment(SwingConstants.CENTER);
 usernameField.setFont(new Font("微軟正黑體", Font.BOLD, 16));
 usernameField.setBounds(269, 10, 225, 44);getContentPane().add(usernameField);
        JLabel label_1 = new JLabel("姓名:");
        label_1.setFont(new Font("微軟正黑體", Font.BOLD | Font.ITALIC, 16));
        label_1.setBounds(54, 63, 79, 53);
        getContentPane().add(label_1); nameField = new JTextField(); 
 nameField.setHorizontalAlignment(SwingConstants.CENTER);
 nameField.setFont(new Font("微軟正黑體", Font.BOLD, 16));
 nameField.setBounds(269, 73, 225, 44);getContentPane().add(nameField);
        JLabel label_2 = new JLabel("密碼:");
        label_2.setFont(new Font("新細明體", Font.BOLD | Font.ITALIC, 16));
        label_2.setBounds(54, 126, 110, 54);
        getContentPane().add(label_2); passwordField = new JPasswordField(); 
 passwordField.setHorizontalAlignment(SwingConstants.CENTER);
 passwordField.setFont(new Font("微軟正黑體", Font.BOLD, 16));
 passwordField.setBounds(269, 136, 225, 44);getContentPane().add(passwordField);
        JLabel label_3 = new JLabel("E-mail:");
        label_3.setFont(new Font("微軟正黑體", Font.BOLD | Font.ITALIC, 16));
        label_3.setBounds(54, 190, 110, 53);
        getContentPane().add(label_3); emailField = new JTextField(); 
 emailField.setHorizontalAlignment(SwingConstants.CENTER);
 emailField.setFont(new Font("微軟正黑體", Font.BOLD, 16));
 emailField.setBounds(269, 199, 225, 44);getContentPane().add(emailField);
        JLabel label_4 = new JLabel("Phone:");
        label_4.setFont(new Font("微軟正黑體", Font.BOLD | Font.ITALIC, 16));
        label_4.setBounds(54, 253, 110, 53);
        getContentPane().add(label_4); phoneField = new JTextField(); 
 phoneField.setHorizontalAlignment(SwingConstants.CENTER);
 phoneField.setFont(new Font("微軟正黑體", Font.BOLD, 16));
 phoneField.setBounds(269, 262, 225, 44);getContentPane().add(phoneField);
        JLabel label_5 = new JLabel("Address:");
        label_5.setFont(new Font("微軟正黑體", Font.BOLD | Font.ITALIC, 16));
        label_5.setBounds(45, 316, 119, 53);
        getContentPane().add(label_5); addressField = new JTextField(); 
 addressField.setHorizontalAlignment(SwingConstants.CENTER);
 addressField.setFont(new Font("微軟正黑體", Font.BOLD, 16));
 addressField.setBounds(269, 325, 225, 44);getContentPane().add(addressField);

        JButton registerBtn = new JButton("註冊");
        registerBtn.setFont(new Font("微軟正黑體", Font.BOLD, 16));
        registerBtn.setBounds(10, 379, 214, 53);
        registerBtn.addActionListener(e -> doRegister());
        getContentPane().add(registerBtn);

        JButton cancelBtn = new JButton("取消");
        cancelBtn.setFont(new Font("微軟正黑體", Font.BOLD, 16));
        cancelBtn.setBounds(269, 379, 225, 53);
        cancelBtn.addActionListener(e -> dispose());
        getContentPane().add(cancelBtn);
    }

    private void doRegister() {
        String username = usernameField.getText().trim();
        String name = nameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        if(username.isEmpty() || name.isEmpty() || password.isEmpty()){
            JOptionPane.showMessageDialog(this,"帳號/姓名/密碼不可為空");
            return;
        }

        int nextId = studentService.getNextId(); // 用service取下一個學生流水號
        String studentNo = CodeGenerator.generateStudentNo(nextId);

        Student student = new Student();
        student.setStudentNo(studentNo);
        student.setUsername(username);
        student.setName(name);
        student.setPassword(PasswordUtil.hash(password));
        student.setEmail(email);
        student.setPhone(phone);
        student.setAddress(address);

        boolean success = studentService.createStudent(student);
        if(success){
            JOptionPane.showMessageDialog(this,"註冊成功！學號：" + studentNo);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this,"註冊失敗，帳號可能已存在");
        }
    }
}
