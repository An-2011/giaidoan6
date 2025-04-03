package com.example.ungdungcoxuongkhop;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class NotificationActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView emptyText;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;
    private EditText edtTitle, edtMessage, edtUserId;
    private Button btnSendNotification;

    private static final String SERVER_URL = "http://172.22.144.1/ungdung_api/guithongbaovsuudai.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerView = findViewById(R.id.recyclerView);
        emptyText = findViewById(R.id.emptyText);
        edtTitle = findViewById(R.id.edtTitle);
        edtMessage = findViewById(R.id.edtMessage);
        edtUserId = findViewById(R.id.edtUserId); // Thêm ô nhập ID người dùng
        btnSendNotification = findViewById(R.id.btnSendNotification);

        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList, this::markAsRead); // Gọi phương thức markAsRead
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadNotifications();
        btnSendNotification.setOnClickListener(v -> sendNotification());
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void sendNotification() {
        String title = edtTitle.getText().toString().trim();
        String message = edtMessage.getText().toString().trim();
        String userIdInput = edtUserId.getText().toString().trim();

        if (title.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("action", "send");
            jsonObject.put("tieu_de", title);
            jsonObject.put("noi_dung", message);

            if (!userIdInput.isEmpty()) {
                jsonObject.put("user_id", Integer.parseInt(userIdInput));
            }

            Log.d("DEBUG", "📡 JSON gửi lên server: " + jsonObject.toString());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, SERVER_URL, jsonObject,
                    response -> {
                        try {
                            String status = response.getString("status");
                            String serverMessage = response.getString("message");
                            Toast.makeText(NotificationActivity.this, serverMessage, Toast.LENGTH_SHORT).show();

                            if (status.equals("success")) {
                                loadNotifications();
                            }
                        } catch (JSONException e) {
                            Log.e("JSONError", "Lỗi xử lý JSON", e);
                        }
                    },
                    error -> Log.e("VolleyError", "🚨 Lỗi kết nối đến server!", error));

            MySingleton.getInstance(this).addToRequestQueue(request);
        } catch (JSONException e) {
            Log.e("JSONError", "Lỗi tạo JSON", e);
        }
    }

    private void loadNotifications() {
        try {
            JSONObject requestData = new JSONObject();
            requestData.put("action", "fetch");

            SharedPreferences preferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            int userId = preferences.getInt("user_id", -1);
            if (userId == -1) {
                emptyText.setText("Lỗi: Không tìm thấy user_id.");
                emptyText.setVisibility(View.VISIBLE);
                return;
            }

            requestData.put("user_id", userId);
            Log.d("DEBUG", "📡 Gửi yêu cầu lấy thông báo: " + requestData.toString());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, SERVER_URL, requestData,
                    response -> {
                        Log.d("DEBUG", "✅ Phản hồi từ server: " + response.toString());

                        notificationList.clear();
                        try {
                            if (response.getString("status").equals("success")) {
                                JSONArray notificationsArray = response.getJSONArray("data");
                                for (int i = 0; i < notificationsArray.length(); i++) {
                                    JSONObject obj = notificationsArray.getJSONObject(i);
                                    Notification notification = new Notification(
                                            obj.getInt("id"),
                                            obj.getString("tieu_de"),
                                            obj.getString("noi_dung"),
                                            obj.getString("ngay_tao"),
                                            obj.getString("trang_thai")
                                    );
                                    notificationList.add(notification);
                                }
                                adapter.notifyDataSetChanged();
                                emptyText.setVisibility(notificationList.isEmpty() ? View.VISIBLE : View.GONE);
                                recyclerView.setVisibility(notificationList.isEmpty() ? View.GONE : View.VISIBLE);
                            } else {
                                emptyText.setText(response.getString("message"));
                                emptyText.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            Log.e("JSONError", "Lỗi xử lý JSON!", e);
                        }
                    },
                    error -> Log.e("VolleyError", "🚨 Lỗi kết nối server!", error));

            MySingleton.getInstance(this).addToRequestQueue(request);
        } catch (JSONException e) {
            Log.e("JSONError", "Lỗi tạo JSON khi tải thông báo!", e);
        }
    }

    // ✅ Thêm phương thức markAsRead để đánh dấu thông báo là đã đọc
    public void markAsRead(int notificationId) {
        try {
            JSONObject requestData = new JSONObject();
            requestData.put("action", "mark_read");
            requestData.put("notification_id", notificationId);

            Log.d("DEBUG", "📡 Gửi yêu cầu đánh dấu đã đọc: " + requestData.toString());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, SERVER_URL, requestData,
                    response -> {
                        try {
                            String status = response.getString("status");
                            if (status.equals("success")) {
                                Toast.makeText(NotificationActivity.this, "Đã đánh dấu Đã đọc", Toast.LENGTH_SHORT).show();
                                loadNotifications(); // Cập nhật danh sách sau khi đánh dấu
                            }
                        } catch (JSONException e) {
                            Log.e("JSONError", "Lỗi xử lý JSON!", e);
                        }
                    },
                    error -> Log.e("VolleyError", "🚨 Lỗi kết nối đến server!", error));

            MySingleton.getInstance(this).addToRequestQueue(request);
        } catch (JSONException e) {
            Log.e("JSONError", "Lỗi tạo JSON!", e);
        }
    }
}
