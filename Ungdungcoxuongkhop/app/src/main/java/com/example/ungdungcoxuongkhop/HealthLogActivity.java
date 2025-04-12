package com.example.ungdungcoxuongkhop;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import android.util.Log;

public class HealthLogActivity extends AppCompatActivity {

    private EditText edtUserId, edtDate, edtPainLevel, edtUricAcid, edtNotes;
    private Button btnSubmit, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_log2);

        edtUserId = findViewById(R.id.edtUserId);  // Trường nhập ID người dùng
        edtDate = findViewById(R.id.edtDate);
        edtPainLevel = findViewById(R.id.edtPainLevel);
        edtUricAcid = findViewById(R.id.edtUricAcid);
        edtNotes = findViewById(R.id.edtNotes);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnBack = findViewById(R.id.btnBack);

        // Cài đặt sự kiện cho nút
        btnSubmit.setOnClickListener(v -> submitHealthLog());
        btnBack.setOnClickListener(v -> finish());
    }

    private void submitHealthLog() {
        String userIdStr = edtUserId.getText().toString().trim();  // Lấy ID người dùng từ EditText
        String date = edtDate.getText().toString().trim();
        String painLevelStr = edtPainLevel.getText().toString().trim();
        String uricAcidStr = edtUricAcid.getText().toString().trim();
        String notes = edtNotes.getText().toString().trim();

        // Kiểm tra ID người dùng có trống không
        if (userIdStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập ID người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra các trường thông tin có trống không
        if (date.isEmpty() || painLevelStr.isEmpty() || uricAcidStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Chuyển đổi dữ liệu từ String sang float
        float painLevel;
        float uricAcid;
        int userId;
        try {
            painLevel = Float.parseFloat(painLevelStr);
            uricAcid = Float.parseFloat(uricAcidStr);
            userId = Integer.parseInt(userIdStr);  // Chuyển ID người dùng thành số
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập đúng các giá trị số", Toast.LENGTH_SHORT).show();
            return;
        }

        // Địa chỉ API
        String url = "http://172.22.144.1/ungdung_api/capnhatsuckhoe.php";
        JSONObject postData = new JSONObject();
        try {
            postData.put("id_nguoi_dung", userId);  // Sử dụng ID người dùng nhập vào
            postData.put("ngay", date);
            postData.put("muc_do_dau", painLevel);
            postData.put("chi_so_axit_uric", uricAcid);
            postData.put("ghi_chu", notes);
        } catch (JSONException e) {
            Log.e("JSON_Error", "Lỗi tạo JSON: " + e.getMessage());
            Toast.makeText(this, "Lỗi tạo JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gửi yêu cầu API
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postData,
                response -> {
                    try {
                        String message = response.getString("message");
                        runOnUiThread(() -> Toast.makeText(HealthLogActivity.this, message, Toast.LENGTH_SHORT).show());
                    } catch (JSONException e) {
                        Log.e("API_Error", "Lỗi phản hồi JSON: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e("API_Error", "Lỗi kết nối server: " + error.toString());
                    Toast.makeText(this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }
        };

        // Thực hiện yêu cầu API
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}
