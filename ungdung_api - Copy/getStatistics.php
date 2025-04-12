<?php
header("Content-Type: application/json; charset=UTF-8");

// Thông tin kết nối CSDL
$host = "localhost";  
$username = "root";  
$password = "";  
$database = "ung_dung_co_xuong_khop_cua_ban";  

// Tạo kết nối MySQLi
$conn = new mysqli($host, $username, $password, $database);

// Kiểm tra kết nối
if ($conn->connect_error) {
    http_response_code(500); // Trả về lỗi server nếu kết nối thất bại
    die(json_encode(["status" => "error", "message" => "Kết nối thất bại: " . $conn->connect_error]));
}

// Lấy số lượng người dùng
$sql_users = "SELECT COUNT(*) as total_users FROM nguoi_dung";
$result_users = $conn->query($sql_users);
$row_users = $result_users->fetch_assoc();

// Lấy số lượng bác sĩ
$sql_doctors = "SELECT COUNT(*) as total_doctors FROM bac_si";
$result_doctors = $conn->query($sql_doctors);
$row_doctors = $result_doctors->fetch_assoc();

// Lấy số lượng lịch khám
$sql_appointments = "SELECT COUNT(*) as total_appointments FROM lich_kham";
$result_appointments = $conn->query($sql_appointments);
$row_appointments = $result_appointments->fetch_assoc();

// Đóng kết nối CSDL
$conn->close();

// Trả về dữ liệu thống kê dưới dạng JSON
echo json_encode([
    'total_users' => $row_users['total_users'],
    'total_doctors' => $row_doctors['total_doctors'],
    'total_appointments' => $row_appointments['total_appointments']
]);
?>
