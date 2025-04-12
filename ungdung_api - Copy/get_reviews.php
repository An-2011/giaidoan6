<?php
header("Content-Type: application/json; charset=UTF-8");
include 'db_connect.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $action = $_POST['action'] ?? '';

    if ($action === 'get') {
        $keyword = $_POST['keyword'] ?? '';

        $sql = "SELECT dg.id, dg.id_nguoi_dung, dg.id_bac_si, dg.diem_so, dg.nhan_xet, dg.ngay_tao,
                       nd.ho_ten AS ten_nguoi_dung, bs.ho_ten AS ten_bac_si
                FROM danh_gia dg
                JOIN nguoi_dung nd ON dg.id_nguoi_dung = nd.id_nguoi_dung
                JOIN bac_si bs ON dg.id_bac_si = bs.id
                WHERE nd.ho_ten LIKE ? OR bs.ho_ten LIKE ?
                ORDER BY dg.ngay_tao DESC";

        $stmt = $conn->prepare($sql);
        $search = '%' . $keyword . '%';
        $stmt->bind_param("ss", $search, $search);
        $stmt->execute();
        $result = $stmt->get_result();

        $reviews = [];
        while ($row = $result->fetch_assoc()) {
            $reviews[] = [
                'id' => $row['id'],
                'id_nguoi_dung' => $row['id_nguoi_dung'],
                'ten_nguoi_dung' => $row['ten_nguoi_dung'],
                'id_bac_si' => $row['id_bac_si'],
                'ten_bac_si' => $row['ten_bac_si'],
                'diem_so' => $row['diem_so'],
                'nhan_xet' => $row['nhan_xet'],
                'ngay_tao' => $row['ngay_tao']
            ];
        }

        echo json_encode(['success' => true, 'reviews' => $reviews]);
        exit;
    }

    if ($action === 'delete') {
        $id = $_POST['id'] ?? '';
        if ($id !== '') {
            $stmt = $conn->prepare("DELETE FROM danh_gia WHERE id = ?");
            $stmt->bind_param("i", $id);
            if ($stmt->execute()) {
                echo json_encode(['success' => true, 'message' => 'Xoá thành công']);
            } else {
                echo json_encode(['success' => false, 'message' => 'Xoá thất bại']);
            }
        } else {
            echo json_encode(['success' => false, 'message' => 'Thiếu ID để xoá']);
        }
        exit;
    }

    echo json_encode(['success' => false, 'message' => 'Tham số action không hợp lệ']);
    exit;
}

echo json_encode(['success' => false, 'message' => 'Chỉ hỗ trợ POST']);
?>
