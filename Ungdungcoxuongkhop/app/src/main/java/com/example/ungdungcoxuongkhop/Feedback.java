package com.example.ungdungcoxuongkhop;

public class Feedback {
    private int id;
    private int userId; // ID của người dùng
    private int doctorId; // ID của bác sĩ
    private int score; // Điểm đánh giá
    private String comment; // Nhận xét của người dùng
    private String date; // Ngày tạo đánh giá
    private String userName; // Tên người dùng
    private String doctorName; // Tên bác sĩ

    public Feedback(int id, int userId, int doctorId, int score, String comment, String date, String userName, String doctorName) {
        this.id = id;
        this.userId = userId;
        this.doctorId = doctorId;
        this.score = score;
        this.comment = comment;
        this.date = date;
        this.userName = userName;
        this.doctorName = doctorName;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public int getScore() {
        return score;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }

    public String getUserName() {
        return userName;
    }

    public String getDoctorName() {
        return doctorName;
    }
}
