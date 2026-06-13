package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "tbl_banggiachitiet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banggiachitiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_banggia", nullable = false)
    @JsonBackReference
    private Banggia banggia;

    @Column(name = "loaingay", nullable = false, length = 50)
    private String loaingay;

    @Column(name = "giobatdau", nullable = false)
    private LocalTime giobatdau;

    @Column(name = "giokethuc", nullable = false)
    private LocalTime giokethuc;

    @Column(name = "dongia", nullable = false)
    private Double dongia;

    @Column(name = "create_at", insertable = false, updatable = false)
    private LocalDateTime createAt;
}
