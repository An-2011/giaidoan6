    package com.example.ungdungcoxuongkhop;

    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;

    import java.util.ArrayList;

    public class AdminReviewAdapter extends RecyclerView.Adapter<AdminReviewAdapter.ViewHolder> {

        private final ArrayList<ReviewModel> list;
        private final DeleteClickListener deleteClickListener;

        public interface DeleteClickListener {
            void onDeleteClick(int reviewId);
        }

        public AdminReviewAdapter(ArrayList<ReviewModel> list, DeleteClickListener listener) {
            this.list = list;
            this.deleteClickListener = listener;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView id, idNguoiDung, tenNguoiDung, idBacSi, tenBacSi, diemSo, nhanXet, ngayTao;
            Button btnDelete;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                id = itemView.findViewById(R.id.review_id);
                idNguoiDung = itemView.findViewById(R.id.review_id_nguoi_dung);
                tenNguoiDung = itemView.findViewById(R.id.review_ten_nguoi_dung);
                idBacSi = itemView.findViewById(R.id.review_id_bac_si);
                tenBacSi = itemView.findViewById(R.id.review_ten_bac_si);
                diemSo = itemView.findViewById(R.id.review_diem_so);
                nhanXet = itemView.findViewById(R.id.review_nhan_xet);
                ngayTao = itemView.findViewById(R.id.review_ngay_tao);
                btnDelete = itemView.findViewById(R.id.review_btn_delete);
            }
        }

        @NonNull
        @Override
        public AdminReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_admin_review, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdminReviewAdapter.ViewHolder holder, int position) {
            ReviewModel model = list.get(position);

            holder.id.setText("🆔 ID: " + model.getId());
            holder.idNguoiDung.setText("👤 ID Người dùng: " + model.getIdNguoiDung());
            holder.tenNguoiDung.setText("🙍‍♂️ Tên người dùng: " + model.getTenNguoiDung());
            holder.idBacSi.setText("👨‍⚕️ ID Bác sĩ: " + model.getIdBacSi());
            holder.tenBacSi.setText("🧑‍⚕️ Tên bác sĩ: " + model.getTenBacSi());
            holder.diemSo.setText("⭐ Điểm số: " + model.getDiemSo());
            holder.nhanXet.setText("💬 Nhận xét: " + model.getNhanXet());
            holder.ngayTao.setText("📅 Ngày tạo: " + model.getNgayTao());


            holder.btnDelete.setOnClickListener(v -> deleteClickListener.onDeleteClick(model.getId()));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
