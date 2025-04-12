package com.example.ungdungcoxuongkhop;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdminNotificationAdapter extends RecyclerView.Adapter<AdminNotificationAdapter.ViewHolder> {

    private Context context;
    private ArrayList<NotificationModel> list;

    public AdminNotificationAdapter(Context context, ArrayList<NotificationModel> list) {
        this.context = context;
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvNguoiNhan, tvTieuDe, tvNoiDung, tvTrangThai, tvLoai, tvNgayTao;
        ImageView btnXoa;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvNguoiNhan = itemView.findViewById(R.id.tvNguoiNhan);
            tvTieuDe = itemView.findViewById(R.id.tvTieuDe);
            tvNoiDung = itemView.findViewById(R.id.tvNoiDung);
            tvTrangThai = itemView.findViewById(R.id.tvTrangThai);
            tvLoai = itemView.findViewById(R.id.tvLoai);
            tvNgayTao = itemView.findViewById(R.id.tvNgayTao);
            btnXoa = itemView.findViewById(R.id.btnXoa);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel item = list.get(position);

        holder.tvId.setText("🆔 ID: " + item.getId());
        holder.tvNguoiNhan.setText("👤 ID Người nhận: " + item.getIdNguoiDung());
        holder.tvTieuDe.setText("📌 Tiêu đề: " + item.getTieuDe());
        holder.tvNoiDung.setText("📝 Nội dung: " + item.getNoiDung());
        holder.tvTrangThai.setText("🔄 Trạng thái: " + item.getTrangThai());
        holder.tvLoai.setText("📂 Loại: " + item.getLoai());
        holder.tvNgayTao.setText("📅 Ngày gửi: " + item.getNgayTao());


        holder.btnXoa.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận xoá")
                    .setMessage("Bạn có chắc chắn muốn xoá thông báo này?")
                    .setPositiveButton("Xoá", (dialog, which) -> {
                        xoaThongBao(item.getId(), position);
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
        });
    }

    private void xoaThongBao(int id, int position) {
        String url = "http://172.22.144.1/ungdung_api/get_all_notifications.php";

        JSONObject params = new JSONObject();
        try {
            params.put("action", "delete");  // Sử dụng action phù hợp với API
            params.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Lỗi dữ liệu!", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            list.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "Đã xoá thông báo!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Xoá thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Lỗi phản hồi!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(context, "Không thể kết nối server!", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(context).add(request);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
