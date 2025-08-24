package model;

import java.sql.Timestamp;

public class Teacher {
	private int id;
    private String teacherNo;
    private String username;
    private String name;
    private String phone;
    private String password;   // SHA256 hash
    private Timestamp createTime;

	public Teacher() {}

	public Teacher(int id, String teacherNo, String username, String name, String phone, String password,
			Timestamp createTime) {
		
		this.id = id;
		this.teacherNo = teacherNo;
		this.username = username;
		this.name = name;
		this.phone = phone;
		this.password = password;
		this.createTime = createTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTeacherNo() {
		return teacherNo;
	}

	public void setTeacherNo(String teacherNo) {
		this.teacherNo = teacherNo;
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
