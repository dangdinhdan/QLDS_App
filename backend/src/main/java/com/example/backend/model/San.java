package com.example.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_san")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class San {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_san", nullable = false, unique = true, length = 50)
    private String maSan;

    @Column(nullable = false, length = 100)
    private String ten;

    @Column(length = 50)
    private String loaimatsan;

    @Column(length = 50)
    private String trangthai;

    @Column(name = "create_at", insertable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;
}
