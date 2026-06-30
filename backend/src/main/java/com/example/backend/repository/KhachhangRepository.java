package com.example.backend.repository;

import com.example.backend.model.Khachhang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

@Repository
public class KhachhangRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Khachhang> rowMapper = (rs, rowNum) -> Khachhang.builder()
            .id(rs.getInt("id"))
            .maKh(rs.getString("ma_kh"))
            .ten(rs.getString("ten"))
            .sdt(rs.getString("sdt"))
            .createAt(rs.getTimestamp("create_at") != null ? rs.getTimestamp("create_at").toLocalDateTime() : null)
            .updateAt(rs.getTimestamp("update_at") != null ? rs.getTimestamp("update_at").toLocalDateTime() : null)
            .build();

    public Optional<Khachhang> findFirstBySdt(String sdt) {
        String sql = "SELECT * FROM tbl_khachhang WHERE sdt = ? LIMIT 1";
        try {
            Khachhang kh = jdbcTemplate.queryForObject(sql, rowMapper, sdt);
            return Optional.ofNullable(kh);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Khachhang> findFirstByTen(String ten) {
        String sql = "SELECT * FROM tbl_khachhang WHERE ten = ? LIMIT 1";
        try {
            Khachhang kh = jdbcTemplate.queryForObject(sql, rowMapper, ten);
            return Optional.ofNullable(kh);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Khachhang> findById(Integer id) {
        String sql = "SELECT * FROM tbl_khachhang WHERE id = ?";
        try {
            Khachhang kh = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(kh);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Khachhang save(Khachhang kh) {
        if (kh.getId() == null) {
            String sql = "INSERT INTO tbl_khachhang (ma_kh, ten, sdt) VALUES (?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, kh.getMaKh());
                ps.setString(2, kh.getTen());
                ps.setString(3, kh.getSdt());
                return ps;
            }, keyHolder);
            if (keyHolder.getKey() != null) {
                kh.setId(keyHolder.getKey().intValue());
            }
        } else {
            String sql = "UPDATE tbl_khachhang SET ma_kh = ?, ten = ?, sdt = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    kh.getMaKh(),
                    kh.getTen(),
                    kh.getSdt(),
                    kh.getId());
        }
        return kh;
    }
}
