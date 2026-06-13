package com.example.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_banggia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banggia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_banggia", nullable = false, unique = true, length = 50)
    private String maBanggia;

    @Column(nullable = false, length = 100)
    private String tenbanggia;

    @Column(length = 255)
    private String mota;

    @OneToMany(mappedBy = "banggia", cascade = CascadeType.ALL, orphanRemoval = true)
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private java.util.List<Banggiachitiet> details;

    @Column(name = "create_at", insertable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(name = "update_at", insertable = false, updatable = false)
    private LocalDateTime updateAt;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;

    @Column(name = "isdelete")
    @Builder.Default
    private Boolean isdelete = false;
}
