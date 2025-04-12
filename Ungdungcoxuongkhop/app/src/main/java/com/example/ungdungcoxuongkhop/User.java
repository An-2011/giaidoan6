package com.example.ungdungcoxuongkhop;

public class User {
    private int id_nguoi_dung;
    private String ho_ten;
    private String email;
    private String so_dien_thoai;
    private String ngay_sinh;
    private String gioi_tinh;
    private String ngay_tao;

    // Constructor với các tham số
    public User(int id_nguoi_dung, String ho_ten, String email, String so_dien_thoai, String ngay_sinh, String gioi_tinh, String ngay_tao) {
        this.id_nguoi_dung = id_nguoi_dung;
        this.ho_ten = ho_ten;
        this.email = email;
        this.so_dien_thoai = so_dien_thoai;
        this.ngay_sinh = ngay_sinh;
        this.gioi_tinh = gioi_tinh;
        this.ngay_tao = ngay_tao;
    }

    // Getter và Setter
    public int getId_nguoi_dung() {
        return id_nguoi_dung;
    }

    public void setId_nguoi_dung(int id_nguoi_dung) {
        this.id_nguoi_dung = id_nguoi_dung;
    }

    public String getHo_ten() {
        return ho_ten;
    }

    public void setHo_ten(String ho_ten) {
        this.ho_ten = ho_ten;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSo_dien_thoai() {
        return so_dien_thoai;
    }

    public void setSo_dien_thoai(String so_dien_thoai) {
        this.so_dien_thoai = so_dien_thoai;
    }

    public String getNgay_sinh() {
        return ngay_sinh;
    }

    public void setNgay_sinh(String ngay_sinh) {
        this.ngay_sinh = ngay_sinh;
    }

    public String getGioi_tinh() {
        return gioi_tinh;
    }

    public void setGioi_tinh(String gioi_tinh) {
        this.gioi_tinh = gioi_tinh;
    }

    public String getNgay_tao() {
        return ngay_tao;
    }

    public void setNgay_tao(String ngay_tao) {
        this.ngay_tao = ngay_tao;
    }
}
