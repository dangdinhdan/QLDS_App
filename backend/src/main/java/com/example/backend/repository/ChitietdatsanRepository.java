package com.example.backend.repository;

import com.example.backend.model.Chitietdatsan;
import com.example.backend.model.Phieudatsan;
import com.example.backend.model.San;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Time;
import java.util.List;
import java.util.Optional;

@Repository
public class ChitietdatsanRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PhieudatsanRepository phieudatsanRepository;

    @Autowired
    private SanRepository sanRepository;

    private final RowMapper<Chitietdatsan> rowMapper = (rs, rowNum) -> {
        Chitietdatsan detail = Chitietdatsan.builder()
                .id(rs.getInt("id"))
                .giobatdau(rs.getTime("giobatdau") != null ? rs.getTime("giobatdau").toLocalTime() : null)
                .giokethuc(rs.getTime("giokethuc") != null ? rs.getTime("giokethuc").toLocalTime() : null)
                .dongia(rs.getDouble("dongia"))
                .thanhtien(rs.getDouble("thanhtien"))
                .build();
        
        int phieuId = rs.getInt("id_phieudatsan");
        detail.setPhieudatsan(phieudatsanRepository.findById(phieuId).orElse(null));

        int sanId = rs.getInt("id_san");
        detail.setSan(sanRepository.findById(sanId).orElse(null));

        return detail;
    };

    public List<Chitietdatsan> findAll() {
        String sql = "SELECT * FROM tbl_chitietdatsan";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Optional<Chitietdatsan> findById(Integer id) {
        String sql = "SELECT * FROM tbl_chitietdatsan WHERE id = ?";
        try {
            Chitietdatsan detail = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(detail);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Chitietdatsan> findByPhieudatsanId(Integer phieudatsanId) {
        String sql = "SELECT * FROM tbl_chitietdatsan WHERE id_phieudatsan = ?";
        return jdbcTemplate.query(sql, rowMapper, phieudatsanId);
    }

    public Chitietdatsan save(Chitietdatsan detail) {
        if (detail.getId() == null) {
            String sql = "INSERT INTO tbl_chitietdatsan (id_phieudatsan, id_san, giobatdau, giokethuc, dongia, thanhtien) VALUES (?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, detail.getPhieudatsan().getId());
                ps.setInt(2, detail.getSan().getId());
                ps.setTime(3, detail.getGiobatdau() != null ? Time.valueOf(detail.getGiobatdau()) : null);
                ps.setTime(4, detail.getGiokethuc() != null ? Time.valueOf(detail.getGiokethuc()) : null);
                ps.setDouble(5, detail.getDongia());
                ps.setDouble(6, detail.getThanhtien());
                return ps;
            }, keyHolder);
            if (keyHolder.getKey() != null) {
                detail.setId(keyHolder.getKey().intValue());
            }
        } else {
            String sql = "UPDATE tbl_chitietdatsan SET id_phieudatsan = ?, id_san = ?, giobatdau = ?, giokethuc = ?, dongia = ?, thanhtien = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    detail.getPhieudatsan().getId(),
                    detail.getSan().getId(),
                    detail.getGiobatdau() != null ? Time.valueOf(detail.getGiobatdau()) : null,
                    detail.getGiokethuc() != null ? Time.valueOf(detail.getGiokethuc()) : null,
                    detail.getDongia(),
                    detail.getThanhtien(),
                    detail.getId());
        }
        return detail;
    }

    public void delete(Chitietdatsan detail) {
        if (detail != null && detail.getId() != null) {
            String sql = "DELETE FROM tbl_chitietdatsan WHERE id = ?";
            jdbcTemplate.update(sql, detail.getId());
        }
    }
}
