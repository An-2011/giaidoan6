<?php
ob_clean();
ini_set('display_errors', 1);
error_reporting(E_ALL);

header("Content-Type: application/json; charset=UTF-8");
include 'db_connect.php';

if (!$conn) {
    echo json_encode(["success" => false, "message" => "Kết nối CSDL thất bại"]);
    exit();
}

$data = json_decode(file_get_contents("php://input"));
$key = $data->key ?? '';

switch ($key) {
    case "get_all_appointments":
        $search = "%" . ($data->search ?? '') . "%";
        $stmt = $conn->prepare("SELECT * FROM lich_kham WHERE CAST(id_nguoi_dung AS CHAR) LIKE ? OR CAST(id_bac_si AS CHAR) LIKE ? OR trang_thai LIKE ? ORDER BY id DESC");
        $stmt->bind_param("sss", $search, $search, $search);
        $stmt->execute();
        $result = $stmt->get_result();

        $appointments = [];
        while ($row = $result->fetch_assoc()) {
            $appointments[] = $row;
        }

        echo json_encode(["success" => true, "appointments" => $appointments], JSON_UNESCAPED_UNICODE);
        break;

    case "delete_appointment":
        $id = $data->id ?? 0;
        if ($id <= 0) {
            echo json_encode(["success" => false, "message" => "ID không hợp lệ"]);
            exit();
        }

        $stmt = $conn->prepare("DELETE FROM lich_kham WHERE id = ?");
        $stmt->bind_param("i", $id);
        $success = $stmt->execute();
        echo json_encode(["success" => $success]);
        break;

    case "update_appointment_status":
        $id = $data->id ?? 0;
        $trang_thai = $data->trang_thai ?? '';

        if ($id <= 0 || empty($trang_thai)) {
            echo json_encode(["success" => false, "message" => "Thiếu dữ liệu"]);
            exit();
        }

        $stmt = $conn->prepare("UPDATE lich_kham SET trang_thai = ? WHERE id = ?");
        $stmt->bind_param("si", $trang_thai, $id);
        $success = $stmt->execute();
        echo json_encode(["success" => $success]);
        break;

    case "add_appointment":
        $id_nguoi_dung = $data->id_nguoi_dung ?? 0;
        $id_bac_si = $data->id_bac_si ?? 0;
        $ngay_gio_kham = $data->ngay_gio_kham ?? '';
        $trang_thai = $data->trang_thai ?? 'chờ xác nhận';

        if ($id_nguoi_dung <= 0 || $id_bac_si <= 0 || empty($ngay_gio_kham)) {
            echo json_encode(["success" => false, "message" => "Thiếu thông tin lịch khám"]);
            exit();
        }

        $sql = "INSERT INTO lich_kham (id_nguoi_dung, id_bac_si, ngay_gio_kham, trang_thai, ngay_tao)
                VALUES (?, ?, ?, ?, NOW())";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("iiss", $id_nguoi_dung, $id_bac_si, $ngay_gio_kham, $trang_thai);

        if ($stmt->execute()) {
            echo json_encode(["success" => true, "message" => "Thêm lịch khám thành công"]);
        } else {
            echo json_encode(["success" => false, "message" => "Thêm thất bại: " . $stmt->error]);
        }
        break;

    default:
        echo json_encode(["success" => false, "message" => "Key không hợp lệ"]);
        break;
}

$conn->close();
?>
