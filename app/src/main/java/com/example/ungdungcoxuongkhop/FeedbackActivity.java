package com.example.ungdungcoxuongkhop;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FeedbackAdapter adapter;
    private List<Feedback> feedbackList;
    private EditText edtComment, edtUserId, edtDoctorId;  // Thêm EditText cho user_id và doctor_id
    private RatingBar ratingBar;
    private Button btnSubmit;
    private TextView emptyText;
    private Button btnBack;

    private static final String SERVER_URL = "http://172.22.144.1/ungdung_api/danhgia.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        recyclerView = findViewById(R.id.recyclerView);
        edtComment = findViewById(R.id.edtComment);
        edtUserId = findViewById(R.id.edtUserId);  // Khởi tạo EditText cho user_id
        edtDoctorId = findViewById(R.id.edtDoctorId);  // Khởi tạo EditText cho doctor_id
        ratingBar = findViewById(R.id.ratingBar);
        btnSubmit = findViewById(R.id.btnSubmit);
        emptyText = findViewById(R.id.emptyText);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        feedbackList = new ArrayList<>();
        adapter = new FeedbackAdapter(feedbackList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadFeedback();

        btnSubmit.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {
        String comment = edtComment.getText().toString().trim();
        int score = (int) ratingBar.getRating();

        if (score == 0) {
            Toast.makeText(this, "❌ Vui lòng chọn số sao!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy user_id và doctor_id từ các ô nhập liệu
        String userIdStr = edtUserId.getText().toString().trim();
        String doctorIdStr = edtDoctorId.getText().toString().trim();

        if (userIdStr.isEmpty() || doctorIdStr.isEmpty()) {
            Toast.makeText(this, "❌ Vui lòng nhập đầy đủ user_id và doctor_id!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Chuyển user_id và doctor_id thành kiểu số nguyên
        int userId = Integer.parseInt(userIdStr);
        int doctorId = Integer.parseInt(doctorIdStr);

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("action", "add");
            jsonObject.put("user_id", userId);  // Gửi user_id
            jsonObject.put("id_bac_si", doctorId);  // Thay đổi thành 'id_bac_si'
            jsonObject.put("diem_so", score);
            jsonObject.put("nhan_xet", comment);

            Log.d("DEBUG", "📡 Gửi JSON lên API: " + jsonObject.toString());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, SERVER_URL, jsonObject,
                    response -> {
                        try {
                            Log.d("DEBUG", "✅ Phản hồi từ API: " + response.toString());
                            String status = response.getString("status");
                            Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            if (status.equals("success")) {
                                loadFeedback();
                                edtComment.setText("");
                                ratingBar.setRating(0);
                                edtUserId.setText("");  // Reset user_id
                                edtDoctorId.setText("");  // Reset doctor_id
                            }
                        } catch (JSONException e) {
                            Log.e("JSONError", "Lỗi xử lý JSON", e);
                        }
                    },
                    error -> {
                        Log.e("VolleyError", "🚨 Lỗi kết nối đến server!", error);
                        Toast.makeText(this, "Lỗi kết nối đến máy chủ!", Toast.LENGTH_SHORT).show();
                    });

            MySingleton.getInstance(this).addToRequestQueue(request);
        } catch (JSONException e) {
            Log.e("JSONError", "Lỗi tạo JSON!", e);
        }
    }

    private void loadFeedback() {
        String doctorIdStr = edtDoctorId.getText().toString().trim();

        // Kiểm tra xem doctorId có trống không
        if (doctorIdStr.isEmpty()) {
            Toast.makeText(this, "❌ Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("action", "fetch_by_doctor");
            jsonObject.put("id_bac_si", doctorIdStr);  // Đảm bảo gửi đúng tham số 'id_bac_si'

            Log.d("DEBUG", "📡 Gửi yêu cầu lấy đánh giá: " + jsonObject.toString());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, SERVER_URL, jsonObject,
                    response -> {
                        try {
                            Log.d("DEBUG", "✅ Phản hồi từ API: " + response.toString());
                            feedbackList.clear();

                            // Kiểm tra 'status' trước khi xử lý dữ liệu
                            String status = response.optString("status", "error");
                            if ("success".equals(status)) {
                                JSONArray data = response.optJSONArray("data");
                                if (data != null && data.length() > 0) {
                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject obj = data.getJSONObject(i);
                                        feedbackList.add(new Feedback(
                                                obj.getInt("id"),
                                                obj.getInt("id_nguoi_dung"),
                                                obj.getInt("id_bac_si"),  // Sửa lại thành 'id' cho bác sĩ
                                                obj.getInt("diem_so"),
                                                obj.getString("nhan_xet"),
                                                obj.getString("ngay_tao"),
                                                obj.getString("ten_nguoi_dung"),  // Thêm tên người dùng
                                                obj.getString("ten_bac_si")  // Thêm tên bác sĩ
                                        ));
                                    }
                                    adapter.notifyDataSetChanged();
                                    emptyText.setVisibility(feedbackList.isEmpty() ? View.VISIBLE : View.GONE);
                                    recyclerView.setVisibility(feedbackList.isEmpty() ? View.GONE : View.VISIBLE);
                                } else {
                                    // Nếu không có dữ liệu, hiển thị thông báo
                                    emptyText.setText("Không có đánh giá nào.");
                                    emptyText.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                                }
                            } else {
                                // Nếu status không phải 'success', hiển thị thông báo lỗi
                                emptyText.setText(response.optString("message", "Lỗi không xác định"));
                                emptyText.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            Log.e("JSONError", "Lỗi xử lý JSON!", e);
                        }
                    },
                    error -> {
                        // Log chi tiết về lỗi kết nối để dễ dàng theo dõi nguyên nhân
                        Log.e("VolleyError", "🚨 Lỗi kết nối server! Chi tiết lỗi: ", error);
                        if (error.networkResponse != null) {
                            Log.e("VolleyError", "Response Code: " + error.networkResponse.statusCode);
                        }
                        if (error.getCause() != null) {
                            Log.e("VolleyError", "Cause: " + error.getCause().toString());
                        }

                        // Hiển thị thông báo cho người dùng
                        Toast.makeText(this, "Lỗi tải đánh giá! Kiểm tra kết nối mạng.", Toast.LENGTH_SHORT).show();
                    });

            MySingleton.getInstance(this).addToRequestQueue(request);
        } catch (JSONException e) {
            Log.e("JSONError", "Lỗi tạo JSON!", e);
        }
    }
}
