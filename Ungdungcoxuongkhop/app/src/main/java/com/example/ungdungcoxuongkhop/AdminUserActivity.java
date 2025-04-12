package com.example.ungdungcoxuongkhop;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import org.json.*;

import java.util.*;

public class AdminUserActivity extends AppCompatActivity {

    EditText edtSearch;
    ImageButton btnAdd; // Thêm người dùng
    ImageButton btnBack; // Nút quay lại
    ImageButton btnSearch; // Nút tìm kiếm
    RecyclerView recyclerView;
    UserAdapter adapter;
    List<User> userList = new ArrayList<>();

    String URL = "http://172.22.144.1/ungdung_api/get_all_users.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user);

        edtSearch = findViewById(R.id.edtSearch); // Lấy EditText tìm kiếm
        btnAdd = findViewById(R.id.btnAdd); // Thêm người dùng
        btnBack = findViewById(R.id.btnBack); // Nút quay lại
        btnSearch = findViewById(R.id.btnSearch); // Nút tìm kiếm
        recyclerView = findViewById(R.id.recyclerView); // RecyclerView hiển thị danh sách người dùng

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Truyền callback xử lý sửa và xóa vào adapter
        adapter = new UserAdapter(userList, new UserAdapter.OnUserActionListener() {
            @Override
            public void onEdit(User user) {
                showEditDialog(user); // Khi nhấn Sửa
            }

            @Override
            public void onDelete(User user) {
                deleteUser(user); // Khi nhấn Xóa
            }
        });

        recyclerView.setAdapter(adapter);

        // Nút Thêm
        btnAdd.setOnClickListener(v -> showEditDialog(null));

        // Nút Quay lại
        btnBack.setOnClickListener(v -> onBackPressed());

        // Tìm kiếm khi nhấn Enter
        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                String keyword = edtSearch.getText().toString().trim();
                searchUser(keyword);
                return true;
            }
            return false;
        });

        // Xử lý khi nhấn vào nút tìm kiếm
        btnSearch.setOnClickListener(v -> {
            String keyword = edtSearch.getText().toString().trim();
            searchUser(keyword);  // Gọi phương thức tìm kiếm với từ khóa
        });

        // Lấy tất cả người dùng
        getAllUsers();
    }

    private void getAllUsers() {
        sendRequest("get_all", null); // Gọi API lấy tất cả người dùng
    }

    private void searchUser(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            getAllUsers(); // Nếu không có từ khóa tìm kiếm, lấy tất cả người dùng
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("key", "search_user");
        params.put("keyword", keyword);
        sendRequest("search_user", params); // Gọi API tìm kiếm người dùng theo từ khóa
    }

    private void showEditDialog(User user) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_user, null);
        EditText edtName = view.findViewById(R.id.edtName);
        EditText edtEmail = view.findViewById(R.id.edtEmail);
        EditText edtPhone = view.findViewById(R.id.edtPhone);
        EditText edtDob = view.findViewById(R.id.edtDob);
        EditText edtGender = view.findViewById(R.id.edtGender);

        if (user != null) {
            edtName.setText(user.getHo_ten());
            edtEmail.setText(user.getEmail());
            edtPhone.setText(user.getSo_dien_thoai());
            edtDob.setText(user.getNgay_sinh());
            edtGender.setText(user.getGioi_tinh());
        }

        new AlertDialog.Builder(this)
                .setTitle(user == null ? "Thêm người dùng" : "Sửa người dùng")
                .setView(view)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    Map<String, String> params = new HashMap<>();
                    String name = edtName.getText().toString().trim();
                    String email = edtEmail.getText().toString().trim();
                    String phone = edtPhone.getText().toString().trim();
                    String dob = edtDob.getText().toString().trim();
                    String gender = edtGender.getText().toString().trim();

                    // Kiểm tra dữ liệu nhập
                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) ||
                            TextUtils.isEmpty(dob) || TextUtils.isEmpty(gender)) {
                        Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    params.put("ho_ten", name);
                    params.put("email", email);
                    params.put("so_dien_thoai", phone);
                    params.put("ngay_sinh", dob);
                    params.put("gioi_tinh", gender);

                    if (user == null) {
                        sendRequest("add_user", params); // Thêm người dùng mới
                    } else {
                        params.put("id_nguoi_dung", String.valueOf(user.getId_nguoi_dung()));
                        sendRequest("update_user", params); // Cập nhật thông tin người dùng
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteUser(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa người dùng")
                .setMessage("Bạn có chắc chắn muốn xóa người dùng này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_nguoi_dung", String.valueOf(user.getId_nguoi_dung()));
                    sendRequest("delete_user", params); // Gọi API xóa người dùng
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void sendRequest(final String key, final Map<String, String> params) {
        final Map<String, String> finalParams = new HashMap<>();
        if (params != null) {
            finalParams.putAll(params);
        }
        finalParams.put("key", key);  // Đảm bảo rằng luôn có key trong params

        // Tạo đối tượng requestData (dữ liệu JSON cần gửi)
        JSONObject requestData = new JSONObject();
        try {
            for (Map.Entry<String, String> entry : finalParams.entrySet()) {
                requestData.put(entry.getKey(), entry.getValue()); // Thêm dữ liệu vào JSON
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Tạo đối tượng JsonObjectRequest
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Xử lý phản hồi từ server
                        try {
                            if (response.getBoolean("success")) {
                                if (key.equals("get_all") || key.equals("search_user")) {
                                    userList.clear();
                                    JSONArray arr = response.getJSONArray("users");
                                    for (int i = 0; i < arr.length(); i++) {
                                        JSONObject u = arr.getJSONObject(i);
                                        userList.add(new User(
                                                u.getInt("id_nguoi_dung"),
                                                u.getString("ho_ten"),
                                                u.getString("email"),
                                                u.getString("so_dien_thoai"),
                                                u.getString("ngay_sinh"),
                                                u.getString("gioi_tinh"),
                                                u.getString("ngay_tao")
                                        ));
                                    }
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(AdminUserActivity.this, "Thao tác thành công", Toast.LENGTH_SHORT).show();
                                    getAllUsers();
                                }
                            } else {
                                Toast.makeText(AdminUserActivity.this, "Thất bại: " + response.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(AdminUserActivity.this, "Lỗi JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi
                        Toast.makeText(AdminUserActivity.this, "Lỗi mạng: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Thêm yêu cầu vào RequestQueue để thực thi
        Volley.newRequestQueue(this).add(request);
    }
}
