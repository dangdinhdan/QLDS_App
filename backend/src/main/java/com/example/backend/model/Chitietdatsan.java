package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@Table(name = "tbl_chitietdatsan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chitietdatsan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieudatsan", nullable = false)
    @JsonIgnore
    private Phieudatsan phieudatsan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_san", nullable = false)
    private San san;

    @Column(name = "giobatdau", nullable = false)
    private LocalTime giobatdau;

    @Column(name = "giokethuc", nullable = false)
    private LocalTime giokethuc;

    @Column(name = "dongia", nullable = false)
    private Double dongia;

    @Column(name = "thanhtien", nullable = false)
    private Double thanhtien;
}
