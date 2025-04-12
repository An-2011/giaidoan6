<?php
header("Content-Type: application/json; charset=UTF-8");
include 'db_connect.php';

// Kiểm tra kết nối
if (!$conn) {
    echo json_encode(["success" => false, "message" => "Kết nối cơ sở dữ liệu thất bại"]);
    exit();
}

// Nhận dữ liệu từ client
$data = json_decode(file_get_contents("php://input"));

if (!isset($data->key)) {
    echo json_encode(["success" => false, "message" => "Thiếu tham số key"]);
    exit();
}

switch ($data->key) {
    case "get_all":
        $search = "%" . ($data->search ?? '') . "%";
        $stmt = $conn->prepare("SELECT id, ho_ten, chuyen_khoa, kinh_nghiem, dia_chi_phong_kham, so_dien_thoai, ngay_tao FROM bac_si WHERE ho_ten LIKE ? OR chuyen_khoa LIKE ? ORDER BY id DESC");
        $stmt->bind_param("ss", $search, $search);
        $stmt->execute();
        $result = $stmt->get_result();

        $doctors = [];
        while ($row = $result->fetch_assoc()) {
            $doctors[] = $row;
        }

        echo json_encode(["success" => true, "doctors" => $doctors], JSON_UNESCAPED_UNICODE);
        break;

    case "add_doctor":
        $ho_ten = $data->ho_ten ?? '';
        $chuyen_khoa = $data->chuyen_khoa ?? '';
        $kinh_nghiem = (int)($data->kinh_nghiem ?? 0);
        $dia_chi = $data->dia_chi_phong_kham ?? '';
        $so_dien_thoai = $data->so_dien_thoai ?? '';

        if (!$ho_ten || !$chuyen_khoa || !$kinh_nghiem || !$dia_chi || !$so_dien_thoai) {
            echo json_encode(["success" => false, "message" => "Vui lòng điền đầy đủ thông tin bác sĩ"]);
            exit();
        }

        $stmt = $conn->prepare("INSERT INTO bac_si (ho_ten, chuyen_khoa, kinh_nghiem, dia_chi_phong_kham, so_dien_thoai, ngay_tao) VALUES (?, ?, ?, ?, ?, NOW())");
        $stmt->bind_param("ssiss", $ho_ten, $chuyen_khoa, $kinh_nghiem, $dia_chi, $so_dien_thoai);

        if ($stmt->execute()) {
            echo json_encode(["success" => true, "message" => "Thêm bác sĩ thành công"]);
        } else {
            echo json_encode(["success" => false, "message" => "Thêm thất bại: " . $stmt->error]);
        }
        break;

    case "update_doctor":
        $id = (int)($data->id ?? 0);
        $ho_ten = $data->ho_ten ?? '';
        $chuyen_khoa = $data->chuyen_khoa ?? '';
        $kinh_nghiem = (int)($data->kinh_nghiem ?? 0);
        $dia_chi = $data->dia_chi_phong_kham ?? '';
        $so_dien_thoai = $data->so_dien_thoai ?? '';

        if ($id <= 0) {
            echo json_encode(["success" => false, "message" => "ID bác sĩ không hợp lệ"]);
            exit();
        }

        $stmt = $conn->prepare("UPDATE bac_si SET ho_ten=?, chuyen_khoa=?, kinh_nghiem=?, dia_chi_phong_kham=?, so_dien_thoai=? WHERE id=?");
        $stmt->bind_param("ssissi", $ho_ten, $chuyen_khoa, $kinh_nghiem, $dia_chi, $so_dien_thoai, $id);

        if ($stmt->execute()) {
            echo json_encode(["success" => true, "message" => "Cập nhật bác sĩ thành công"]);
        } else {
            echo json_encode(["success" => false, "message" => "Cập nhật thất bại: " . $stmt->error]);
        }
        break;

    case "delete_doctor":
        $id = (int)($data->id ?? 0);

        if ($id <= 0) {
            echo json_encode(["success" => false, "message" => "ID bác sĩ không hợp lệ"]);
            exit();
        }

        $stmt = $conn->prepare("DELETE FROM bac_si WHERE id=?");
        $stmt->bind_param("i", $id);

        if ($stmt->execute()) {
            echo json_encode(["success" => true, "message" => "Xoá bác sĩ thành công"]);
        } else {
            echo json_encode(["success" => false, "message" => "Xoá thất bại: " . $stmt->error]);
        }
        break;

    default:
        echo json_encode(["success" => false, "message" => "Key không hợp lệ"]);
        break;
}

$conn->close();
?>
