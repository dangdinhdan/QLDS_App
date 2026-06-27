# PicklePro Management System - Hướng dẫn Cài đặt & Triển khai

Chào mừng bạn đến với **PicklePro Manager** - hệ thống quản lý sân Pickleball hiện đại. Dự án bao gồm hai thành phần chính:
1. **Backend**: Spring Boot REST API kết nối cơ sở dữ liệu MySQL.
2. **Mobile Client**: Ứng dụng Android (Java) cung cấp giao diện quản lý lịch trình đặt sân, doanh thu, khách hàng và bảng giá.

---

## 📋 Yêu cầu Hệ thống
Trước khi triển khai, hãy đảm bảo máy tính của bạn đã được cài đặt đầy đủ các công cụ sau:
* **Java JDK**: Phiên bản 17 hoặc 21.
* **Android Studio**: Để biên dịch và chạy ứng dụng di động.
* **Cơ sở dữ liệu**: MySQL (khuyến nghị cài đặt thông qua **XAMPP** hoặc **Laragon**).
* **Quản lý dependencies**: Gradle (đã tích hợp sẵn trong dự án).

---

## 🛠️ Hướng dẫn Triển khai Chi tiết

### Bước 1: Thiết lập Cơ sở dữ liệu (MySQL)
1. Khởi động ứng dụng **XAMPP Control Panel** (hoặc Laragon) và nhấn **Start** cho dịch vụ **MySQL**.
2. Truy cập vào giao diện quản lý cơ sở dữ liệu (ví dụ: `http://localhost/phpmyadmin` trên trình duyệt).
3. Tạo một cơ sở dữ liệu mới có tên chính xác là:
   ```sql
   CREATE DATABASE doan_db;
   ```
4. **Nhập dữ liệu mẫu (Tùy chọn)**: Nếu bạn có sẵn file sao lưu `.sql` từ máy cũ, hãy chọn cơ sở dữ liệu `doan_db` và nhấn **Import (Nhập)** file này. Nếu không, Spring Boot JPA Hibernate sẽ tự động sinh các bảng trống trong lần chạy đầu tiên.

---

### Bước 2: Cấu hình & Chạy Backend (Spring Boot)
1. Di chuyển vào thư mục mã nguồn `backend`.
2. Mở file cấu hình kết nối database tại đường dẫn:
   `backend/src/main/resources/application.properties`
3. Điều chỉnh các thông số kết nối MySQL phù hợp với máy tính mới của bạn:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/doan_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.username=root
   spring.datasource.password=  # Điền mật khẩu MySQL nếu có, mặc định của XAMPP là để trống
   ```
4. Khởi chạy server Backend:
   * Mở Terminal tại thư mục `backend`.
   * Thực hiện lệnh sau để chạy:
     * **Windows**: `gradlew.bat bootRun`
     * **macOS/Linux**: `./gradlew bootRun`
   * Chờ hệ thống khởi chạy đến khi xuất hiện thông báo:  
     `Started BackendApplication in X.XXX seconds (Tomcat started on port 8080)`

---

### Bước 3: Cấu hình & Chạy Mobile Client (Android App)
1. Mở phần mềm **Android Studio**.
2. Chọn **Open** và dẫn tới thư mục dự án `mobile`.
3. Cấu hình địa chỉ IP kết nối của API trong file cấu hình:
   `mobile/app/src/main/java/com/example/mobile/config/ApiConfig.java`
4. Điều chỉnh hằng số `BASE_URL` tùy thuộc vào thiết bị chạy thử của bạn:
   * **Nếu chạy trên Máy ảo Android (Emulator)** trên cùng máy tính chạy server:
     ```java
     public static final String BASE_URL = "http://10.0.2.2:8080";
     ```
   * **Nếu chạy trên Thiết bị thật (Physical Phone/Tablet)**:
     * Máy tính chạy backend và thiết bị Android phải kết nối **chung một mạng Wi-Fi**.
     * Tìm địa chỉ IP nội bộ của máy tính (Mở Command Prompt chạy `ipconfig`, tìm `IPv4 Address`, ví dụ: `192.168.1.15`).
     * Cập nhật địa chỉ trong code:
       ```java
       public static final String BASE_URL = "http://192.168.1.15:8080";
       ```
5. Nhấn nút **Run** (biểu tượng mũi tên xanh lục ở thanh công cụ phía trên) trong Android Studio để xây dựng ứng dụng và cài đặt trực tiếp lên điện thoại hoặc máy ảo của bạn.

---

## 🌟 Các Tính năng Nổi bật
* **Dashboard Tổng quan trực quan**: Doanh thu hôm nay (Today's Revenue), Số lượt đặt sân và Sân đang sử dụng cập nhật thời gian thực.
* **Truy cập nhanh (Quick Access)**: Phím tắt điều hướng nhanh gọn đến các tác vụ trọng tâm trên màn hình Dashboard chính.
* **Đặt sân & Xem lịch trình lưới**: Grid timeline trực quan xem danh sách trạng thái các sân, cho phép đặt sân, hủy đặt sân hoặc thanh toán hóa đơn.
* **Thống kê Báo cáo Chuyên sâu**: Biểu đồ doanh thu tuần, tỉ lệ đặt sân thành công và phân tích cơ cấu khách hàng (Khách quay lại vs Khách mới) tự động tính toán từ cơ sở dữ liệu dựa trên ngày thực tế của thiết bị.
* **Quản lý Bảng giá**: Hỗ trợ thiết lập các khung giờ cao điểm, giờ thường linh hoạt theo nhu cầu kinh doanh.
