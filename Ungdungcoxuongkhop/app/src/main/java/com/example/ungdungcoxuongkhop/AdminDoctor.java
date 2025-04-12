package com.example.ungdungcoxuongkhop;

public class AdminDoctor {
    private int id;
    private String hoTen;
    private String chuyenKhoa;
    private int kinhNghiem;
    private String diaChiPhongKham ;
    private String soDienThoai;
    private String ngayTao;

    public AdminDoctor(int id, String hoTen, String chuyenKhoa, int kinhNghiem, String diaChiPhongKham, String soDienThoai, String ngayTao) {
        this.id = id;
        this.hoTen = hoTen;
        this.chuyenKhoa = chuyenKhoa;
        this.kinhNghiem = kinhNghiem;
        this.diaChiPhongKham = diaChiPhongKham;
        this.soDienThoai = soDienThoai;
        this.ngayTao = ngayTao;
    }

    public int getId() {
        return id;
    }

    public String getHoTen() {
        return hoTen;
    }

    public String getChuyenKhoa() {
        return chuyenKhoa;
    }

    public int getKinhNghiem() {
        return kinhNghiem;
    }

    public String getDiaChi() {
        return diaChiPhongKham;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public String getNgayTao() {
        return ngayTao;
    }
}
