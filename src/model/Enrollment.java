package model;

import java.sql.Timestamp;

public class Enrollment {
	
    private int enrollId;
    private String studentNo;  // FK to student
    private int courseId;      // FK to course
    private Timestamp enrollTime;
    private Double score;
    private Timestamp scoreTime;

	public Enrollment() {}

	public Enrollment(int enrollId, String studentNo, int courseId, Timestamp enrollTime, Double score,
			Timestamp scoreTime) {
		

		this.enrollId = enrollId;
		this.studentNo = studentNo;
		this.courseId = courseId;
		this.enrollTime = enrollTime;
		this.score = score;
		this.scoreTime = scoreTime;
	}

	
	public int getEnrollId() {
		return enrollId;
	}

	public void setEnrollId(int enrollId) {
		this.enrollId = enrollId;
	}

	public String getStudentNo() {
		return studentNo;
	}

	public void setStudentNo(String studentNo) {
		this.studentNo = studentNo;
	}

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	public Timestamp getEnrollTime() {
		return enrollTime;
	}

	public void setEnrollTime(Timestamp enrollTime) {
		this.enrollTime = enrollTime;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public Timestamp getScoreTime() {
		return scoreTime;
	}

	public void setScoreTime(Timestamp scoreTime) {
		this.scoreTime = scoreTime;
	}

	
}
