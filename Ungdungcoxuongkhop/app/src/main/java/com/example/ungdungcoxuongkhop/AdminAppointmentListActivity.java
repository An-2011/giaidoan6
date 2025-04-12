package com.example.ungdungcoxuongkhop;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import android.util.Log;

public class AdminAppointmentListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminAppointmentAdapter adapter;
    private List<AdminAppointment> fullAppointmentList;
    private EditText edtSearch;
    private ImageView btnBack, btnAddAppointment;
    private ProgressBar progressBar;

    private final String URL_API = "http://172.22.144.1/ungdung_api/get_all_appointments.php";

    // Doctor Spinner Data
    private List<AdminDoctor> danhSachBacSi = new ArrayList<>();
    private ArrayAdapter<String> adapterSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_appointment_list);

        recyclerView = findViewById(R.id.recyclerAppointmentAdmin);
        edtSearch = findViewById(R.id.edtSearchAppointment);
        btnBack = findViewById(R.id.btnBackAppointment);
        btnAddAppointment = findViewById(R.id.btnAddAppointment);
        progressBar = findViewById(R.id.progressBarAppointment);

        fullAppointmentList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdminAppointmentAdapter(fullAppointmentList, this, new AdminAppointmentAdapter.OnAppointmentActionListener() {
            @Override
            public void onDelete(AdminAppointment appointment) {
                confirmDelete(appointment);
            }

            @Override
            public void onUpdateStatus(AdminAppointment appointment) {
                showUpdateDialog(appointment);
            }
        });

        recyclerView.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        btnAddAppointment.setOnClickListener(v -> showAddAppointmentDialog());

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterAppointments(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        loadAppointments();
    }

    private void loadAppointments() {
        progressBar.setVisibility(View.VISIBLE);

        JSONObject params = new JSONObject();
        try {
            params.put("key", "get_all_appointments");
            params.put("search", "");
        } catch (Exception e) {
            Log.e("AddAppointment", "Lỗi tạo JSON params", e);
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                URL_API,
                params,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        if (response.has("success") && response.getBoolean("success") && response.has("appointments")) {
                            JSONArray dataArray = response.getJSONArray("appointments");

                            fullAppointmentList.clear();
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);
                                AdminAppointment appointment = new AdminAppointment(
                                        obj.getInt("id"),
                                        obj.getInt("id_nguoi_dung"),
                                        obj.getInt("id_bac_si"),
                                        obj.getString("ngay_gio_kham"),
                                        obj.getString("trang_thai"),
                                        obj.getString("ngay_tao")
                                );
                                fullAppointmentList.add(appointment);
                            }

                            adapter.setAppointmentList(fullAppointmentList);
                        } else {
                            Toast.makeText(this, "Dữ liệu trả về không hợp lệ", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void filterAppointments(String keyword) {
        List<AdminAppointment> filtered = new ArrayList<>();
        for (AdminAppointment a : fullAppointmentList) {
            if (String.valueOf(a.getIdNguoiDung()).contains(keyword) ||
                    String.valueOf(a.getIdBacSi()).contains(keyword) ||
                    a.getTrangThai().toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(a);
            }
        }
        adapter.setAppointmentList(filtered);
    }

    private void confirmDelete(AdminAppointment appointment) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xoá")
                .setMessage("Bạn có chắc muốn xoá lịch khám ID: " + appointment.getId() + "?")
                .setPositiveButton("Xoá", (dialog, which) -> deleteAppointment(appointment.getId()))
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void deleteAppointment(int id) {
        progressBar.setVisibility(View.VISIBLE);

        JSONObject params = new JSONObject();
        try {
            params.put("key", "delete_appointment");
            params.put("id", id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                URL_API,
                params,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        if (response.has("success") && response.getBoolean("success")) {
                            Toast.makeText(this, "Xoá thành công", Toast.LENGTH_SHORT).show();
                            loadAppointments();
                        } else {
                            Toast.makeText(this, "Xoá thất bại", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Lỗi khi xoá", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void showUpdateDialog(AdminAppointment appointment) {
        final EditText edt = new EditText(this);
        edt.setHint("Nhập trạng thái mới (ví dụ: đã khám)");

        new AlertDialog.Builder(this)
                .setTitle("Cập nhật trạng thái")
                .setView(edt)
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String newStatus = edt.getText().toString().trim();
                    if (!newStatus.isEmpty()) {
                        updateStatus(appointment.getId(), newStatus);
                    } else {
                        Toast.makeText(this, "Trạng thái không được để trống", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void updateStatus(int id, String status) {
        progressBar.setVisibility(View.VISIBLE);

        JSONObject params = new JSONObject();
        try {
            params.put("key", "update_appointment_status");
            params.put("id", id);
            params.put("trang_thai", status);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                URL_API,
                params,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        if (response.has("success") && response.getBoolean("success")) {
                            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            loadAppointments();
                        } else {
                            Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void showAddAppointmentDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_appointment, null);

        EditText edtUserId = view.findViewById(R.id.edtUserId);
        Spinner spinnerDoctor = view.findViewById(R.id.spinnerDoctor);
        EditText edtDateTime = view.findViewById(R.id.edtDateTime);
        EditText edtStatus = view.findViewById(R.id.edtStatus);

        edtDateTime.setFocusable(false);
        edtDateTime.setClickable(true);

        edtDateTime.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();

            new DatePickerDialog(this, (datePicker, year, month, day) -> {
                int finalMonth = month + 1;
                String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, finalMonth, day);

                new TimePickerDialog(this, (timePicker, hour, minute) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d:00", hour, minute);
                    edtDateTime.setText(date + " " + time);
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();

            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Spinner initialization
        adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDoctor.setAdapter(adapterSpinner);

        // Load doctors for spinner
        String doctorUrl = "http://172.22.144.1/ungdung_api/getData.php";
        JSONObject paramGet = new JSONObject();
        try {
            paramGet.put("key", "get_all");
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest doctorRequest = new JsonObjectRequest(
                Request.Method.POST,
                doctorUrl,
                paramGet,
                response -> {
                    try {
                        JSONArray doctors = response.getJSONArray("doctors");
                        for (int i = 0; i < doctors.length(); i++) {
                            JSONObject obj = doctors.getJSONObject(i);
                            int id = obj.getInt("id");
                            String ten = obj.getString("ho_ten");
                            // Add doctor to the list
                            danhSachBacSi.add(new AdminDoctor(id, ten, obj.getString("chuyen_khoa"),
                                    obj.getInt("kinh_nghiem"), obj.getString("dia_chi_phong_kham"),
                                    obj.getString("so_dien_thoai"), obj.getString("ngay_tao")));
                            // Update Spinner
                            adapterSpinner.add(ten + " (ID: " + id + ")");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("GetDoctors", "Lỗi kết nối", error);
                });

        Volley.newRequestQueue(this).add(doctorRequest);

        new AlertDialog.Builder(this)
                .setTitle("Thêm lịch khám")
                .setView(view)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String userId = edtUserId.getText().toString().trim();
                    String dateTime = edtDateTime.getText().toString().trim();
                    String status = edtStatus.getText().toString().trim();
                    AdminDoctor selectedDoctor = danhSachBacSi.get(spinnerDoctor.getSelectedItemPosition());

                    if (!userId.isEmpty() && !dateTime.isEmpty() && !status.isEmpty()) {
                        addAppointment(userId, selectedDoctor.getId(), dateTime, status);
                    } else {
                        Toast.makeText(AdminAppointmentListActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }


    private void addAppointment(String userId, int doctorId, String dateTime, String status) {
        progressBar.setVisibility(View.VISIBLE);

        JSONObject params = new JSONObject();
        try {
            params.put("key", "add_appointment");
            params.put("id_nguoi_dung", userId);
            params.put("id_bac_si", doctorId);
            params.put("ngay_gio_kham", dateTime);
            params.put("trang_thai", status);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                URL_API,
                params,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        if (response.has("success") && response.getBoolean("success")) {
                            Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                            loadAppointments();
                        } else {
                            Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Lỗi thêm lịch khám", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }
}
