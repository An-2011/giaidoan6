package com.example.ungdungcoxuongkhop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewFunctions;
    private FunctionAdapter adapter;
    private List<FunctionItem> functionList;
    private ConstraintLayout mainLayout;
    private TextView appTitle;
    private ImageView imageView;

    private static final String API_URL = "http://172.22.144.1/ungdung_api/get_system_settings.php";
    private static final String IMAGE_URL = "https://example.com/your-image.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = findViewById(R.id.mainLayout);
        appTitle = findViewById(R.id.appTitle);
        imageView = findViewById(R.id.imageView);

        setupRecyclerView();
        setupFunctionList();
        registerFirebaseNotifications();

        // Tải ảnh từ URL vào ImageView bằng Glide
        Glide.with(this)
                .load(IMAGE_URL)
                .into(imageView);

        // Gọi API để lấy và cập nhật cài đặt hệ thống
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String result = doInBackground(API_URL);
            runOnUiThread(() -> updateUI(result));
        });
    }

    private void setupRecyclerView() {
        recyclerViewFunctions = findViewById(R.id.recyclerViewFunctions);
        recyclerViewFunctions.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void setupFunctionList() {
        functionList = new ArrayList<>();
        functionList.add(new FunctionItem("Tìm hiểu bệnh Gout", R.drawable.timhieubenhgout, InfoActivity.class));
        functionList.add(new FunctionItem("Tự kiểm tra nguy cơ", R.drawable.ic_medical_logo, SelfCheckActivity.class));
        functionList.add(new FunctionItem("Đặt lịch khám", R.drawable.ic_health_journal, BookAppointmentActivity.class));
        functionList.add(new FunctionItem("Nhật ký sức khỏe", R.drawable.nhatkysuckhoe, HealthLogActivity.class));
        functionList.add(new FunctionItem("Tư vấn trực tuyến", R.drawable.tuvantructiep, ChatActivity.class));
        functionList.add(new FunctionItem("Thông báo & Ưu đãi", R.drawable.thongbaouudai, NotificationActivity.class));
        functionList.add(new FunctionItem("Phản hồi & Đánh giá", R.drawable.phanhoidanhgia, FeedbackActivity.class));
        functionList.add(new FunctionItem("Phát triển tương lai", R.drawable.phattrientuonglai, FutureDevelopmentActivity.class));

        adapter = new FunctionAdapter(this, functionList);
        recyclerViewFunctions.setAdapter(adapter);

        adapter.setOnItemClickListener(item -> {
            int position = functionList.indexOf(item);
            handleItemClick(position);
        });
    }

    private void handleItemClick(int position) {
        if (position >= 0 && position < functionList.size()) {
            FunctionItem item = functionList.get(position);
            if (item.getActivityClass() != null) {
                Intent intent = new Intent(MainActivity.this, item.getActivityClass());
                if (item.getActivityClass() == FeedbackActivity.class) {
                    SharedPreferences preferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    int doctorId = preferences.getInt("id_bac_si", -1);
                    if (doctorId != -1) {
                        intent.putExtra("id_bac_si", doctorId);
                    }
                }
                startActivity(intent);
            }
        }
    }

    private void registerFirebaseNotifications() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("FCM", "Lỗi lấy token", task.getException());
                    } else {
                        String token = task.getResult();
                        Log.d("FCM", "Token Firebase: " + token);
                    }
                });
    }

    private String doInBackground(String urlString) {
        try {
            JSONObject settings = new JSONObject();
            settings.put("theme_color", "#FFFFFF");
            settings.put("font_size", "16px");

            JSONObject description = new JSONObject();
            description.put("theme_color", "Màu nền của ứng dụng");
            description.put("font_size", "Cỡ chữ của ứng dụng");

            JSONObject data = new JSONObject();
            data.put("settings", settings);
            data.put("description", description);

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            os.write(data.toString().getBytes());
            os.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            int responseCode = conn.getResponseCode();
            Log.d("API_RESPONSE", "Mã trạng thái HTTP: " + responseCode);
            return responseCode == HttpURLConnection.HTTP_OK ? response.toString() : null;

        } catch (Exception e) {
            Log.e("API_ERROR", "Lỗi khi gọi API: " + e.getMessage());
            return null;
        }
    }

    private void updateUI(String result) {
        if (result == null) {
            Log.e("API_ERROR", "Không nhận được phản hồi từ API");
            return;
        }

        try {
            JSONObject jsonResponse = new JSONObject(result);
            if (!jsonResponse.getString("status").equals("success")) {
                Log.e("API_ERROR", "API trả về lỗi: " + jsonResponse.getString("message"));
                return;
            }

            JSONObject settings = jsonResponse.getJSONObject("settings");

            if (settings.has("theme_color")) {
                String themeColor = settings.getString("theme_color");
                mainLayout.setBackgroundColor(Color.parseColor(themeColor));
            }

            if (settings.has("font_size")) {
                String fontSize = settings.getString("font_size").replace("px", "");
                appTitle.setTextSize(Float.parseFloat(fontSize));
            }

        } catch (Exception e) {
            Log.e("API_ERROR", "Lỗi xử lý JSON: " + e.getMessage());
        }
    }
}
