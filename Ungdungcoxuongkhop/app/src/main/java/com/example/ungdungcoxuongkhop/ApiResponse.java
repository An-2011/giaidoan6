package com.example.ungdungcoxuongkhop;

import java.util.List;

public class ApiResponse {
    private boolean success;
    private List<User> users;  // Danh sách người dùng trả về từ API

    // Getter cho users
    public List<User> getUsers() {
        return users;
    }

    // Getter cho success (nếu cần)
    public boolean isSuccess() {
        return success;
    }
}
