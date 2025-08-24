package model;

import java.sql.Timestamp;

public class Student {
	
	private int id;
    private String studentNo;
    private String username;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String password;   // SHA256 hash
    private Timestamp createTime;

		public Student() {}

		public Student(int id, String studentNo, String username, String name, String phone, String email,
				String address, String password, Timestamp createTime) {
			
			this.id = id;
			this.studentNo = studentNo;
			this.username = username;
			this.name = name;
			this.phone = phone;
			this.email = email;
			this.address = address;
			this.password = password;
			this.createTime = createTime;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getStudentNo() {
			return studentNo;
		}

		public void setStudentNo(String studentNo) {
			this.studentNo = studentNo;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public Timestamp getCreateTime() {
			return createTime;
		}

		public void setCreateTime(Timestamp createTime) {
			this.createTime = createTime;
		}

}	