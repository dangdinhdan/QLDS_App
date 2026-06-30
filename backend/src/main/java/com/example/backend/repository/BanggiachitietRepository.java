package com.example.backend.repository;

import com.example.backend.model.Banggia;
import com.example.backend.model.Banggiachitiet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Time;
import java.util.List;

@Repository
public class BanggiachitietRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Banggiachitiet> rowMapper = (rs, rowNum) -> {
        Banggiachitiet item = Banggiachitiet.builder()
                .id(rs.getInt("id"))
                .loaingay(rs.getString("loaingay"))
                .giobatdau(rs.getTime("giobatdau") != null ? rs.getTime("giobatdau").toLocalTime() : null)
                .giokethuc(rs.getTime("giokethuc") != null ? rs.getTime("giokethuc").toLocalTime() : null)
                .dongia(rs.getDouble("dongia"))
                .createAt(rs.getTimestamp("create_at") != null ? rs.getTimestamp("create_at").toLocalDateTime() : null)
                .build();
        
        int banggiaId = rs.getInt("id_banggia");
        item.setBanggia(Banggia.builder().id(banggiaId).build());
        return item;
    };

    public List<Banggiachitiet> findByBanggiaId(Integer banggiaId) {
        String sql = "SELECT * FROM tbl_banggiachitiet WHERE id_banggia = ?";
        return jdbcTemplate.query(sql, rowMapper, banggiaId);
    }

    public void deleteByBanggiaId(Integer banggiaId) {
        String sql = "DELETE FROM tbl_banggiachitiet WHERE id_banggia = ?";
        jdbcTemplate.update(sql, banggiaId);
    }

    public Banggiachitiet save(Banggiachitiet detail) {
        if (detail.getId() == null) {
            String sql = "INSERT INTO tbl_banggiachitiet (id_banggia, loaingay, giobatdau, giokethuc, dongia) VALUES (?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, detail.getBanggia().getId());
                ps.setString(2, detail.getLoaingay());
                ps.setTime(3, detail.getGiobatdau() != null ? Time.valueOf(detail.getGiobatdau()) : null);
                ps.setTime(4, detail.getGiokethuc() != null ? Time.valueOf(detail.getGiokethuc()) : null);
                ps.setDouble(5, detail.getDongia());
                return ps;
            }, keyHolder);
            if (keyHolder.getKey() != null) {
                detail.setId(keyHolder.getKey().intValue());
            }
        } else {
            String sql = "UPDATE tbl_banggiachitiet SET id_banggia = ?, loaingay = ?, giobatdau = ?, giokethuc = ?, dongia = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    detail.getBanggia().getId(),
                    detail.getLoaingay(),
                    detail.getGiobatdau() != null ? Time.valueOf(detail.getGiobatdau()) : null,
                    detail.getGiokethuc() != null ? Time.valueOf(detail.getGiokethuc()) : null,
                    detail.getDongia(),
                    detail.getId());
        }
        return detail;
    }
}
