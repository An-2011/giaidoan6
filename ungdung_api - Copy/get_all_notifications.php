<?php
include 'db_connect.php';
header('Content-Type: application/json');

$data = json_decode(file_get_contents("php://input"), true);
$action = $data['action'] ?? '';

$limit = intval($data['limit'] ?? 20);
$offset = intval($data['offset'] ?? 0);

if ($action === 'all') {
    $stmt = $conn->prepare("
        SELECT 
            tb.id,
            tb.id_nguoi_dung,
            tb.tieu_de,
            tb.noi_dung,
            tb.trang_thai,
            tb.ngay_tao,
            tb.loai,
            nd.ho_ten AS nguoi_nhan
        FROM thong_bao tb
        JOIN nguoi_dung nd ON tb.id_nguoi_dung = nd.id_nguoi_dung
        ORDER BY tb.ngay_tao DESC
        LIMIT ? OFFSET ?
    ");
    $stmt->bind_param("ii", $limit, $offset);
    $stmt->execute();
    $result = $stmt->get_result();
    $data = [];

    while ($row = $result->fetch_assoc()) {
        $data[] = [
            'id' => $row['id'],
            'id_nguoi_dung' => $row['id_nguoi_dung'],
            'tieu_de' => $row['tieu_de'],
            'noi_dung' => $row['noi_dung'],
            'trang_thai' => $row['trang_thai'],
            'ngay_tao' => $row['ngay_tao'],
            'loai' => $row['loai'],
            'nguoi_nhan' => $row['nguoi_nhan']
        ];
    }

    echo json_encode(["status" => "success", "data" => $data]);
    exit;
}

if ($action === 'list') {
    $search = $data['search'] ?? '';
    $param = '%' . $search . '%';

    $stmt = $conn->prepare("
        SELECT 
            tb.id,
            tb.id_nguoi_dung,
            tb.tieu_de,
            tb.noi_dung,
            tb.trang_thai,
            tb.ngay_tao,
            tb.loai,
            nd.ho_ten AS nguoi_nhan
        FROM thong_bao tb
        JOIN nguoi_dung nd ON tb.id_nguoi_dung = nd.id_nguoi_dung
        WHERE tb.tieu_de LIKE ? OR nd.ho_ten LIKE ?
        ORDER BY tb.ngay_tao DESC
        LIMIT ? OFFSET ?
    ");
    $stmt->bind_param("ssii", $param, $param, $limit, $offset);
    $stmt->execute();
    $result = $stmt->get_result();
    $data = [];

    while ($row = $result->fetch_assoc()) {
        $data[] = [
            'id' => $row['id'],
            'id_nguoi_dung' => $row['id_nguoi_dung'],
            'tieu_de' => $row['tieu_de'],
            'noi_dung' => $row['noi_dung'],
            'trang_thai' => $row['trang_thai'],
            'ngay_tao' => $row['ngay_tao'],
            'loai' => $row['loai'],
            'nguoi_nhan' => $row['nguoi_nhan']
        ];
    }

    echo json_encode(["status" => "success", "data" => $data]);
    exit;
}

if ($action === 'delete') {
    $id = intval($data['id'] ?? 0);

    if ($id > 0) {
        $stmt = $conn->prepare("DELETE FROM thong_bao WHERE id = ?");
        if ($stmt === false) {
            echo json_encode(["status" => "error", "message" => "Lỗi chuẩn bị truy vấn: " . $conn->error]);
            exit;
        }

        $stmt->bind_param("i", $id);
        if ($stmt->execute()) {
            if ($stmt->affected_rows > 0) {
                echo json_encode(["status" => "success"]);
            } else {
                echo json_encode(["status" => "fail", "message" => "Không tìm thấy ID hoặc không xoá được"]);
            }
        } else {
            echo json_encode(["status" => "fail", "message" => "Lỗi khi xoá: " . $stmt->error]);
        }
    } else {
        echo json_encode(["status" => "error", "message" => "ID không hợp lệ"]);
    }
    exit;
}

echo json_encode(["status" => "error", "message" => "Hành động không hợp lệ"]);
