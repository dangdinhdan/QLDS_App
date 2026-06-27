package com.example.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_hoadon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hoadon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_hoadon", nullable = false, unique = true, length = 50)
    private String maHoadon;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_phieudatsan", nullable = false)
    private Phieudatsan phieudatsan;

    @Column(length = 255)
    private String ghichu;

    @Column(name = "tongtien", nullable = false)
    private Double tongtien;
}
