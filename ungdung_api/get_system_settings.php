<?php
include 'db_connect.php';

header('Content-Type: application/json');
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type");

// Kiểm tra phương thức HTTP
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["status" => "error", "message" => "Invalid request method"], JSON_UNESCAPED_UNICODE);
    exit;
}

// Kiểm tra kết nối CSDL
if (!$conn) {
    echo json_encode(["status" => "error", "message" => "Database connection failed: " . mysqli_connect_error()], JSON_UNESCAPED_UNICODE);
    exit;
}

// Đọc dữ liệu JSON từ yêu cầu POST
$raw_data = file_get_contents("php://input");
error_log("📌 JSON nhận được: " . $raw_data);
$data = json_decode($raw_data, true);

// Kiểm tra nếu dữ liệu trống
if (!is_array($data)) {
    echo json_encode(["status" => "error", "message" => "Invalid JSON format"], JSON_UNESCAPED_UNICODE);
    exit;
}

// Lấy danh sách cài đặt từ CSDL
$sql = "SELECT setting_key, setting_value FROM system";
$result = mysqli_query($conn, $sql);

if (!$result) {
    echo json_encode(["status" => "error", "message" => "Query failed: " . mysqli_error($conn)], JSON_UNESCAPED_UNICODE);
    exit;
}

$settings = [];
while ($row = mysqli_fetch_assoc($result)) {
    $settings[$row['setting_key']] = $row['setting_value'];
}

// Trả về JSON
echo json_encode(["status" => "success", "settings" => $settings], JSON_UNESCAPED_UNICODE);

mysqli_close($conn);
?>
