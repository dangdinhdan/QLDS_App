package com.example.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_khachhang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Khachhang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_kh", nullable = false, unique = true, length = 50)
    private String maKh;

    @Column(nullable = false, length = 100)
    private String ten;

    @Column(length = 20, unique = true, nullable = false)
    private String sdt;

    @Column(name = "create_at", insertable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at", insertable = false, updatable = false)
    private LocalDateTime updateAt;
}
