package com.example.ungdungcoxuongkhop;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import android.app.TimePickerDialog;

public class BookAppointmentActivity extends AppCompatActivity {

    private EditText edtUserId, edtDate;
    private Spinner spnDoctor;
    private ProgressBar progressBar;
    private Button btnBook;
    private TextView tvBack;
    private RequestQueue requestQueue;
    private String selectedDoctorId = "-1"; // Mặc định không có bác sĩ nào

    private static final String API_GET_DOCTORS = "http://172.22.144.1/ungdung_api/getData.php";
    private static final String API_BOOK_APPOINTMENT = "http://172.22.144.1/ungdung_api/book_appointment.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        edtUserId = findViewById(R.id.edtUserId);
        edtDate = findViewById(R.id.edtDate);
        spnDoctor = findViewById(R.id.spnDoctor);
        progressBar = findViewById(R.id.progressBar);
        btnBook = findViewById(R.id.btnBook);
        tvBack = findViewById(R.id.btnBack);
        requestQueue = Volley.newRequestQueue(this);

        // ✅ Xử lý sự kiện nhấn vào nút Quay lại
        tvBack.setOnClickListener(v -> onBackPressed());
        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());

        // ✅ Load doctorId từ SharedPreferences nếu có
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        selectedDoctorId = String.valueOf(prefs.getInt("id_bac_si", -1));
        Log.d("DEBUG", "📢 doctorId đã lưu trong SharedPreferences: " + selectedDoctorId);

        loadDoctors();
        edtDate.setOnClickListener(v -> showDatePicker());
        btnBook.setOnClickListener(v -> bookAppointment());
    }

    private void loadDoctors() {
        progressBar.setVisibility(View.VISIBLE);
        Log.d("API", "Đang tải danh sách bác sĩ từ API: " + API_GET_DOCTORS);

        // Tạo request JSON để gửi key "get_all"
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("key", "get_all");
            // Optional: Có thể thêm search parameter nếu cần
            requestData.put("search", ""); // Search trống nếu không cần tìm kiếm
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API_GET_DOCTORS, requestData,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    Log.d("API_RESPONSE", "Phản hồi từ API: " + response.toString());

                    try {
                        if (!response.getBoolean("success")) {
                            Toast.makeText(this, "Lỗi tải danh sách bác sĩ!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray jsonArray = response.getJSONArray("doctors");
                        String[] doctorNames = new String[jsonArray.length()];
                        final String[] doctorIds = new String[jsonArray.length()];

                        // Duyệt qua mảng dữ liệu bác sĩ trả về và lưu vào doctorNames và doctorIds
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            doctorNames[i] = jsonObject.getString("ho_ten");
                            doctorIds[i] = jsonObject.getString("id");
                        }

                        // Gắn adapter cho Spinner
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_spinner_item, doctorNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnDoctor.setAdapter(adapter);

                        // ✅ Chọn lại bác sĩ đã lưu nếu có
                        for (int i = 0; i < doctorIds.length; i++) {
                            if (doctorIds[i].equals(selectedDoctorId)) {
                                spnDoctor.setSelection(i);
                                break;
                            }
                        }

                        // ✅ Lưu doctorId khi người dùng chọn bác sĩ
                        spnDoctor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                selectedDoctorId = doctorIds[position];

                                // ✅ Lưu doctorId vào SharedPreferences
                                SharedPreferences preferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putInt("id_bac_si", Integer.parseInt(selectedDoctorId));
                                editor.apply();

                                Log.d("DEBUG", "✅ doctorId đã lưu vào SharedPreferences: " + selectedDoctorId);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                    } catch (JSONException e) {
                        Log.e("JSON_ERROR", "Lỗi xử lý JSON: " + e.getMessage());
                        Toast.makeText(this, "Lỗi xử lý danh sách bác sĩ!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e("API_ERROR", "Lỗi kết nối API: " + error.getMessage());
                    Toast.makeText(this, "Lỗi tải danh sách bác sĩ!", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }


    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Hiển thị DatePickerDialog cho người dùng chọn ngày
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            // Sau khi chọn ngày, hiển thị TimePickerDialog để chọn giờ và phút
            showTimePicker(year1, month1, dayOfMonth);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePicker(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Hiển thị TimePickerDialog cho người dùng chọn giờ và phút
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minuteOfHour) -> {
            // Định dạng lại ngày và giờ theo định dạng yyyy-MM-dd HH:mm:ss
            String date = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", day);
            String time = String.format("%02d:%02d:00", hourOfDay, minuteOfHour); // Mặc định giây là 00
            String fullDateTime = date + " " + time;

            // Đặt giá trị cho EditText
            edtDate.setText(fullDateTime);
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void bookAppointment() {
        String userId = edtUserId.getText().toString().trim();
        String date = edtDate.getText().toString().trim();

        if (userId.isEmpty() || date.isEmpty() || selectedDoctorId.equals("-1")) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra định dạng ngày giờ
        if (!date.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
            Toast.makeText(this, "Định dạng ngày giờ không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        JSONObject requestData = new JSONObject();
        try {
            // Đảm bảo rằng các tham số gửi lên khớp với API
            requestData.put("id_nguoi_dung", userId);
            requestData.put("id_bac_si", selectedDoctorId);
            requestData.put("ngay_gio_kham", date); // Format date: yyyy-MM-dd HH:mm:ss
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API_BOOK_APPOINTMENT, requestData,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        // Kiểm tra phản hồi từ API
                        String status = response.getString("status");
                        String message = response.getString("message");

                        if (status.equals("success")) {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            finish(); // Đóng activity sau khi đặt lịch thành công
                        } else {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi xử lý dữ liệu!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }
}