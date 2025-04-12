package com.example.ungdungcoxuongkhop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {
    private List<Feedback> feedbackList;

    public FeedbackAdapter(List<Feedback> feedbackList) {
        if (feedbackList != null) {
            this.feedbackList = feedbackList;
        } else {
            this.feedbackList = new ArrayList<>(); // Đảm bảo không có lỗi khi danh sách rỗng
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (feedbackList != null && !feedbackList.isEmpty()) {
            Feedback feedback = feedbackList.get(position);

            // Hiển thị thông tin người dùng và bác sĩ
            holder.txtUserId.setText("👤 Người dùng ID: " + feedback.getUserId());
            holder.txtDoctorId.setText("👨‍⚕️ Bác sĩ ID: " + feedback.getDoctorId());

            // Hiển thị điểm số (chuyển thành sao)
            holder.txtScore.setText("⭐ Điểm: " + "⭐".repeat(feedback.getScore()));

            // Hiển thị nhận xét
            holder.txtComment.setText("💬 Nhận xét: " + feedback.getComment());

            // Định dạng ngày tháng
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                // Chuyển đổi ngày
                String formattedDate = outputFormat.format(inputFormat.parse(feedback.getDate()));
                holder.txtDate.setText("📅 Ngày: " + formattedDate);
            } catch (Exception e) {
                holder.txtDate.setText("📅 Ngày: Không xác định");
            }
        } else {
            // Nếu không có dữ liệu, có thể hiển thị một thông báo khác hoặc làm trống các TextView
            holder.txtUserId.setText("Không có thông tin.");
            holder.txtDoctorId.setText("Không có thông tin.");
            holder.txtScore.setText("Không có thông tin.");
            holder.txtComment.setText("Không có thông tin.");
            holder.txtDate.setText("Không có thông tin.");
        }
    }

    @Override
    public int getItemCount() {
        return feedbackList == null ? 0 : feedbackList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUserId, txtDoctorId, txtScore, txtComment, txtDate;

        public ViewHolder(View itemView) {
            super(itemView);
            txtUserId = itemView.findViewById(R.id.txtUserId);
            txtDoctorId = itemView.findViewById(R.id.txtDoctorId);
            txtScore = itemView.findViewById(R.id.txtScore);
            txtComment = itemView.findViewById(R.id.txtComment);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }
}
