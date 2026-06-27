-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th6 27, 2026 lúc 05:28 PM
-- Phiên bản máy phục vụ: 10.4.32-MariaDB
-- Phiên bản PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `doan_db`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tbl_banggia`
--

CREATE TABLE `tbl_banggia` (
  `id` int(11) NOT NULL,
  `ma_banggia` varchar(50) NOT NULL,
  `tenbanggia` varchar(100) NOT NULL,
  `mota` varchar(255) DEFAULT NULL,
  `create_at` datetime DEFAULT current_timestamp(),
  `update_at` datetime DEFAULT NULL ON UPDATE current_timestamp(),
  `delete_at` datetime DEFAULT NULL,
  `isdelete` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `tbl_banggia`
--

INSERT INTO `tbl_banggia` (`id`, `ma_banggia`, `tenbanggia`, `mota`, `create_at`, `update_at`, `delete_at`, `isdelete`) VALUES
(1, 'BG_STANDARD', 'Bảng giá chuẩn', 'Áp dụng cho các ngày trong tuần và cuối tuần', '2026-06-13 04:44:34', NULL, NULL, 0),
(2, 'BG_VIP', 'Bang gia VIP Sieu Cap', 'Áp dụng cho sân VIP', '2026-06-13 04:44:34', '2026-06-14 01:53:29', NULL, 0),
(3, 'pickerball1', 'bang gia san pickerball1', '', '2026-06-14 02:36:07', NULL, NULL, 0),
(4, 'caulong', 'cau long 1', '', '2026-06-14 04:23:44', '2026-06-14 04:24:51', NULL, 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tbl_banggiachitiet`
--

CREATE TABLE `tbl_banggiachitiet` (
  `id` int(11) NOT NULL,
  `id_banggia` int(11) NOT NULL,
  `loaingay` varchar(50) NOT NULL,
  `giobatdau` time NOT NULL,
  `giokethuc` time NOT NULL,
  `dongia` double NOT NULL,
  `create_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `tbl_banggiachitiet`
--

INSERT INTO `tbl_banggiachitiet` (`id`, `id_banggia`, `loaingay`, `giobatdau`, `giokethuc`, `dongia`, `create_at`) VALUES
(1, 1, 'Ngày thường', '05:00:00', '16:00:00', 80000, '2026-06-13 04:44:34'),
(2, 1, 'Ngày thường', '16:00:00', '22:00:00', 120000, '2026-06-13 04:44:34'),
(3, 1, 'Cuối tuần', '05:00:00', '22:00:00', 150000, '2026-06-13 04:44:34'),
(4, 2, 'Ngày thường', '05:00:00', '16:00:00', 100000, '2026-06-13 04:44:34'),
(5, 2, 'Ngày thường', '16:00:00', '22:00:00', 150000, '2026-06-13 04:44:34'),
(6, 2, 'Cuối tuần', '05:00:00', '22:00:00', 200000, '2026-06-13 04:44:34'),
(9, 4, 'Ngày thường', '06:00:00', '17:00:00', 150000, '2026-06-14 04:24:22');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tbl_chitietdatsan`
--

CREATE TABLE `tbl_chitietdatsan` (
  `id` int(11) NOT NULL,
  `id_phieudatsan` int(11) NOT NULL,
  `id_san` int(11) NOT NULL,
  `giobatdau` time NOT NULL,
  `giokethuc` time NOT NULL,
  `dongia` double NOT NULL,
  `thanhtien` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `tbl_chitietdatsan`
--

INSERT INTO `tbl_chitietdatsan` (`id`, `id_phieudatsan`, `id_san`, `giobatdau`, `giokethuc`, `dongia`, `thanhtien`) VALUES
(1, 1, 1, '08:00:00', '10:00:00', 80000, 160000),
(2, 2, 3, '18:00:00', '20:00:00', 120000, 240000),
(3, 3, 2, '06:00:00', '06:45:00', 15, 11.25),
(8, 8, 2, '07:00:00', '09:00:00', 150000, 300000),
(12, 12, 4, '06:00:00', '09:00:00', 150000, 450000),
(13, 13, 6, '06:00:00', '09:00:00', 150000, 450000),
(14, 14, 5, '06:00:00', '09:00:00', 150000, 450000),
(15, 15, 3, '06:00:00', '09:00:00', 150000, 450000),
(16, 16, 2, '09:00:00', '11:00:00', 150000, 300000),
(17, 17, 5, '19:00:00', '22:00:00', 150000, 450000),
(18, 18, 6, '19:00:00', '22:00:00', 150000, 450000),
(25, 25, 4, '18:00:00', '22:00:00', 150000, 600000),
(26, 26, 5, '19:00:00', '22:00:00', 150000, 450000),
(27, 27, 6, '19:00:00', '22:00:00', 150000, 450000),
(28, 28, 3, '19:00:00', '21:00:00', 150000, 300000),
(29, 29, 3, '19:00:00', '20:00:00', 150000, 150000),
(30, 30, 3, '15:00:00', '16:00:00', 150000, 150000),
(31, 31, 4, '14:00:00', '15:00:00', 150000, 150000),
(32, 32, 5, '16:00:00', '18:00:00', 150000, 300000),
(33, 33, 6, '16:00:00', '18:00:00', 150000, 300000),
(34, 34, 2, '08:00:00', '12:00:00', 150000, 600000),
(35, 35, 5, '08:00:00', '12:00:00', 150000, 600000),
(36, 36, 3, '06:00:00', '09:00:00', 150000, 450000);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tbl_hoadon`
--

CREATE TABLE `tbl_hoadon` (
  `id` int(11) NOT NULL,
  `ma_hoadon` varchar(50) NOT NULL,
  `id_phieudatsan` int(11) NOT NULL,
  `ghichu` varchar(255) DEFAULT NULL,
  `tongtien` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `tbl_hoadon`
--

INSERT INTO `tbl_hoadon` (`id`, `ma_hoadon`, `id_phieudatsan`, `ghichu`, `tongtien`) VALUES
(1, 'HD001', 1, 'Hoa don thanh toan phieu dat san 1', 160000),
(2, 'HD017', 17, 'Hóa đơn thanh toán cho phiếu đặt sân #17', 450000),
(3, 'HD018', 18, 'Hóa đơn thanh toán cho phiếu đặt sân #18', 450000),
(4, 'HD025', 25, 'Hóa đơn thanh toán cho phiếu đặt sân #25', 600000),
(5, 'HD026', 26, 'Hóa đơn thanh toán cho phiếu đặt sân #26', 450000),
(6, 'HD027', 27, 'Hóa đơn thanh toán cho phiếu đặt sân #27', 450000),
(7, 'HD030', 30, 'Hóa đơn thanh toán cho phiếu đặt sân #30', 150000),
(8, 'HD029', 29, 'Hóa đơn thanh toán cho phiếu đặt sân #29', 150000);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tbl_khachhang`
--

CREATE TABLE `tbl_khachhang` (
  `id` int(11) NOT NULL,
  `ma_kh` varchar(50) NOT NULL,
  `ten` varchar(100) NOT NULL,
  `sdt` varchar(20) NOT NULL,
  `create_at` datetime DEFAULT current_timestamp(),
  `update_at` datetime DEFAULT NULL ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `tbl_khachhang`
--

INSERT INTO `tbl_khachhang` (`id`, `ma_kh`, `ten`, `sdt`, `create_at`, `update_at`) VALUES
(1, 'KH001', 'Test User', '0999999999', '2026-06-13 04:44:34', '2026-06-27 16:10:17'),
(2, 'KH002', 'Tran Thi B', '0987654321', '2026-06-13 04:44:34', NULL),
(3, 'KH003', 'Le Hoang C', '0905123456', '2026-06-13 04:44:34', NULL),
(4, 'KH234', 'dann', '0111111111', '2026-06-17 06:11:43', '2026-06-17 06:42:36'),
(8, 'KH171', 'phuc', '0111111112', '2026-06-17 07:00:11', NULL),
(9, 'KH427', 'dann', '01111111111', '2026-06-17 07:00:29', NULL),
(10, 'KH406', 'dannn', '0444444444', '2026-06-27 15:33:39', NULL),
(11, 'KH732', 'huyy', '2222222222', '2026-06-27 21:47:29', NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tbl_phieudatsan`
--

CREATE TABLE `tbl_phieudatsan` (
  `id` int(11) NOT NULL,
  `id_khachhang` int(11) NOT NULL,
  `ngaydat` date NOT NULL,
  `trangthai` varchar(50) DEFAULT 'Cho xac nhan',
  `ghichu` varchar(255) DEFAULT NULL,
  `create_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `tbl_phieudatsan`
--

INSERT INTO `tbl_phieudatsan` (`id`, `id_khachhang`, `ngaydat`, `trangthai`, `ghichu`, `create_at`) VALUES
(1, 1, '2026-06-11', 'Hoàn thành', 'Test Notes', '2026-06-13 04:44:34'),
(2, 2, '2026-06-11', 'Cho xac nhan', 'Dat truoc san vip vao buoi toi', '2026-06-13 04:44:34'),
(3, 4, '2026-06-17', 'Đã đặt', 'Đặt sân từ ứng dụng di động', '2026-06-17 06:11:43'),
(8, 4, '2026-06-17', 'Đã đặt', 'Đặt sân từ ứng dụng di động', '2026-06-17 06:25:53'),
(12, 4, '2026-06-17', 'Đã đặt', 'Đặt sân từ ứng dụng di động', '2026-06-17 06:42:36'),
(13, 4, '2026-06-17', 'Đã đặt', 'Đặt sân từ ứng dụng di động', '2026-06-17 06:42:37'),
(14, 8, '2026-06-17', 'Đã đặt', 'Đặt sân từ ứng dụng di động', '2026-06-17 07:00:11'),
(15, 9, '2026-06-17', 'Đã đặt', 'Đặt sân từ ứng dụng di động', '2026-06-17 07:00:29'),
(16, 4, '2026-06-17', 'Đã đặt', 'Đặt sân từ ứng dụng di động', '2026-06-17 07:19:33'),
(17, 4, '2026-06-11', 'Hoàn thành', 'Đặt sân từ ứng dụng di động', '2026-06-27 13:29:01'),
(18, 4, '2026-06-11', 'Hoàn thành', 'Đặt sân từ ứng dụng di động', '2026-06-27 13:29:01'),
(25, 4, '2026-06-27', 'Hoàn thành', 'Đặt sân từ ứng dụng di động', '2026-06-27 13:39:05'),
(26, 4, '2026-06-27', 'Hoàn thành', 'Đặt sân từ ứng dụng di động', '2026-06-27 13:39:06'),
(27, 4, '2026-06-27', 'Hoàn thành', 'Đặt sân từ ứng dụng di động', '2026-06-27 13:39:29'),
(28, 4, '2026-06-28', 'Đã đặt', 'Đặt sân từ ứng dụng di động', '2026-06-27 13:39:50'),
(29, 9, '2026-06-27', 'Hoàn thành', 'Đặt sân từ ứng dụng di động', '2026-06-27 15:32:41'),
(30, 4, '2026-06-27', 'Hoàn thành', 'Đặt sân từ ứng dụng di động', '2026-06-27 15:33:12'),
(31, 10, '2026-06-27', 'Đã đặt', 'Đặt sân từ ứng dụng di động', '2026-06-27 15:33:39'),
(32, 4, '2026-06-27', 'Đã đặt', '', '2026-06-27 16:25:08'),
(33, 4, '2026-06-27', 'Đã đặt', '', '2026-06-27 16:25:08'),
(34, 11, '2026-07-01', 'Đã đặt', '', '2026-06-27 21:47:29'),
(35, 11, '2026-07-01', 'Đã đặt', '', '2026-06-27 21:47:29'),
(36, 4, '2026-07-01', 'Đã đặt', '', '2026-06-27 21:47:40');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tbl_san`
--

CREATE TABLE `tbl_san` (
  `id` int(11) NOT NULL,
  `ma_san` varchar(50) NOT NULL,
  `id_banggia` int(11) DEFAULT NULL,
  `ten` varchar(100) NOT NULL,
  `loaimatsan` varchar(50) DEFAULT NULL,
  `trangthai` varchar(50) DEFAULT 'Trong',
  `url` varchar(255) DEFAULT NULL,
  `create_at` datetime DEFAULT current_timestamp(),
  `delete_at` datetime DEFAULT NULL,
  `isdelete` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `tbl_san`
--

INSERT INTO `tbl_san` (`id`, `ma_san`, `id_banggia`, `ten`, `loaimatsan`, `trangthai`, `url`, `create_at`, `delete_at`, `isdelete`) VALUES
(1, 'SAN01', NULL, 'San Pickleball A (Standard)', 'Trong nhà', 'Trong', 'https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=500', '2026-06-13 04:44:34', NULL, 1),
(2, 'SAN02', 3, 'San Pickleball B (Standard)', 'Trong nhà', 'Đã đặt', 'https://images.unsplash.com/photo-1595435934249-5df7ed86e1c0?w=500', '2026-06-13 04:44:34', NULL, 0),
(3, 'SAN03', 2, 'San Pickleball C (VIP)', 'Mat thap co', 'Đã đặt', 'https://images.unsplash.com/photo-1530541930197-ff16ac917b0e?w=500', '2026-06-13 04:44:34', NULL, 0),
(4, 'SAN04', 1, 'San Pickleball D (Standard)', 'Mat son', 'Đã đặt', 'https://images.unsplash.com/photo-1599447421416-3414500d18a5?w=500', '2026-06-13 04:44:34', NULL, 0),
(5, 'caulong12', 1, 'cau long 12', 'Trong nhà', 'Đã đặt', 'https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=500', '2026-06-15 05:41:45', NULL, 0),
(6, 'test1', 3, 'san test', 'Trong nhà', 'Đã đặt', 'https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=500', '2026-06-15 05:44:55', NULL, 0);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tbl_taikhoan`
--

CREATE TABLE `tbl_taikhoan` (
  `id` int(11) NOT NULL,
  `taikhoan` varchar(50) NOT NULL,
  `matkhau` varchar(255) NOT NULL,
  `create_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `tbl_taikhoan`
--

INSERT INTO `tbl_taikhoan` (`id`, `taikhoan`, `matkhau`, `create_at`) VALUES
(1, 'admin', 'admin123', '2026-06-13 04:44:34'),
(2, 'user1', 'user123', '2026-06-13 04:44:34'),
(3, 'nv_banhang', 'password123', '2026-06-13 04:44:34');

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `tbl_banggia`
--
ALTER TABLE `tbl_banggia`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `ma_banggia` (`ma_banggia`);

--
-- Chỉ mục cho bảng `tbl_banggiachitiet`
--
ALTER TABLE `tbl_banggiachitiet`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_banggia` (`id_banggia`);

--
-- Chỉ mục cho bảng `tbl_chitietdatsan`
--
ALTER TABLE `tbl_chitietdatsan`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_phieudatsan` (`id_phieudatsan`),
  ADD KEY `id_san` (`id_san`);

--
-- Chỉ mục cho bảng `tbl_hoadon`
--
ALTER TABLE `tbl_hoadon`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `ma_hoadon` (`ma_hoadon`),
  ADD KEY `id_phieudatsan` (`id_phieudatsan`);

--
-- Chỉ mục cho bảng `tbl_khachhang`
--
ALTER TABLE `tbl_khachhang`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `ma_kh` (`ma_kh`),
  ADD UNIQUE KEY `sdt` (`sdt`),
  ADD UNIQUE KEY `sdt_2` (`sdt`);

--
-- Chỉ mục cho bảng `tbl_phieudatsan`
--
ALTER TABLE `tbl_phieudatsan`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_khachhang` (`id_khachhang`);

--
-- Chỉ mục cho bảng `tbl_san`
--
ALTER TABLE `tbl_san`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `ma_san` (`ma_san`),
  ADD KEY `id_banggia` (`id_banggia`);

--
-- Chỉ mục cho bảng `tbl_taikhoan`
--
ALTER TABLE `tbl_taikhoan`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `taikhoan` (`taikhoan`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `tbl_banggia`
--
ALTER TABLE `tbl_banggia`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT cho bảng `tbl_banggiachitiet`
--
ALTER TABLE `tbl_banggiachitiet`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT cho bảng `tbl_chitietdatsan`
--
ALTER TABLE `tbl_chitietdatsan`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- AUTO_INCREMENT cho bảng `tbl_hoadon`
--
ALTER TABLE `tbl_hoadon`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT cho bảng `tbl_khachhang`
--
ALTER TABLE `tbl_khachhang`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT cho bảng `tbl_phieudatsan`
--
ALTER TABLE `tbl_phieudatsan`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- AUTO_INCREMENT cho bảng `tbl_san`
--
ALTER TABLE `tbl_san`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT cho bảng `tbl_taikhoan`
--
ALTER TABLE `tbl_taikhoan`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `tbl_banggiachitiet`
--
ALTER TABLE `tbl_banggiachitiet`
  ADD CONSTRAINT `tbl_banggiachitiet_ibfk_1` FOREIGN KEY (`id_banggia`) REFERENCES `tbl_banggia` (`id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `tbl_chitietdatsan`
--
ALTER TABLE `tbl_chitietdatsan`
  ADD CONSTRAINT `tbl_chitietdatsan_ibfk_1` FOREIGN KEY (`id_phieudatsan`) REFERENCES `tbl_phieudatsan` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `tbl_chitietdatsan_ibfk_2` FOREIGN KEY (`id_san`) REFERENCES `tbl_san` (`id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `tbl_hoadon`
--
ALTER TABLE `tbl_hoadon`
  ADD CONSTRAINT `tbl_hoadon_ibfk_1` FOREIGN KEY (`id_phieudatsan`) REFERENCES `tbl_phieudatsan` (`id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `tbl_phieudatsan`
--
ALTER TABLE `tbl_phieudatsan`
  ADD CONSTRAINT `tbl_phieudatsan_ibfk_1` FOREIGN KEY (`id_khachhang`) REFERENCES `tbl_khachhang` (`id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `tbl_san`
--
ALTER TABLE `tbl_san`
  ADD CONSTRAINT `tbl_san_ibfk_1` FOREIGN KEY (`id_banggia`) REFERENCES `tbl_banggia` (`id`) ON DELETE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
