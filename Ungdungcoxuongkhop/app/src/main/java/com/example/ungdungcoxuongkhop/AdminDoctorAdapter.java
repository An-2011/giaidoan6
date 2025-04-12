package com.example.ungdungcoxuongkhop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminDoctorAdapter extends RecyclerView.Adapter<AdminDoctorAdapter.DoctorViewHolder> {

    private List<AdminDoctor> doctorList;
    private Context context;
    private OnDoctorActionListener listener;

    public interface OnDoctorActionListener {
        void onEditDoctor(AdminDoctor doctor);
        void onDeleteDoctor(AdminDoctor doctor);
    }

    public AdminDoctorAdapter(List<AdminDoctor> doctorList, Context context, OnDoctorActionListener listener) {
        this.doctorList = doctorList;
        this.context = context;
        this.listener = listener;
    }

    public void setDoctorList(List<AdminDoctor> doctorList) {
        this.doctorList = doctorList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        AdminDoctor doctor = doctorList.get(position);

        StringBuilder infoBuilder = new StringBuilder();
        infoBuilder.append("🆔 ID: ").append(doctor.getId()).append("\n")
                .append("👨‍⚕️ Họ tên: ").append(doctor.getHoTen()).append("\n")
                .append("🏥 Chuyên khoa: ").append(doctor.getChuyenKhoa()).append("\n")
                .append("🧠 Kinh nghiệm: ").append(doctor.getKinhNghiem()).append(" năm\n")
                .append("📍 Địa chỉ phòng khám: ").append(doctor.getDiaChi()).append("\n")
                .append("📞 Số điện thoại: ").append(doctor.getSoDienThoai()).append("\n")
                .append("🕓 Ngày tạo: ").append(doctor.getNgayTao());


        holder.tvInfo.setText(infoBuilder.toString());

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditDoctor(doctor);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteDoctor(doctor);
        });
    }


    @Override
    public int getItemCount() {
        return doctorList != null ? doctorList.size() : 0;
    }

    public static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView tvInfo;
        ImageButton btnEdit, btnDelete;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInfo = itemView.findViewById(R.id.tvDoctorInfoAdmin);
            btnEdit = itemView.findViewById(R.id.btnEditDoctorAdmin);
            btnDelete = itemView.findViewById(R.id.btnDeleteDoctorAdmin);
        }
    }
}
