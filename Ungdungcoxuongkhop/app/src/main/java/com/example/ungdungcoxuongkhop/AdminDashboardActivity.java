package com.example.ungdungcoxuongkhop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {
    private RecyclerView recyclerViewAdmin;
    private FunctionAdapter adminAdapter;
    private List<FunctionItem> adminFunctions;
    private Button btnBack; // Nút quay lại

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        recyclerViewAdmin = findViewById(R.id.recyclerViewAdmin);
        recyclerViewAdmin.setLayoutManager(new GridLayoutManager(this, 2));

        btnBack = findViewById(R.id.btnBack); // Khởi tạo nút quay lại

        // Thiết lập sự kiện cho nút quay lại
        btnBack.setOnClickListener(v -> {
            onBackPressed(); // Quay lại màn hình trước đó
        });

        setupAdminFunctions();
    }

    private void setupAdminFunctions() {
        adminFunctions = new ArrayList<>();

        adminFunctions.add(new FunctionItem("Quản lý người dùng", R.drawable.ic_users, AdminUserActivity.class));
        adminFunctions.add(new FunctionItem("Thống kê tổng quan", R.drawable.ic_statistics, StatisticsActivity.class));
        adminFunctions.add(new FunctionItem("Gửi thông báo", R.drawable.ic_notification, AdminNotificationListActivity.class));
        adminFunctions.add(new FunctionItem("Quản lý lịch khám", R.drawable.lichkham,AdminAppointmentListActivity.class));
        adminFunctions.add(new FunctionItem("Quản lý bác sĩ", R.drawable.ic_doctor, AdminDoctorListActivity.class));
        adminFunctions.add(new FunctionItem("Phản hồi & đánh giá", R.drawable.ic_feedback, AdminReviewListActivity.class));

        adminAdapter = new FunctionAdapter(this, adminFunctions);
        recyclerViewAdmin.setAdapter(adminAdapter);

        adminAdapter.setOnItemClickListener(item -> {
            if (item.getActivityClass() != null) {
                Intent intent = new Intent(AdminDashboardActivity.this, item.getActivityClass());
                startActivity(intent);
            }
        });
    }
}
