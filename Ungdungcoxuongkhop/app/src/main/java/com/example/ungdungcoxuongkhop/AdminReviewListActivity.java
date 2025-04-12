package com.example.ungdungcoxuongkhop;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminReviewListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminReviewAdapter adapter;
    private ArrayList<ReviewModel> list;
    private static final String URL = "http://172.22.144.1/ungdung_api/get_reviews.php";
    private EditText edtSearch;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_review_list);

        // Ánh xạ view
        recyclerView = findViewById(R.id.recyclerViewReview);
        edtSearch = findViewById(R.id.edtSearchReview);
        btnBack = findViewById(R.id.btnBack);

        // Khởi tạo list và adapter
        list = new ArrayList<>();
        adapter = new AdminReviewAdapter(list, this::deleteReview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Tải toàn bộ dữ liệu ban đầu
        loadData("");

        // Tìm kiếm khi nhấn phím Enter
        edtSearch.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {

                String keyword = edtSearch.getText().toString().trim();
                loadData(keyword);
                return true;
            }
            return false;
        });

        // Bắt sự kiện quay lại
        btnBack.setOnClickListener(v -> finish());
    }

    // Hàm tải dữ liệu đánh giá
    private void loadData(String keyword) {
        list.clear();

        StringRequest request = new StringRequest(Request.Method.POST, URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");

                        if (success) {
                            JSONArray jsonArray = jsonObject.getJSONArray("reviews");

                            if (jsonArray.length() == 0) {
                                Toast.makeText(this, "Không tìm thấy đánh giá phù hợp", Toast.LENGTH_SHORT).show();
                                adapter.notifyDataSetChanged();
                                return;
                            }

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);

                                int id = obj.getInt("id");
                                int idNguoiDung = obj.getInt("id_nguoi_dung");
                                String tenNguoiDung = obj.getString("ten_nguoi_dung");
                                int idBacSi = obj.getInt("id_bac_si");
                                String tenBacSi = obj.getString("ten_bac_si");
                                int diemSo = obj.getInt("diem_so");
                                String nhanXet = obj.getString("nhan_xet");
                                String ngayTao = obj.getString("ngay_tao");

                                list.add(new ReviewModel(id, idNguoiDung, tenNguoiDung,
                                        idBacSi, tenBacSi, diemSo, nhanXet, ngayTao));
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "Không có dữ liệu đánh giá", Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Lỗi phân tích dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Không thể kết nối đến máy chủ", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "get");
                params.put("keyword", keyword);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    // Hàm xoá đánh giá
    private void deleteReview(int id) {
        StringRequest request = new StringRequest(Request.Method.POST, URL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");

                        if (success) {
                            Toast.makeText(this, "Xoá đánh giá thành công", Toast.LENGTH_SHORT).show();
                            loadData(edtSearch.getText().toString().trim());
                        } else {
                            Toast.makeText(this, "Xoá thất bại: " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Lỗi phản hồi xoá: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Lỗi khi xoá: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "delete");
                params.put("id", String.valueOf(id));
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
