package com.example.backend.repository;

import com.example.backend.model.Khachhang;
import com.example.backend.model.Phieudatsan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

@Repository
public class PhieudatsanRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private KhachhangRepository khachhangRepository;

    private final RowMapper<Phieudatsan> rowMapper = (rs, rowNum) -> {
        Phieudatsan phieu = Phieudatsan.builder()
                .id(rs.getInt("id"))
                .ngaydat(rs.getDate("ngaydat") != null ? rs.getDate("ngaydat").toLocalDate() : null)
                .trangthai(rs.getString("trangthai"))
                .ghichu(rs.getString("ghichu"))
                .createAt(rs.getTimestamp("create_at") != null ? rs.getTimestamp("create_at").toLocalDateTime() : null)
                .build();
        
        int khId = rs.getInt("id_khachhang");
        phieu.setKhachhang(khachhangRepository.findById(khId).orElse(null));
        return phieu;
    };

    public Optional<Phieudatsan> findById(Integer id) {
        String sql = "SELECT * FROM tbl_phieudatsan WHERE id = ?";
        try {
            Phieudatsan phieu = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(phieu);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Phieudatsan save(Phieudatsan phieu) {
        if (phieu.getId() == null) {
            String sql = "INSERT INTO tbl_phieudatsan (id_khachhang, ngaydat, trangthai, ghichu) VALUES (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, phieu.getKhachhang().getId());
                ps.setDate(2, phieu.getNgaydat() != null ? Date.valueOf(phieu.getNgaydat()) : null);
                ps.setString(3, phieu.getTrangthai());
                ps.setString(4, phieu.getGhichu());
                return ps;
            }, keyHolder);
            if (keyHolder.getKey() != null) {
                phieu.setId(keyHolder.getKey().intValue());
            }
        } else {
            String sql = "UPDATE tbl_phieudatsan SET id_khachhang = ?, ngaydat = ?, trangthai = ?, ghichu = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    phieu.getKhachhang().getId(),
                    phieu.getNgaydat() != null ? Date.valueOf(phieu.getNgaydat()) : null,
                    phieu.getTrangthai(),
                    phieu.getGhichu(),
                    phieu.getId());
        }
        return phieu;
    }

    public void delete(Phieudatsan phieu) {
        if (phieu != null && phieu.getId() != null) {
            String sql = "DELETE FROM tbl_phieudatsan WHERE id = ?";
            jdbcTemplate.update(sql, phieu.getId());
        }
    }
}
