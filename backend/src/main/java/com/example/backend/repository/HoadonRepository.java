package com.example.backend.repository;

import com.example.backend.model.Hoadon;
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
public class HoadonRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PhieudatsanRepository phieudatsanRepository;

    private final RowMapper<Hoadon> rowMapper = (rs, rowNum) -> {
        Hoadon hd = Hoadon.builder()
                .id(rs.getInt("id"))
                .maHoadon(rs.getString("ma_hoadon"))
                .ghichu(rs.getString("ghichu"))
                .tongtien(rs.getDouble("tongtien"))
                .build();
        
        int phieuId = rs.getInt("id_phieudatsan");
        hd.setPhieudatsan(phieudatsanRepository.findById(phieuId).orElse(null));
        return hd;
    };

    public Optional<Hoadon> findByPhieudatsanId(Integer phieuId) {
        String sql = "SELECT * FROM tbl_hoadon WHERE id_phieudatsan = ?";
        try {
            Hoadon hd = jdbcTemplate.queryForObject(sql, rowMapper, phieuId);
            return Optional.ofNullable(hd);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Hoadon> findById(Integer id) {
        String sql = "SELECT * FROM tbl_hoadon WHERE id = ?";
        try {
            Hoadon hd = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(hd);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Hoadon save(Hoadon hd) {
        if (hd.getId() == null) {
            String sql = "INSERT INTO tbl_hoadon (ma_hoadon, id_phieudatsan, ghichu, tongtien) VALUES (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, hd.getMaHoadon());
                ps.setInt(2, hd.getPhieudatsan().getId());
                ps.setString(3, hd.getGhichu());
                ps.setDouble(4, hd.getTongtien());
                return ps;
            }, keyHolder);
            if (keyHolder.getKey() != null) {
                hd.setId(keyHolder.getKey().intValue());
            }
        } else {
            String sql = "UPDATE tbl_hoadon SET ma_hoadon = ?, id_phieudatsan = ?, ghichu = ?, tongtien = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    hd.getMaHoadon(),
                    hd.getPhieudatsan().getId(),
                    hd.getGhichu(),
                    hd.getTongtien(),
                    hd.getId());
        }
        return hd;
    }
}
