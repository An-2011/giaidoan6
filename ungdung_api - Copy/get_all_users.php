<?php
header("Content-Type: application/json; charset=UTF-8");
include 'db_connect.php';

// Kiểm tra kết nối CSDL
if ($conn === false) {
    echo json_encode(["success" => false, "message" => "Lỗi kết nối cơ sở dữ liệu"]);
    exit();
}

$data = json_decode(file_get_contents("php://input"));

if (!isset($data->key)) {
    echo json_encode(["success" => false, "message" => "Thiếu tham số key"]);
    exit();
}

switch ($data->key) {
    case "get_all":
        $sql = "SELECT id_nguoi_dung, ho_ten, email, so_dien_thoai, ngay_sinh, gioi_tinh, ngay_tao FROM nguoi_dung";
        $result = mysqli_query($conn, $sql);
        if ($result) {
            $users = [];
            while ($row = mysqli_fetch_assoc($result)) {
                $users[] = $row;
            }
            echo json_encode(["success" => true, "users" => $users]);
        } else {
            echo json_encode(["success" => false, "message" => "Lỗi truy vấn: " . mysqli_error($conn)]);
        }
        break;

    case "add_user":
        // Kiểm tra các tham số đầu vào
        $ho_ten = $data->ho_ten ?? '';
        $email = $data->email ?? '';
        $so_dien_thoai = $data->so_dien_thoai ?? '';
        $ngay_sinh = $data->ngay_sinh ?? '';
        $gioi_tinh = $data->gioi_tinh ?? '';

        // Kiểm tra các tham số có hợp lệ không
        if (empty($ho_ten) || empty($email) || empty($so_dien_thoai) || empty($ngay_sinh) || empty($gioi_tinh)) {
            echo json_encode(["success" => false, "message" => "Các tham số không được để trống"]);
            exit();
        }

        $sql = "INSERT INTO nguoi_dung (ho_ten, email, so_dien_thoai, ngay_sinh, gioi_tinh, ngay_tao)
                VALUES (?, ?, ?, ?, ?, NOW())";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("sssss", $ho_ten, $email, $so_dien_thoai, $ngay_sinh, $gioi_tinh);

        if ($stmt->execute()) {
            echo json_encode(["success" => true, "message" => "Thêm người dùng thành công"]);
        } else {
            echo json_encode(["success" => false, "message" => "Thêm thất bại: " . $stmt->error]);
        }
        break;

    case "update_user":
        $id = $data->id_nguoi_dung ?? 0;
        $ho_ten = $data->ho_ten ?? '';
        $email = $data->email ?? '';
        $so_dien_thoai = $data->so_dien_thoai ?? '';
        $ngay_sinh = $data->ngay_sinh ?? '';
        $gioi_tinh = $data->gioi_tinh ?? '';

        // Kiểm tra ID hợp lệ
        if ($id <= 0) {
            echo json_encode(["success" => false, "message" => "ID người dùng không hợp lệ"]);
            exit();
        }

        $sql = "UPDATE nguoi_dung SET ho_ten=?, email=?, so_dien_thoai=?, ngay_sinh=?, gioi_tinh=? WHERE id_nguoi_dung=?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("sssssi", $ho_ten, $email, $so_dien_thoai, $ngay_sinh, $gioi_tinh, $id);

        if ($stmt->execute()) {
            echo json_encode(["success" => true, "message" => "Cập nhật người dùng thành công"]);
        } else {
            echo json_encode(["success" => false, "message" => "Cập nhật thất bại: " . $stmt->error]);
        }
        break;

    case "delete_user":
        $id = $data->id_nguoi_dung ?? 0;

        if ($id <= 0) {
            echo json_encode(["success" => false, "message" => "ID người dùng không hợp lệ"]);
            exit();
        }

        $sql = "DELETE FROM nguoi_dung WHERE id_nguoi_dung=?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("i", $id);

        if ($stmt->execute()) {
            echo json_encode(["success" => true, "message" => "Xóa người dùng thành công"]);
        } else {
            echo json_encode(["success" => false, "message" => "Xóa thất bại: " . $stmt->error]);
        }
        break;

    case "search_user":
        $keyword = "%" . ($data->keyword ?? '') . "%";

        $sql = "SELECT id_nguoi_dung, ho_ten, email, so_dien_thoai, ngay_sinh, gioi_tinh, ngay_tao
                FROM nguoi_dung
                WHERE ho_ten LIKE ? OR email LIKE ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("ss", $keyword, $keyword);
        $stmt->execute();
        $result = $stmt->get_result();

        $users = [];
        while ($row = $result->fetch_assoc()) {
            $users[] = $row;
        }

        echo json_encode(["success" => true, "users" => $users]);
        break;

    default:
        echo json_encode(["success" => false, "message" => "Key không hợp lệ"]);
        break;
}
?>