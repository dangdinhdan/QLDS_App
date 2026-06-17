package com.example.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tbl_phieudatsan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Phieudatsan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_khachhang", nullable = false)
    private Khachhang khachhang;

    @Column(name = "ngaydat", nullable = false)
    private LocalDate ngaydat;

    @Column(length = 50)
    private String trangthai;

    @Column(length = 255)
    private String ghichu;

    @Column(name = "create_at", insertable = false, updatable = false)
    private LocalDateTime createAt;

    @OneToMany(mappedBy = "phieudatsan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chitietdatsan> details;
}
