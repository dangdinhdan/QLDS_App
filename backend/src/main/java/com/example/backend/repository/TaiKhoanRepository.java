package com.example.backend.repository;

import com.example.backend.model.TaiKhoan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class TaiKhoanRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<TaiKhoan> rowMapper = (rs, rowNum) -> TaiKhoan.builder()
            .id(rs.getInt("id"))
            .taikhoan(rs.getString("taikhoan"))
            .matkhau(rs.getString("matkhau"))
            .createAt(rs.getTimestamp("create_at") != null ? rs.getTimestamp("create_at").toLocalDateTime() : null)
            .build();

    public Optional<TaiKhoan> findByTaikhoan(String taikhoan) {
        String sql = "SELECT * FROM tbl_taikhoan WHERE taikhoan = ?";
        try {
            TaiKhoan tk = jdbcTemplate.queryForObject(sql, rowMapper, taikhoan);
            return Optional.ofNullable(tk);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<TaiKhoan> findById(Integer id) {
        String sql = "SELECT * FROM tbl_taikhoan WHERE id = ?";
        try {
            TaiKhoan tk = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(tk);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
