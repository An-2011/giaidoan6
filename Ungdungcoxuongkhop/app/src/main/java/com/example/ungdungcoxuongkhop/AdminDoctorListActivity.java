package com.example.ungdungcoxuongkhop;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminDoctorListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminDoctorAdapter adapter;
    private List<AdminDoctor> fullDoctorList;
    private EditText edtSearchDoctor;
    private ProgressBar progressBar;
    private ImageView btnBack, btnAdd;

    private final String URL_DOCTOR_LIST = "http://172.22.144.1/ungdung_api/getData.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_doctor_list);

        recyclerView = findViewById(R.id.recyclerDoctorAdmin);
        edtSearchDoctor = findViewById(R.id.edtSearchDoctorAdmin);
        progressBar = findViewById(R.id.progressBarDoctorAdmin);
        btnBack = findViewById(R.id.btnBack);
        btnAdd = findViewById(R.id.btnAdd);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fullDoctorList = new ArrayList<>();

        adapter = new AdminDoctorAdapter(fullDoctorList, this, new AdminDoctorAdapter.OnDoctorActionListener() {
            @Override
            public void onEditDoctor(AdminDoctor doctor) {
                showEditDoctorDialog(doctor);
            }

            @Override
            public void onDeleteDoctor(AdminDoctor doctor) {
                showDeleteDoctorDialog(doctor);
            }
        });

        recyclerView.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        edtSearchDoctor.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDoctorList(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnAdd.setOnClickListener(v -> showAddDoctorDialog());

        loadDoctors();
    }

    private void loadDoctors() {
        progressBar.setVisibility(View.VISIBLE);

        JSONObject params = new JSONObject();
        try {
            params.put("key", "get_all");
            params.put("search", "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                URL_DOCTOR_LIST,
                params,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        if (response.has("success") && response.getBoolean("success") && response.has("doctors")) {
                            JSONArray dataArray = response.getJSONArray("doctors");

                            fullDoctorList.clear();
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);
                                AdminDoctor doctor = new AdminDoctor(
                                        obj.getInt("id"),
                                        obj.getString("ho_ten"),
                                        obj.getString("chuyen_khoa"),
                                        obj.getInt("kinh_nghiem"),
                                        obj.getString("dia_chi_phong_kham"),
                                        obj.getString("so_dien_thoai"),
                                        obj.getString("ngay_tao")
                                );
                                fullDoctorList.add(doctor);
                            }

                            adapter.setDoctorList(fullDoctorList);
                        } else {
                            Toast.makeText(this, "Dữ liệu trả về không đúng định dạng", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    error.printStackTrace();
                    Toast.makeText(this, "Không thể kết nối đến máy chủ", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void filterDoctorList(String keyword) {
        List<AdminDoctor> filtered = new ArrayList<>();
        for (AdminDoctor doctor : fullDoctorList) {
            if (doctor.getHoTen().toLowerCase().contains(keyword.toLowerCase()) ||
                    doctor.getChuyenKhoa().toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(doctor);
            }
        }
        adapter.setDoctorList(filtered);
    }

    private void showAddDoctorDialog() {
        showDoctorDialog(null);
    }

    private void showEditDoctorDialog(AdminDoctor doctor) {
        showDoctorDialog(doctor);
    }

    private void showDoctorDialog(AdminDoctor doctor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(doctor == null ? "Thêm bác sĩ mới" : "Chỉnh sửa bác sĩ");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_doctor, null);
        builder.setView(view);

        EditText edtName = view.findViewById(R.id.edtDoctorName);
        EditText edtSpecialty = view.findViewById(R.id.edtDoctorSpecialty);
        EditText edtExperience = view.findViewById(R.id.edtDoctorExperience);
        EditText edtAddress = view.findViewById(R.id.edtDoctorAddress);
        EditText edtPhone = view.findViewById(R.id.edtDoctorPhone);

        if (doctor != null) {
            edtName.setText(doctor.getHoTen());
            edtSpecialty.setText(doctor.getChuyenKhoa());
            edtExperience.setText(String.valueOf(doctor.getKinhNghiem()));
            edtAddress.setText(doctor.getDiaChi());
            edtPhone.setText(doctor.getSoDienThoai());
        }

        builder.setPositiveButton(doctor == null ? "Thêm" : "Lưu", (dialog, which) -> {
            String name = edtName.getText().toString().trim();
            String specialty = edtSpecialty.getText().toString().trim();
            String experienceStr = edtExperience.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();

            if (name.isEmpty() || specialty.isEmpty() || experienceStr.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            int experience = Integer.parseInt(experienceStr);

            if (doctor == null) {
                addDoctor(name, specialty, experience, address, phone);
            } else {
                updateDoctor(doctor.getId(), name, specialty, experience, address, phone);
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showDeleteDoctorDialog(AdminDoctor doctor) {
        new AlertDialog.Builder(this)
                .setTitle("Xoá bác sĩ")
                .setMessage("Bạn có chắc chắn muốn xoá bác sĩ này không?")
                .setPositiveButton("Xoá", (dialog, which) -> deleteDoctor(doctor.getId()))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void addDoctor(String name, String specialty, int experience, String address, String phone) {
        progressBar.setVisibility(View.VISIBLE);

        JSONObject params = new JSONObject();
        try {
            params.put("key", "add_doctor");
            params.put("ho_ten", name);
            params.put("chuyen_khoa", specialty);
            params.put("kinh_nghiem", experience);
            params.put("dia_chi_phong_kham", address);
            params.put("so_dien_thoai", phone);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sendDoctorRequest(params, "Thêm bác sĩ thành công", "Thêm bác sĩ thất bại");
    }

    private void updateDoctor(int id, String name, String specialty, int experience, String address, String phone) {
        progressBar.setVisibility(View.VISIBLE);

        JSONObject params = new JSONObject();
        try {
            params.put("key", "update_doctor");
            params.put("id", id);
            params.put("ho_ten", name);
            params.put("chuyen_khoa", specialty);
            params.put("kinh_nghiem", experience);
            params.put("dia_chi_phong_kham", address);
            params.put("so_dien_thoai", phone);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sendDoctorRequest(params, "Cập nhật bác sĩ thành công", "Cập nhật bác sĩ thất bại");
    }

    private void deleteDoctor(int id) {
        progressBar.setVisibility(View.VISIBLE);

        JSONObject params = new JSONObject();
        try {
            params.put("key", "delete_doctor");
            params.put("id", id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sendDoctorRequest(params, "Xoá bác sĩ thành công", "Xoá bác sĩ thất bại");
    }

    private void sendDoctorRequest(JSONObject params, String successMsg, String errorMsg) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                URL_DOCTOR_LIST,
                params,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        if (response.has("success") && response.getBoolean("success")) {
                            Toast.makeText(this, successMsg, Toast.LENGTH_SHORT).show();
                            loadDoctors();
                        } else {
                            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi xử lý phản hồi", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi kết nối đến máy chủ", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }
}