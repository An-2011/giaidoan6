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

public class AdminAppointmentAdapter extends RecyclerView.Adapter<AdminAppointmentAdapter.AppointmentViewHolder> {

    private List<AdminAppointment> appointmentList;
    private final Context context;
    private final OnAppointmentActionListener listener;

    public interface OnAppointmentActionListener {
        void onDelete(AdminAppointment appointment);
        void onUpdateStatus(AdminAppointment appointment);
    }

    public AdminAppointmentAdapter(List<AdminAppointment> appointmentList, Context context, OnAppointmentActionListener listener) {
        this.appointmentList = appointmentList;
        this.context = context;
        this.listener = listener;
    }

    public void setAppointmentList(List<AdminAppointment> list) {
        this.appointmentList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        AdminAppointment appointment = appointmentList.get(position);
        String info = "🆔 ID: " + appointment.getId() + "\n"
                + "👤 ID Người dùng: " + appointment.getIdNguoiDung() + "\n"
                + "👨‍⚕️ ID Bác sĩ: " + appointment.getIdBacSi() + "\n"
                + "📆 Ngày giờ khám: " + appointment.getNgayGioKham() + "\n"
                + "🔄 Trạng thái: " + appointment.getTrangThai() + "\n"
                + "🕓 Ngày tạo: " + appointment.getNgayTao();


        holder.tvAppointmentInfo.setText(info);

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(appointment);
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onUpdateStatus(appointment);
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList != null ? appointmentList.size() : 0;
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvAppointmentInfo;
        ImageButton btnDelete, btnEdit;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAppointmentInfo = itemView.findViewById(R.id.tvAppointmentInfo);
            btnDelete = itemView.findViewById(R.id.btnDeleteAppointment);
            btnEdit = itemView.findViewById(R.id.btnEditAppointment);
        }
    }
}
