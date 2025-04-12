<?php
ob_clean();
error_reporting(E_ALL);
ini_set('display_errors', 0);
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Content-Type");

$conn = new mysqli('localhost', 'root', '', 'ung_dung_co_xuong_khop_cua_ban');
if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "Kết nối thất bại: " . $conn->connect_error]);
    exit();
}

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["status" => "error", "message" => "Phương thức không hợp lệ. Chỉ chấp nhận POST."]);
    exit();
}

$data = json_decode(file_get_contents('php://input'), true);

if (json_last_error() !== JSON_ERROR_NONE) {
    echo json_encode(["status" => "error", "message" => "Lỗi JSON: " . json_last_error_msg()]);
    exit();
}

// Kiểm tra dữ liệu đầu vào
if (
    !isset($data['id_nguoi_gui']) || !is_numeric($data['id_nguoi_gui']) ||
    !isset($data['id_nguoi_nhan']) || !is_numeric($data['id_nguoi_nhan']) ||
    !isset($data['tin_nhan']) || trim($data['tin_nhan']) === ''
) {
    echo json_encode(["status" => "error", "message" => "Thiếu dữ liệu đầu vào hợp lệ."]);
    exit();
}

$id_nguoi_gui = (int)$data['id_nguoi_gui'];
$id_nguoi_nhan = (int)$data['id_nguoi_nhan'];
$tin_nhan = trim($conn->real_escape_string($data['tin_nhan']));
$hinh_anh = isset($data['hinh_anh']) ? trim($conn->real_escape_string($data['hinh_anh'])) : null;

$sql = "INSERT INTO tin_nhan_tu_van (id_nguoi_gui, id_nguoi_nhan, tin_nhan, hinh_anh, thoi_gian_gui) VALUES (?, ?, ?, ?, NOW())";
$stmt = $conn->prepare($sql);

if (!$stmt) {
    echo json_encode(["status" => "error", "message" => "Lỗi prepare: " . $conn->error]);
    exit();
}

$stmt->bind_param("iiss", $id_nguoi_gui, $id_nguoi_nhan, $tin_nhan, $hinh_anh);

if ($stmt->execute()) {
    echo json_encode(["status" => "success", "message" => "Tin nhắn đã được gửi thành công"]);
} else {
    echo json_encode(["status" => "error", "message" => "Lỗi khi gửi tin nhắn: " . $stmt->error]);
}

$stmt->close();
$conn->close();
