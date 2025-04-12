package com.example.ungdungcoxuongkhop;

public class AdminAppointment {
    private int id;
    private int idNguoiDung;
    private int idBacSi;
    private String ngayGioKham;
    private String trangThai;
    private String ngayTao;

    public AdminAppointment(int id, int idNguoiDung, int idBacSi, String ngayGioKham, String trangThai, String ngayTao) {
        this.id = id;
        this.idNguoiDung = idNguoiDung;
        this.idBacSi = idBacSi;
        this.ngayGioKham = ngayGioKham;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
    }

    public int getId() {
        return id;
    }

    public int getIdNguoiDung() {
        return idNguoiDung;
    }

    public int getIdBacSi() {
        return idBacSi;
    }

    public String getNgayGioKham() {
        return ngayGioKham;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public String getNgayTao() {
        return ngayTao;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIdNguoiDung(int idNguoiDung) {
        this.idNguoiDung = idNguoiDung;
    }

    public void setIdBacSi(int idBacSi) {
        this.idBacSi = idBacSi;
    }

    public void setNgayGioKham(String ngayGioKham) {
        this.ngayGioKham = ngayGioKham;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public void setNgayTao(String ngayTao) {
        this.ngayTao = ngayTao;
    }
}
