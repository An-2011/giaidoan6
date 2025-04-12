package com.example.ungdungcoxuongkhop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    public interface OnUserActionListener {
        void onEdit(User user);   // Sửa người dùng
        void onDelete(User user); // Xóa người dùng
    }

    private final List<User> userList;
    private final OnUserActionListener listener;

    // Constructor để nhận danh sách người dùng và listener
    public UserAdapter(List<User> userList, OnUserActionListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    // UserViewHolder để chứa các item của người dùng
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, phone, birth, gender, created;
        ImageButton btnEdit, btnDelete;

        // Khởi tạo các view trong item
        public UserViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.txtName);
            email = view.findViewById(R.id.txtEmail);
            phone = view.findViewById(R.id.txtPhone);
            birth = view.findViewById(R.id.txtBirth);
            gender = view.findViewById(R.id.txtGender);
            created = view.findViewById(R.id.txtCreated);
            btnEdit = view.findViewById(R.id.btnEdit);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = userList.get(position);

        // Gán dữ liệu cho các view
        holder.name.setText("👤 " + user.getId_nguoi_dung() + " - " + user.getHo_ten());
        holder.email.setText("📧 " + user.getEmail());
        holder.phone.setText("📱 " + user.getSo_dien_thoai());
        holder.birth.setText("🎂 Ngày sinh: " + user.getNgay_sinh());
        holder.gender.setText("⚧️ Giới tính: " + user.getGioi_tinh());
        holder.created.setText("📅 Ngày tạo: " + user.getNgay_tao());

        // Đặt sự kiện cho nút Sửa
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(user);  // Gọi hàm onEdit từ listener
            }
        });

        // Đặt sự kiện cho nút Xóa
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(user);  // Gọi hàm onDelete từ listener
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
