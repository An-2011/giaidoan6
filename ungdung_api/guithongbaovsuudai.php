<?php
include 'db_connect.php';
header('Content-Type: application/json; charset=UTF-8');

// Nhận dữ liệu JSON từ request
$json = file_get_contents('php://input');
$data = json_decode($json, true);

// Debug: Ghi log dữ liệu từ Android
file_put_contents("log.txt", "[" . date("Y-m-d H:i:s") . "] Request: " . print_r($data, true) . "\n", FILE_APPEND);

// Kiểm tra tham số action
$action = $data['action'] ?? null;
if (!$action) {
    echo json_encode(["status" => "error", "message" => "Thiếu tham số action"]);
    exit;
}

// ✅ Xử lý gửi thông báo (send)
if ($action === "send") {
    $tieu_de = $data['tieu_de'] ?? null;
    $noi_dung = $data['noi_dung'] ?? null;
    $user_id = $data['user_id'] ?? null;

    // Kiểm tra dữ liệu cần thiết
    if (!$tieu_de || !$noi_dung) {
        echo json_encode(["status" => "error", "message" => "Thiếu tiêu đề hoặc nội dung"]);
        exit;
    }

    if ($user_id) {
        // Gửi thông báo cho một người dùng cụ thể
        $sql = "INSERT INTO thong_bao (id_nguoi_dung, tieu_de, noi_dung, ngay_tao, trang_thai, loai) 
                VALUES (?, ?, ?, NOW(), 'Chưa đọc', 'uu_dai')";
        $stmt = $conn->prepare($sql);
        if ($stmt) {
            $stmt->bind_param("iss", $user_id, $tieu_de, $noi_dung);
            $stmt->execute();
            $stmt->close();
            echo json_encode(["status" => "success", "message" => "Gửi thông báo thành công cho người dùng $user_id"]);
        } else {
            echo json_encode(["status" => "error", "message" => "Lỗi khi chèn dữ liệu"]);
        }
    } else {
        // Gửi thông báo cho tất cả người dùng
        $sql_users = "SELECT id FROM nguoi_dung";
        $result_users = $conn->query($sql_users);

        if ($result_users->num_rows > 0) {
            while ($user = $result_users->fetch_assoc()) {
                $user_id_insert = $user['id'];
                $sql = "INSERT INTO thong_bao (id_nguoi_dung, tieu_de, noi_dung, ngay_tao, trang_thai, loai) 
                        VALUES (?, ?, ?, NOW(), 'Chưa đọc', 'uu_dai')";
                $stmt = $conn->prepare($sql);
                if ($stmt) {
                    $stmt->bind_param("iss", $user_id_insert, $tieu_de, $noi_dung);
                    $stmt->execute();
                    $stmt->close();
                }
            }
            echo json_encode(["status" => "success", "message" => "Gửi thông báo cho tất cả người dùng thành công"]);
        } else {
            echo json_encode(["status" => "error", "message" => "Không có người dùng nào"]);
        }
    }
    $conn->close();
    exit;
}

// ✅ Xử lý lấy thông báo (fetch)
if ($action === "fetch") {
    $user_id = $data['user_id'] ?? null;
    if (!$user_id) {
        echo json_encode(["status" => "error", "message" => "Thiếu tham số user_id"]);
        exit;
    }

    // Lấy thông báo cho người dùng
    $sql = "SELECT * FROM thong_bao WHERE id_nguoi_dung = ? AND trang_thai = 'Chưa đọc' ORDER BY ngay_tao DESC";
    $stmt = $conn->prepare($sql);
    if ($stmt) {
        $stmt->bind_param("i", $user_id);
        $stmt->execute();
        $result = $stmt->get_result();
        
        if ($result->num_rows > 0) {
            $notifications = [];
            while ($row = $result->fetch_assoc()) {
                $notifications[] = $row;
            }
            echo json_encode(["status" => "success", "data" => $notifications]);
        } else {
            echo json_encode(["status" => "success", "message" => "Không có thông báo mới"]);
        }
        $stmt->close();
    } else {
        echo json_encode(["status" => "error", "message" => "Lỗi khi truy vấn dữ liệu"]);
    }
    $conn->close();
    exit;
}

// Nếu action không hợp lệ
echo json_encode(["status" => "error", "message" => "Action không hợp lệ: " . $action]);
$conn->close();
?>
