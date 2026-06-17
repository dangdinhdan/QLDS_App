package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.BookingDTO;
import com.example.backend.model.Chitietdatsan;
import com.example.backend.model.Khachhang;
import com.example.backend.model.Phieudatsan;
import com.example.backend.model.San;
import com.example.backend.repository.ChitietdatsanRepository;
import com.example.backend.repository.KhachhangRepository;
import com.example.backend.repository.PhieudatsanRepository;
import com.example.backend.repository.SanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private PhieudatsanRepository phieudatsanRepository;

    @Autowired
    private ChitietdatsanRepository chitietdatsanRepository;

    @Autowired
    private KhachhangRepository khachhangRepository;

    @Autowired
    private SanRepository sanRepository;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingDTO>>> getAllBookings() {
        try {
            List<Chitietdatsan> detailsList = chitietdatsanRepository.findAll();
            List<BookingDTO> dtoList = new ArrayList<>();

            for (Chitietdatsan detail : detailsList) {
                Phieudatsan phieu = detail.getPhieudatsan();
                if (phieu == null) continue;

                Khachhang kh = phieu.getKhachhang();
                String playerName = kh != null ? kh.getTen() : "Không tên";
                String phoneNumber = kh != null ? kh.getSdt() : "";

                BookingDTO dto = BookingDTO.builder()
                        .id(detail.getId())
                        .courtId(detail.getSan().getId())
                        .playerName(playerName)
                        .phoneNumber(phoneNumber)
                        .date(phieu.getNgaydat().toString())
                        .startTime(detail.getGiobatdau().format(TIME_FORMATTER))
                        .endTime(detail.getGiokethuc().format(TIME_FORMATTER))
                        .fee(detail.getThanhtien())
                        .status(phieu.getTrangthai())
                        .build();

                dtoList.add(dto);
            }

            return ResponseEntity.ok(ApiResponse.<List<BookingDTO>>builder()
                    .success(true)
                    .message("Lấy danh sách đặt sân thành công!")
                    .data(dtoList)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.<List<BookingDTO>>builder()
                    .success(false)
                    .message("Lỗi khi lấy danh sách đặt sân: " + e.getMessage())
                    .build());
        }
    }

    @GetMapping("/customer")
    public ResponseEntity<ApiResponse<Khachhang>> getCustomerByPhone(@RequestParam String phone) {
        try {
            Optional<Khachhang> optKh = khachhangRepository.findFirstBySdt(phone.trim());
            if (optKh.isPresent()) {
                return ResponseEntity.ok(ApiResponse.<Khachhang>builder()
                        .success(true)
                        .message("Tìm thấy khách hàng!")
                        .data(optKh.get())
                        .build());
            } else {
                return ResponseEntity.ok(ApiResponse.<Khachhang>builder()
                        .success(false)
                        .message("Không tìm thấy khách hàng với số điện thoại này.")
                        .build());
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.<Khachhang>builder()
                    .success(false)
                    .message("Lỗi: " + e.getMessage())
                    .build());
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingDTO>> createBooking(@RequestBody BookingDTO dto) {
        try {
            if (dto.getPlayerName() == null || dto.getPlayerName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.<BookingDTO>builder()
                        .success(false)
                        .message("Tên khách hàng không được để trống!")
                        .build());
            }

            if (dto.getPhoneNumber() == null || dto.getPhoneNumber().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.<BookingDTO>builder()
                        .success(false)
                        .message("Số điện thoại không được để trống!")
                        .build());
            }

            // Find or create customer by phone number
            Khachhang kh = null;
            String phone = dto.getPhoneNumber() != null ? dto.getPhoneNumber().trim() : "";
            if (!phone.isEmpty()) {
                Optional<Khachhang> optKh = khachhangRepository.findFirstBySdt(phone);
                if (optKh.isPresent()) {
                    kh = optKh.get();
                }
            }

            if (kh == null) {
                // Generate a unique KH code
                String maKh = "KH" + String.format("%03d", new Random().nextInt(900) + 100);
                kh = Khachhang.builder()
                        .maKh(maKh)
                        .ten(dto.getPlayerName().trim())
                        .sdt(phone)
                        .build();
                kh = khachhangRepository.save(kh);
            }

            // Find Court (San)
            Optional<San> optSan = sanRepository.findById(dto.getCourtId());
            if (!optSan.isPresent()) {
                return ResponseEntity.badRequest().body(ApiResponse.<BookingDTO>builder()
                        .success(false)
                        .message("Không tìm thấy sân với ID: " + dto.getCourtId())
                        .build());
            }
            San san = optSan.get();

            // Create booking ticket (Phieudatsan)
            LocalDate bookingDate = LocalDate.parse(dto.getDate());
            Phieudatsan phieu = Phieudatsan.builder()
                    .khachhang(kh)
                    .ngaydat(bookingDate)
                    .trangthai(dto.getStatus() != null ? dto.getStatus() : "Đã đặt")
                    .ghichu("Đặt sân từ ứng dụng di động")
                    .build();
            phieu = phieudatsanRepository.save(phieu);

            // Parse times
            LocalTime start = LocalTime.parse(dto.getStartTime(), TIME_FORMATTER);
            LocalTime end = LocalTime.parse(dto.getEndTime(), TIME_FORMATTER);

            // Create booking detail (Chitietdatsan)
            Chitietdatsan detail = Chitietdatsan.builder()
                    .phieudatsan(phieu)
                    .san(san)
                    .giobatdau(start)
                    .giokethuc(end)
                    .dongia(san.getIdBanggia() != null ? 150000.0 : 150000.0) // Mock base price or fetch from table
                    .thanhtien(dto.getFee())
                    .build();
            detail = chitietdatsanRepository.save(detail);

            // Update Court Status to BOOKED
            if ("Trong".equals(san.getTrangthai()) || "EMPTY".equals(san.getTrangthai())) {
                san.setTrangthai("Đã đặt");
                sanRepository.save(san);
            }

            dto.setId(detail.getId());
            return ResponseEntity.ok(ApiResponse.<BookingDTO>builder()
                    .success(true)
                    .message("Đặt sân thành công!")
                    .data(dto)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.<BookingDTO>builder()
                    .success(false)
                    .message("Lỗi khi thêm lịch đặt sân: " + e.getMessage())
                    .build());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBooking(@PathVariable Integer id) {
        try {
            Optional<Chitietdatsan> optDetail = chitietdatsanRepository.findById(id);
            if (!optDetail.isPresent()) {
                return ResponseEntity.status(404).body(ApiResponse.<Void>builder()
                        .success(false)
                        .message("Không tìm thấy chi tiết lịch đặt sân với ID: " + id)
                        .build());
            }

            Chitietdatsan detail = optDetail.get();
            Phieudatsan phieu = detail.getPhieudatsan();
            San san = detail.getSan();

            // Delete the detail slot
            chitietdatsanRepository.delete(detail);

            // Delete empty parent booking tickets
            if (phieu != null) {
                // If it has no other details left, delete the ticket
                List<Chitietdatsan> otherDetails = phieu.getDetails();
                if (otherDetails == null || otherDetails.size() <= 1) {
                    phieudatsanRepository.delete(phieu);
                }
            }

            // Check if court has any other active bookings, if not, update status to EMPTY (Trong)
            boolean hasOther = false;
            List<Chitietdatsan> allRemainingDetails = chitietdatsanRepository.findAll();
            for (Chitietdatsan remaining : allRemainingDetails) {
                if (remaining.getSan().getId().equals(san.getId())) {
                    hasOther = true;
                    break;
                }
            }

            if (!hasOther && ("Đã đặt".equals(san.getTrangthai()) || "BOOKED".equals(san.getTrangthai()))) {
                san.setTrangthai("Trong");
                sanRepository.save(san);
            }

            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Xóa lịch đặt sân thành công!")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Lỗi khi xóa lịch đặt sân: " + e.getMessage())
                    .build());
        }
    }
}
