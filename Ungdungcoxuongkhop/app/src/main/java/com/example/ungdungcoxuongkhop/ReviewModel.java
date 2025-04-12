package com.example.ungdungcoxuongkhop;

public class ReviewModel {
    private int id;
    private int idNguoiDung;
    private String tenNguoiDung;
    private int idBacSi;
    private String tenBacSi;
    private int diemSo;
    private String nhanXet;
    private String ngayTao;

    public ReviewModel(int id, int idNguoiDung, String tenNguoiDung,
                       int idBacSi, String tenBacSi, int diemSo,
                       String nhanXet, String ngayTao) {
        this.id = id;
        this.idNguoiDung = idNguoiDung;
        this.tenNguoiDung = tenNguoiDung;
        this.idBacSi = idBacSi;
        this.tenBacSi = tenBacSi;
        this.diemSo = diemSo;
        this.nhanXet = nhanXet;
        this.ngayTao = ngayTao;
    }

    public int getId() {
        return id;
    }

    public int getIdNguoiDung() {
        return idNguoiDung;
    }

    public String getTenNguoiDung() {
        return tenNguoiDung;
    }

    public int getIdBacSi() {
        return idBacSi;
    }

    public String getTenBacSi() {
        return tenBacSi;
    }

    public int getDiemSo() {
        return diemSo;
    }

    public String getNhanXet() {
        return nhanXet;
    }

    public String getNgayTao() {
        return ngayTao;
    }
}
