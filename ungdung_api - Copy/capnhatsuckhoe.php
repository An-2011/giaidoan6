<?php
include 'db_connect.php';
header('Content-Type: application/json; charset=UTF-8');

if (!$conn) {
    echo json_encode(["status" => "error", "message" => "Lỗi kết nối cơ sở dữ liệu."]);
    exit;
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $json = file_get_contents("php://input");
    $data = json_decode($json, true);

    if ($data === null) {
        echo json_encode(["status" => "error", "message" => "Dữ liệu JSON không hợp lệ."]);
        exit;
    }

    if (!isset($data['id_nguoi_dung'], $data['ngay'], $data['muc_do_dau'], $data['chi_so_axit_uric'])) {
        echo json_encode(["status" => "error", "message" => "Thiếu thông tin bắt buộc."]);
        exit;
    }

    $id_nguoi_dung = intval($data['id_nguoi_dung']);
    $date = $data['ngay'];
    $pain_level = floatval($data['muc_do_dau']);
    $uric_acid = floatval($data['chi_so_axit_uric']);
    $notes = isset($data['ghi_chu']) ? $data['ghi_chu'] : "";

    // Kiểm tra xem người dùng có tồn tại không
    $sql_check_user = "SELECT id_nguoi_dung FROM nguoi_dung WHERE id_nguoi_dung = ?";
    $stmt_check_user = $conn->prepare($sql_check_user);
    $stmt_check_user->bind_param("i", $id_nguoi_dung);
    $stmt_check_user->execute();
    $stmt_check_user->store_result();

    if ($stmt_check_user->num_rows === 0) {
        echo json_encode(["status" => "error", "message" => "Người dùng không tồn tại."]);
        $stmt_check_user->close();
        exit;
    }

    $stmt_check_user->close();

    // Insert dữ liệu vào cơ sở dữ liệu
    $sql_insert = "INSERT INTO nhat_ky_suc_khoe (id_nguoi_dung, ngay, muc_do_dau, chi_so_axit_uric, ghi_chu) VALUES (?, ?, ?, ?, ?)";
    $stmt_insert = $conn->prepare($sql_insert);
    $stmt_insert->bind_param("isdds", $id_nguoi_dung, $date, $pain_level, $uric_acid, $notes);
    
    if ($stmt_insert->execute()) {
        echo json_encode(["status" => "success", "message" => "Thêm nhật ký sức khỏe thành công."]);

        // Gọi API thứ hai (API thông báo ưu đãi)
        $notification_url = 'http://172.22.144.1/ungdung_api/guithongbaovsuudai.php';  // Địa chỉ API gửi thông báo
        $notification_data = json_encode([
            'id_nguoi_dung' => $id_nguoi_dung,
            'tieu_de' => 'Khuyến mãi sức khỏe!',
            'noi_dung' => 'Bạn đã nhận thông báo ưu đãi sức khỏe! Kiểm tra ngay!'
        ]);

        // Cấu hình gửi yêu cầu HTTP POST đến API thứ hai
        $options = [
            'http' => [
                'method' => 'POST',
                'header' => 'Content-Type: application/json',
                'content' => $notification_data
            ]
        ];

        // Gửi yêu cầu đến API thứ 2
        $context = stream_context_create($options);
        $notification_response = file_get_contents($notification_url, false, $context);

        // Kiểm tra kết quả trả về từ API thứ hai
        if ($notification_response === false) {
            echo json_encode(["status" => "error", "message" => "Lỗi khi gửi yêu cầu thông báo."]);
        } else {
            echo json_encode(["status" => "success", "message" => "Thông báo khuyến mãi đã được gửi thành công."]);
        }

    } else {
        echo json_encode(["status" => "error", "message" => "Lỗi khi thêm dữ liệu."]);
    }

    $stmt_insert->close();
    exit;
} else {
    echo json_encode(["status" => "error", "message" => "Phương thức yêu cầu không hợp lệ."]);
    exit;
}
?>
