# PicklePro Management System - Hướng Hướng dẫn Cài đặt & Triển khai

Tài liệu này hướng dẫn chi tiết cách cài đặt, cấu hình và khởi chạy dự án **PicklePro Manager** khi chuyển sang một máy tính mới. Hệ thống bao gồm hai thành phần chính:
1. **Backend**: Spring Boot REST API kết nối cơ sở dữ liệu MySQL.
2. **Mobile Client**: Ứng dụng Android (Java) quản lý đặt sân Pickleball, hóa đơn, doanh thu và bảng giá.

---

## 📋 Yêu cầu hệ thống (Prerequisites)

Hãy đảm bảo máy tính mới của bạn đã cài đặt đầy đủ các công cụ sau:
* **Java JDK**: Phiên bản 17 hoặc 21 (Khuyên dùng OpenJDK 17).
* **Android Studio**: Phiên bản Koala trở lên để biên dịch và chạy ứng dụng di động.
* **Cơ sở dữ liệu**: MySQL (Khuyên dùng **XAMPP** phiên bản mới nhất chứa MySQL/MariaDB).
* **Git**: Để quản lý mã nguồn (tùy chọn).

---

## 🛠️ Quy trình triển khai từng bước (Step-by-Step Deployment)

### Bước 1: Sao chép mã nguồn & Database
1. Sao chép toàn bộ thư mục dự án `doan` sang máy tính mới.
2. Cấu trúc thư mục chuẩn của dự án:
   * `/backend`: Mã nguồn Spring Boot Backend.
   * `/mobile`: Mã nguồn ứng dụng Android Client.
   * `/database`: Thư mục chứa file backup cơ sở dữ liệu (`doan_db.sql`).

---

### Bước 2: Thiết lập Cơ sở dữ liệu (MySQL)
1. Khởi động **XAMPP Control Panel** trên máy tính mới và nhấn **Start** tại dịch vụ **MySQL** (và **Apache** nếu muốn dùng phpMyAdmin quản lý trực quan).
2. Truy cập giao diện quản lý cơ sở dữ liệu phpMyAdmin qua đường dẫn: `http://localhost/phpmyadmin`.
3. Tạo một Database mới:
   * Nhấp vào **New (Mới)** ở danh sách bên trái.
   * Nhập tên cơ sở dữ liệu chính xác là: `doan_db`.
   * Chọn bảng mã (Collation): `utf8mb4_unicode_ci` (hoặc `utf8mb4_general_ci`).
   * Nhấn nút **Create (Tạo)**.
4. Nhập dữ liệu từ file sao lưu:
   * Click chọn cơ sở dữ liệu `doan_db` vừa tạo ở cột bên trái.
   * Nhấp vào tab **Import (Nhập)** ở thanh công cụ phía trên.
   * Tại mục **File to import (Tệp để nhập)**, nhấn **Browse... (Chọn tệp)** và chọn đường dẫn đến file:
     `database/doan_db.sql` (hoặc có thể dùng `backend/db_dump.sql` hoặc `backend/schema.sql`).
   * Cuộn xuống dưới cùng và nhấn nút **Import (Nhập)**.
   * Đợi hệ thống xử lý đến khi có thông báo import thành công. Tất cả các bảng (`tbl_san`, `tbl_banggia`, `tbl_hoadon`, `tbl_phieudatsan`, `tbl_taikhoan`, v.v.) và dữ liệu mẫu sẽ được khởi tạo đầy đủ.

> [!TIP]
> **Nhập database nhanh hơn bằng dòng lệnh (Command Line)**:
> Nếu file SQL lớn và giao diện web bị lỗi quá thời gian tải (timeout), bạn hãy mở Terminal/Command Prompt và chạy lệnh:
> ```bash
> C:\xampp\mysql\bin\mysql.exe -u root -p doan_db < "đường_dẫn_thư_mục_dự_án/database/doan_db.sql"
> ```

---

### Bước 3: Cấu hình và Khởi chạy Backend (Spring Boot)
1. Sử dụng IntelliJ IDEA, Android Studio hoặc VS Code để mở thư mục `backend`.
2. Mở file cấu hình kết nối cơ sở dữ liệu tại đường dẫn:
   [application.properties](file:///c:/Users/Admin/AndroidStudioProjects/doan/backend/src/main/resources/application.properties)
3. Điều chỉnh các cấu hình kết nối MySQL phù hợp với máy tính mới của bạn:
   ```properties
   spring.application.name=backend
   
   # Cấu hình kết nối MySQL
   spring.datasource.url=jdbc:mysql://localhost:3306/doan_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.username=root
   spring.datasource.password=  # Điền mật khẩu MySQL của máy mới nếu có (mặc định XAMPP để trống)
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

   # Cấu hình JPA / Hibernate
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.format_sql=true
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
   ```
4. **Khởi chạy server Backend**:
   * **Cách 1: Chạy trực tiếp từ dòng lệnh (Terminal)**
     * Mở Terminal tại thư mục `backend`.
     * Thực hiện lệnh khởi chạy:
       * **Windows (PowerShell/CMD)**: `.\gradlew.bat bootRun`
       * **macOS/Linux**: `./gradlew bootRun`
   * **Cách 2: Chạy thông qua IDE (IntelliJ IDEA / Android Studio)**
     * Sau khi mở thư mục `backend`, chờ IDE đồng bộ Gradle hoàn tất.
     * Tìm đến file `BackendApplication.java` tại `backend/src/main/java/com/example/backend/BackendApplication.java`.
     * Nhấp chuột phải chọn **Run 'BackendApplication'** hoặc nhấn nút Run ở góc trên.
5. **Xác nhận Backend chạy thành công**:
   * Kiểm tra ở cửa sổ log console, nếu xuất hiện thông báo:
     `Started BackendApplication in X.XXX seconds (Tomcat started on port 8080)`
   * Mở trình duyệt web truy cập địa chỉ: `http://localhost:8080/api/san`. Nếu kết quả trả về chuỗi JSON chứa danh sách các sân bóng từ database, backend đã hoạt động hoàn toàn bình thường.

---

### Bước 4: Cấu hình và Khởi chạy Ứng dụng Di động (Android Client)
1. Khởi động **Android Studio**.
2. Chọn **Open** và trỏ đến thư mục `mobile` trong thư mục dự án của bạn.
3. Chờ Android Studio thực hiện quá trình đồng bộ Gradle (Gradle Sync) để tải các thư viện cần thiết.
4. **Cấu hình địa chỉ IP máy chủ API**:
   * Để ứng dụng Android có thể giao tiếp được với server Backend, bạn cần chỉnh sửa địa chỉ IP trong file cấu hình.
   * Mở file: [ApiConfig.java](file:///c:/Users/Admin/AndroidStudioProjects/doan/mobile/app/src/main/java/com/example/mobile/config/ApiConfig.java)
   * Chỉnh sửa hằng số `BASE_URL` cho phù hợp với cách chạy thử nghiệm của bạn:
     * **Trường hợp 1: Chạy ứng dụng trên Máy ảo Android (Android Emulator)** trên cùng máy tính với backend:
       ```java
       public static final String BASE_URL = "http://10.0.2.2:8080";
       ```
       *(IP `10.0.2.2` là IP đặc biệt của máy ảo Android để truy cập localhost của máy tính host).*
     * **Trường hợp 2: Chạy ứng dụng trên Thiết bị thật (Điện thoại/Máy tính bảng kết nối qua cáp USB)**:
       * Máy tính chạy backend và thiết bị Android phải kết nối **chung một mạng Wi-Fi**.
       * Tìm địa chỉ IP mạng nội bộ của máy tính của bạn:
         * Trên **Windows**: Mở Command Prompt chạy lệnh `ipconfig`, tìm dòng `IPv4 Address` (ví dụ: `192.168.1.15`).
         * Trên **macOS/Linux**: Mở Terminal chạy lệnh `ifconfig` hoặc `ip a`.
       * Cập nhật địa chỉ IP vừa tìm được vào code:
         ```java
         public static final String BASE_URL = "http://192.168.1.15:8080";
         ```
5. **Cài đặt & Khởi chạy**:
   * Kết nối thiết bị thật (đã bật tùy chọn nhà phát triển - USB Debugging) hoặc mở một máy ảo Android.
   * Chọn thiết bị ở thanh công cụ phía trên và nhấn nút **Run** (biểu tượng tam giác xanh lá `Run 'app'`). Chờ Gradle build xong và tự động cài đặt ứng dụng lên thiết bị của bạn.

---

## 🔍 Hướng dẫn Kiểm tra & Xác minh sau cài đặt

| Thành phần | Cách thức kiểm tra | Kết quả mong đợi |
| :--- | :--- | :--- |
| **MySQL Database** | Truy cập `http://localhost/phpmyadmin` | Nhìn thấy database `doan_db` với đầy đủ dữ liệu trong các bảng. |
| **Spring Boot API** | Truy cập đường dẫn `http://localhost:8080/api/san` trên trình duyệt | Trả về dữ liệu danh sách sân dưới định dạng JSON. |
| **Ứng dụng di động** | Khởi chạy app, thực hiện đăng nhập và xem sân | Đăng nhập thành công, dữ liệu sân được hiển thị đầy đủ trên màn hình Dashboard và Timeline đặt sân. |

---

## ⚡ Các lỗi thường gặp & Cách khắc phục (Troubleshooting)

### 1. Lỗi cổng kết nối bị chiếm dụng (Port 8080 already in use)
* **Triệu chứng**: Khi chạy backend báo lỗi `Web server failed to start. Port 8080 was already in use.`
* **Cách khắc phục**:
  * Tắt tiến trình đang sử dụng cổng 8080 trên máy mới.
  * Hoặc đổi cổng chạy của Spring Boot sang cổng khác (ví dụ: `8081`) bằng cách thêm dòng sau vào [application.properties](file:///c:/Users/Admin/AndroidStudioProjects/doan/backend/src/main/resources/application.properties):
    ```properties
    server.port=8081
    ```
  * Khi đổi cổng backend, hãy lưu ý cập nhật lại cổng tương ứng ở hằng số `BASE_URL` trong file [ApiConfig.java](file:///c:/Users/Admin/AndroidStudioProjects/doan/mobile/app/src/main/java/com/example/mobile/config/ApiConfig.java) của ứng dụng Android (ví dụ: `"http://10.0.2.2:8081"`).

### 2. Thiết bị di động không kết nối được đến server (Connection Timed Out)
* **Triệu chứng**: Ứng dụng Android chạy trên máy thật báo lỗi kết nối hoặc không load được dữ liệu sân từ backend dù đã chỉnh đúng IP máy tính.
* **Cách khắc phục**:
  * Kiểm tra xem cả máy tính và điện thoại đã kết nối **cùng một mạng Wi-Fi** hay chưa.
  * Do **Tường lửa (Firewall)** trên Windows chặn kết nối từ thiết bị ngoại vi vào cổng 8080. Bạn có thể tạm thời tắt Windows Defender Firewall để kiểm tra hoặc tạo một rule Inbound cho phép cổng 8080.

### 3. Lỗi Driver MySQL hoặc lệch múi giờ (Timezone error)
* **Triệu chứng**: Spring Boot không khởi động được và báo lỗi liên quan đến `serverTimezone`.
* **Cách khắc phục**: Hãy giữ nguyên các tham số kết nối cơ bản phía sau URL cơ sở dữ liệu như cấu hình mẫu để tự động đồng bộ múi giờ với UTC và cho phép xác thực khóa công khai:
  ```properties
  spring.datasource.url=jdbc:mysql://localhost:3306/doan_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
  ```
