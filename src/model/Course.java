package model;


public class Course {
	
	private int courseId;
    private String courseCode; // 新增課程編號欄位
    private String name;
    private int credits;
    private int dayOfWeek;    // 1=Monday ... 7=Sunday
    private String timeSlot;
    private String teacherNo; // FK to teacher

    public Course() {}

	public Course(int courseId, String courseCode, String name, int credits, int dayOfWeek, String timeSlot,
			String teacherNo) {
		
		this.courseId = courseId;
		this.courseCode = courseCode;
		this.name = name;
		this.credits = credits;
		this.dayOfWeek = dayOfWeek;
		this.timeSlot = timeSlot;
		this.teacherNo = teacherNo;
	}

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCredits() {
		return credits;
	}

	public void setCredits(int credits) {
		this.credits = credits;
	}

	public int getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public String getTimeSlot() {
		return timeSlot;
	}

	public void setTimeSlot(String timeSlot) {
		this.timeSlot = timeSlot;
	}

	public String getTeacherNo() {
		return teacherNo;
	}

	public void setTeacherNo(String teacherNo) {
		this.teacherNo = teacherNo;
	}
}