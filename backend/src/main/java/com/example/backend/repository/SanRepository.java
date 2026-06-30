package com.example.backend.repository;

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
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class SanRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<San> rowMapper = (rs, rowNum) -> San.builder()
            .id(rs.getInt("id"))
            .maSan(rs.getString("ma_san"))
            .idBanggia(rs.getObject("id_banggia") != null ? rs.getInt("id_banggia") : null)
            .ten(rs.getString("ten"))
            .loaimatsan(rs.getString("loaimatsan"))
            .trangthai(rs.getString("trangthai"))
            .url(rs.getString("url"))
            .createAt(rs.getTimestamp("create_at") != null ? rs.getTimestamp("create_at").toLocalDateTime() : null)
            .deleteAt(rs.getTimestamp("delete_at") != null ? rs.getTimestamp("delete_at").toLocalDateTime() : null)
            .isdelete(rs.getObject("isdelete") != null ? rs.getBoolean("isdelete") : false)
            .build();

    public Optional<San> findByMaSan(String maSan) {
        String sql = "SELECT * FROM tbl_san WHERE ma_san = ?";
        try {
            San san = jdbcTemplate.queryForObject(sql, rowMapper, maSan);
            return Optional.ofNullable(san);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<San> findByIsdeleteFalseOrIsdeleteIsNull() {
        String sql = "SELECT * FROM tbl_san WHERE isdelete = false OR isdelete IS NULL";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Optional<San> findById(Integer id) {
        String sql = "SELECT * FROM tbl_san WHERE id = ?";
        try {
            San san = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(san);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public San save(San san) {
        if (san.getId() == null) {
            String sql = "INSERT INTO tbl_san (ma_san, id_banggia, ten, loaimatsan, trangthai, url, isdelete, delete_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, san.getMaSan());
                if (san.getIdBanggia() != null) {
                    ps.setInt(2, san.getIdBanggia());
                } else {
                    ps.setNull(2, java.sql.Types.INTEGER);
                }
                ps.setString(3, san.getTen());
                ps.setString(4, san.getLoaimatsan());
                ps.setString(5, san.getTrangthai());
                ps.setString(6, san.getUrl());
                ps.setBoolean(7, san.getIsdelete() != null ? san.getIsdelete() : false);
                if (san.getDeleteAt() != null) {
                    ps.setTimestamp(8, Timestamp.valueOf(san.getDeleteAt()));
                } else {
                    ps.setNull(8, java.sql.Types.TIMESTAMP);
                }
                return ps;
            }, keyHolder);
            if (keyHolder.getKey() != null) {
                san.setId(keyHolder.getKey().intValue());
            }
        } else {
            String sql = "UPDATE tbl_san SET ma_san = ?, id_banggia = ?, ten = ?, loaimatsan = ?, trangthai = ?, url = ?, isdelete = ?, delete_at = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    san.getMaSan(),
                    san.getIdBanggia(),
                    san.getTen(),
                    san.getLoaimatsan(),
                    san.getTrangthai(),
                    san.getUrl(),
                    san.getIsdelete() != null ? san.getIsdelete() : false,
                    san.getDeleteAt() != null ? Timestamp.valueOf(san.getDeleteAt()) : null,
                    san.getId());
        }
        return san;
    }
}
