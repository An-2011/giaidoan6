<?php
include 'db_connect.php';
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT); // Hiển thị lỗi rõ ràng

try {
    if ($_SERVER['REQUEST_METHOD'] != 'POST') {
        throw new Exception("Yêu cầu không hợp lệ");
    }

    $json = file_get_contents("php://input");
    error_log("\n📩 JSON nhận được: " . $json);
    $data = json_decode($json, true);
    error_log("\n📩 Dữ liệu đã decode: " . print_r($data, true));

    if (!$data) {
        throw new Exception("Dữ liệu JSON không hợp lệ");
    }

    $required_fields = ["id_nguoi_dung", "id_bac_si", "ngay_gio_kham"];
    foreach ($required_fields as $field) {
        if (empty($data[$field])) {
            throw new Exception("Thiếu thông tin bắt buộc: " . $field);
        }
    }

    $user_id = intval($data['id_nguoi_dung']);
    $doctor_id = intval($data['id_bac_si']);
    $appointment_time = $data['ngay_gio_kham'];
    $status = "Đã đặt";
    $created_at = date("Y-m-d H:i:s");

    if (!preg_match('/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/', $appointment_time)) {
        throw new Exception("Định dạng ngày giờ không hợp lệ");
    }

    $stmt = $conn->prepare("SELECT id FROM bac_si WHERE id = ?");
    $stmt->bind_param("i", $doctor_id);
    $stmt->execute();
    $stmt->store_result();
    if ($stmt->num_rows == 0) {
        throw new Exception("Bác sĩ không tồn tại");
    }
    $stmt->close();

    // Kiểm tra người dùng tồn tại
    $stmt = $conn->prepare("SELECT id_nguoi_dung FROM nguoi_dung WHERE id_nguoi_dung = ?");
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    $stmt->store_result();
    if ($stmt->num_rows == 0) {
        throw new Exception("Người dùng không tồn tại");
    }
    $stmt->close();

    $stmt = $conn->prepare("SELECT id FROM lich_kham WHERE id_bac_si = ? AND ngay_gio_kham = ?");
    $stmt->bind_param("is", $doctor_id, $appointment_time);
    $stmt->execute();
    $stmt->store_result();
    if ($stmt->num_rows > 0) {
        throw new Exception("Bác sĩ đã có lịch vào thời gian này");
    }
    $stmt->close();

    $stmt = $conn->prepare("INSERT INTO lich_kham (id_nguoi_dung, id_bac_si, ngay_gio_kham, trang_thai, ngay_tao) VALUES (?, ?, ?, ?, ?)");
    $stmt->bind_param("iisss", $user_id, $doctor_id, $appointment_time, $status, $created_at);
    
    if ($stmt->execute()) {
        echo json_encode(["status" => "success", "message" => "Đặt lịch thành công"]);
    } else {
        throw new Exception("Lỗi đặt lịch: " . $stmt->error);
    }

    $stmt->close();
    $conn->close();

} catch (Exception $e) {
    error_log("Lỗi API đặt lịch: " . $e->getMessage());
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
}
?>
