<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

header('Content-Type: application/json; charset=UTF-8');

$host = "localhost";  
$username = "root";  
$password = "";  
$database = "ung_dung_co_xuong_khop_cua_ban";  

// Tạo kết nối MySQLi
$conn = new mysqli($host, $username, $password, $database);

// Kiểm tra kết nối
if ($conn->connect_error) {
    http_response_code(500); // Trả về lỗi server
    die(json_encode(["status" => "error", "message" => "Kết nối thất bại: " . $conn->connect_error]));
}

// Không in JSON nếu kết nối thành công, để tránh lỗi dư JSON
?>
