package com.example.ungdungcoxuongkhop;

public class FunctionItem {
    private String title;           // Tiêu đề chức năng
    private int iconResId;          // ID của biểu tượng
    private Class<?> activityClass; // Activity tương ứng với chức năng
    private String description;     // Mô tả ngắn về chức năng (tùy chọn)

    // Constructor để khởi tạo FunctionItem
    public FunctionItem(String title, int iconResId, Class<?> activityClass) {
        this.title = title;
        this.iconResId = iconResId;
        this.activityClass = activityClass;
        this.description = ""; // Mặc định là không có mô tả
    }

    // Constructor để khởi tạo FunctionItem với mô tả
    public FunctionItem(String title, int iconResId, Class<?> activityClass, String description) {
        this.title = title;
        this.iconResId = iconResId;
        this.activityClass = activityClass;
        this.description = description;
    }

    // Getter và Setter cho các trường
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public Class<?> getActivityClass() {
        return activityClass;
    }

    public void setActivityClass(Class<?> activityClass) {
        this.activityClass = activityClass;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
