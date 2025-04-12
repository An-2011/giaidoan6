package com.example.ungdungcoxuongkhop;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdminNotificationListActivity extends AppCompatActivity {

    private EditText edtSearch;
    private RecyclerView recyclerView;
    private AdminNotificationAdapter adapter;
    private ArrayList<NotificationModel> list = new ArrayList<>();

    private static final String URL = "http://172.22.144.1/ungdung_api/get_all_notifications.php";
    private static final int LIMIT = 20;
    private int offset = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String currentKeyword = "";

    private ImageView btnBack; // ✅ Thêm nút quay lại

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_notification_list);

        edtSearch = findViewById(R.id.edtSearch);
        recyclerView = findViewById(R.id.recyclerView);
        btnBack = findViewById(R.id.btnBack); // ✅ Gán nút quay lại

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminNotificationAdapter(this, list);
        recyclerView.setAdapter(adapter);

        loadNotifications("", false); // Tải dữ liệu lần đầu

        // ✅ Xử lý sự kiện nút quay lại
        btnBack.setOnClickListener(v -> finish());

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentKeyword = s.toString().trim();
                loadNotifications(currentKeyword, false);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);
                LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();
                if (!isLoading && !isLastPage && lm != null && lm.findLastVisibleItemPosition() >= list.size() - 1) {
                    loadNotifications(currentKeyword, true);
                }
            }
        });
    }

    private void loadNotifications(String keyword, boolean isLoadMore) {
        if (isLoading) return;
        isLoading = true;

        if (!isLoadMore) {
            offset = 0;
            isLastPage = false;
            list.clear();
            adapter.notifyDataSetChanged();
        }

        JSONObject params = new JSONObject();
        try {
            params.put("action", keyword.isEmpty() ? "all" : "list");
            if (!keyword.isEmpty()) {
                params.put("search", keyword);
            }
            params.put("limit", LIMIT);
            params.put("offset", offset);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi tạo dữ liệu gửi!", Toast.LENGTH_SHORT).show();
            isLoading = false;
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                params,
                response -> {
                    Log.d("SERVER_RESPONSE", response.toString());

                    try {
                        if (response.getString("status").equals("success")) {
                            JSONArray dataArray = response.getJSONArray("data");

                            if (dataArray.length() < LIMIT) {
                                isLastPage = true;
                            }

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);

                                NotificationModel noti = new NotificationModel(
                                        obj.getInt("id"),
                                        obj.getInt("id_nguoi_dung"),
                                        obj.getString("tieu_de"),
                                        obj.getString("noi_dung"),
                                        obj.getString("ngay_tao"),
                                        obj.getString("loai"),
                                        obj.optString("nguoi_nhan", "Không rõ"),
                                        obj.optString("trang_thai", "Chưa rõ")
                                );

                                list.add(noti);
                            }

                            adapter.notifyDataSetChanged();
                            offset += LIMIT;
                        } else {
                            isLastPage = true;
                            Toast.makeText(this, "Không có dữ liệu!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi phân tích dữ liệu!", Toast.LENGTH_SHORT).show();
                    }
                    isLoading = false;
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Lỗi kết nối máy chủ!", Toast.LENGTH_SHORT).show();
                    isLoading = false;
                }
        );

        Volley.newRequestQueue(this).add(request);
    }
}
