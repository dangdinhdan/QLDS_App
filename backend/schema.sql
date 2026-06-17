-- Create database if not exists
CREATE DATABASE IF NOT EXISTS doan_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE doan_db;

-- Drop tables if they exist to start fresh (in correct order of dependencies)
DROP TABLE IF EXISTS tbl_hoadon;
DROP TABLE IF EXISTS tbl_chitietdatsan;
DROP TABLE IF EXISTS tbl_phieudatsan;
DROP TABLE IF EXISTS tbl_khachhang;
DROP TABLE IF EXISTS tbl_banggia_san;
DROP TABLE IF EXISTS tbl_san;
DROP TABLE IF EXISTS tbl_banggiachitiet;
DROP TABLE IF EXISTS tbl_banggia;
DROP TABLE IF EXISTS tbl_taikhoan;

-- Table tbl_taikhoan
CREATE TABLE tbl_taikhoan (
    id INT AUTO_INCREMENT PRIMARY KEY,
    taikhoan VARCHAR(50) NOT NULL UNIQUE,
    matkhau VARCHAR(255) NOT NULL,
    create_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Table tbl_banggia
CREATE TABLE tbl_banggia (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_banggia VARCHAR(50) NOT NULL UNIQUE,
    tenbanggia VARCHAR(100) NOT NULL,
    mota VARCHAR(255),
    create_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    delete_at DATETIME,
    isdelete TINYINT(1) DEFAULT 0
) ENGINE=InnoDB;

-- Table tbl_banggiachitiet
CREATE TABLE tbl_banggiachitiet (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_banggia INT NOT NULL,
    loaingay VARCHAR(50) NOT NULL,
    giobatdau TIME NOT NULL,
    giokethuc TIME NOT NULL,
    dongia DECIMAL(10, 2) NOT NULL,
    create_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_banggia) REFERENCES tbl_banggia(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table tbl_san
CREATE TABLE tbl_san (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_san VARCHAR(50) NOT NULL UNIQUE,
    id_banggia INT,
    ten VARCHAR(100) NOT NULL,
    loaimatsan VARCHAR(50),
    trangthai VARCHAR(50) DEFAULT 'Trong',
    url VARCHAR(255),
    create_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    delete_at DATETIME,
    isdelete TINYINT(1) DEFAULT 0,
    FOREIGN KEY (id_banggia) REFERENCES tbl_banggia(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Table tbl_khachhang
CREATE TABLE tbl_khachhang (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_kh VARCHAR(50) NOT NULL UNIQUE,
    ten VARCHAR(100) NOT NULL,
    sdt VARCHAR(20) NOT NULL UNIQUE,
    create_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_at DATETIME ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Table tbl_phieudatsan
CREATE TABLE tbl_phieudatsan (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_khachhang INT NOT NULL,
    ngaydat DATE NOT NULL,
    trangthai VARCHAR(50) DEFAULT 'Cho xac nhan',
    ghichu VARCHAR(255),
    create_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_khachhang) REFERENCES tbl_khachhang(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table tbl_chitietdatsan
CREATE TABLE tbl_chitietdatsan (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_phieudatsan INT NOT NULL,
    id_san INT NOT NULL,
    giobatdau TIME NOT NULL,
    giokethuc TIME NOT NULL,
    dongia DECIMAL(10, 2) NOT NULL,
    thanhtien DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_phieudatsan) REFERENCES tbl_phieudatsan(id) ON DELETE CASCADE,
    FOREIGN KEY (id_san) REFERENCES tbl_san(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table tbl_hoadon
CREATE TABLE tbl_hoadon (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ma_hoadon VARCHAR(50) NOT NULL UNIQUE,
    id_phieudatsan INT NOT NULL,
    ghichu VARCHAR(255),
    tongtien DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    FOREIGN KEY (id_phieudatsan) REFERENCES tbl_phieudatsan(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Insert Seed Data
-- 1. Accounts (Tai Khoan)
INSERT INTO tbl_taikhoan (taikhoan, matkhau) VALUES 
('admin', 'admin123'),
('user1', 'user123'),
('nv_banhang', 'password123');

-- 2. Price Charts (Bang Gia)
INSERT INTO tbl_banggia (ma_banggia, tenbanggia, mota) VALUES 
('BG_STANDARD', 'Bảng giá chuẩn', 'Áp dụng cho các ngày trong tuần và cuối tuần'),
('BG_VIP', 'Bảng giá VIP', 'Áp dụng cho sân VIP');

-- 3. Price Chart Details (Bang Gia Chi Tiet)
INSERT INTO tbl_banggiachitiet (id_banggia, loaingay, giobatdau, giokethuc, dongia) VALUES 
(1, 'Ngày thường', '05:00:00', '16:00:00', 80000.00),
(1, 'Ngày thường', '16:00:00', '22:00:00', 120000.00),
(1, 'Cuối tuần', '05:00:00', '22:00:00', 150000.00),
(2, 'Ngày thường', '05:00:00', '16:00:00', 100000.00),
(2, 'Ngày thường', '16:00:00', '22:00:00', 150000.00),
(2, 'Cuối tuần', '05:00:00', '22:00:00', 200000.00);

-- 4. Courts (San)
INSERT INTO tbl_san (ma_san, ten, loaimatsan, trangthai, url, id_banggia) VALUES 
('SAN01', 'San Pickleball A (Standard)', 'Mat son', 'Trong', 'https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=500', 1),
('SAN02', 'San Pickleball B (Standard)', 'Mat son', 'Trong', 'https://images.unsplash.com/photo-1595435934249-5df7ed86e1c0?w=500', 1),
('SAN03', 'San Pickleball C (VIP)', 'Mat thap co', 'Trong', 'https://images.unsplash.com/photo-1530541930197-ff16ac917b0e?w=500', 2),
('SAN04', 'San Pickleball D (Standard)', 'Mat son', 'Trong', 'https://images.unsplash.com/photo-1599447421416-3414500d18a5?w=500', 1);

-- 5. Customers (Khach Hang)
INSERT INTO tbl_khachhang (ma_kh, ten, sdt) VALUES 
('KH001', 'Nguyen Van A', '0912345678'),
('KH002', 'Tran Thi B', '0987654321'),
('KH003', 'Le Hoang C', '0905123456');

-- 6. Booking Slips (Phieu Dat San)
INSERT INTO tbl_phieudatsan (id_khachhang, ngaydat, trangthai, ghichu) VALUES 
(1, '2026-06-11', 'Da thanh toan', 'Dat san buoi sang'),
(2, '2026-06-11', 'Cho xac nhan', 'Dat truoc san vip vao buoi toi');

-- 7. Booking Details (Chi Tiet Dat San)
INSERT INTO tbl_chitietdatsan (id_phieudatsan, id_san, giobatdau, giokethuc, dongia, thanhtien) VALUES 
(1, 1, '08:00:00', '10:00:00', 80000.00, 160000.00), -- KH001 booking SAN01 for 2 hours
(2, 3, '18:00:00', '20:00:00', 120000.00, 240000.00); -- KH002 booking SAN03 for 2 hours

-- 8. Invoices (Hoa Don)
INSERT INTO tbl_hoadon (ma_hoadon, id_phieudatsan, ghichu, tongtien) VALUES 
('HD001', 1, 'Hoa don thanh toan phieu dat san 1', 160000.00);
