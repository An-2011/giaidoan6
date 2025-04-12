package com.example.ungdungcoxuongkhop;

public class NotificationModel {
    private int id;
    private int idNguoiDung;
    private String tieuDe;
    private String noiDung;
    private String ngayTao;
    private String loai;
    private String nguoiNhan;
    private String trangThai; // Thêm trạng thái

    public NotificationModel(int id, int idNguoiDung, String tieuDe, String noiDung, String ngayTao, String loai, String nguoiNhan, String trangThai) {
        this.id = id;
        this.idNguoiDung = idNguoiDung;
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
        this.ngayTao = ngayTao;
        this.loai = loai;
        this.nguoiNhan = nguoiNhan;
        this.trangThai = trangThai;
    }

    // Getter
    public int getId() {
        return id;
    }

    public int getIdNguoiDung() {
        return idNguoiDung;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public String getNgayTao() {
        return ngayTao;
    }

    public String getLoai() {
        return loai;
    }

    public String getNguoiNhan() {
        return nguoiNhan;
    }

    public String getTrangThai() {
        return trangThai;
    }

    // Setter
    public void setId(int id) {
        this.id = id;
    }

    public void setIdNguoiDung(int idNguoiDung) {
        this.idNguoiDung = idNguoiDung;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public void setNgayTao(String ngayTao) {
        this.ngayTao = ngayTao;
    }

    public void setLoai(String loai) {
        this.loai = loai;
    }

    public void setNguoiNhan(String nguoiNhan) {
        this.nguoiNhan = nguoiNhan;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
