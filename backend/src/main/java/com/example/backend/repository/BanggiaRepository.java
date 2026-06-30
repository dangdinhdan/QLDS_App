package com.example.backend.repository;

import com.example.backend.model.Banggia;
import com.example.backend.model.Banggiachitiet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class BanggiaRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BanggiachitietRepository banggiachitietRepository;

    private final RowMapper<Banggia> rowMapper = (rs, rowNum) -> Banggia.builder()
            .id(rs.getInt("id"))
            .maBanggia(rs.getString("ma_banggia"))
            .tenbanggia(rs.getString("tenbanggia"))
            .mota(rs.getString("mota"))
            .createAt(rs.getTimestamp("create_at") != null ? rs.getTimestamp("create_at").toLocalDateTime() : null)
            .updateAt(rs.getTimestamp("update_at") != null ? rs.getTimestamp("update_at").toLocalDateTime() : null)
            .deleteAt(rs.getTimestamp("delete_at") != null ? rs.getTimestamp("delete_at").toLocalDateTime() : null)
            .isdelete(rs.getObject("isdelete") != null ? rs.getBoolean("isdelete") : false)
            .build();

    public List<Banggia> findByIsdeleteFalse() {
        String sql = "SELECT * FROM tbl_banggia WHERE isdelete = false OR isdelete IS NULL";
        List<Banggia> list = jdbcTemplate.query(sql, rowMapper);
        for (Banggia bg : list) {
            bg.setDetails(banggiachitietRepository.findByBanggiaId(bg.getId()));
        }
        return list;
    }

    public Optional<Banggia> findById(Integer id) {
        String sql = "SELECT * FROM tbl_banggia WHERE id = ?";
        try {
            Banggia bg = jdbcTemplate.queryForObject(sql, rowMapper, id);
            if (bg != null) {
                bg.setDetails(banggiachitietRepository.findByBanggiaId(bg.getId()));
            }
            return Optional.ofNullable(bg);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Transactional
    public Banggia save(Banggia bg) {
        if (bg.getId() == null) {
            String sql = "INSERT INTO tbl_banggia (ma_banggia, tenbanggia, mota, isdelete, delete_at) VALUES (?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, bg.getMaBanggia());
                ps.setString(2, bg.getTenbanggia());
                ps.setString(3, bg.getMota());
                ps.setBoolean(4, bg.getIsdelete() != null ? bg.getIsdelete() : false);
                if (bg.getDeleteAt() != null) {
                    ps.setTimestamp(5, Timestamp.valueOf(bg.getDeleteAt()));
                } else {
                    ps.setNull(5, java.sql.Types.TIMESTAMP);
                }
                return ps;
            }, keyHolder);
            if (keyHolder.getKey() != null) {
                bg.setId(keyHolder.getKey().intValue());
            }
        } else {
            String sql = "UPDATE tbl_banggia SET ma_banggia = ?, tenbanggia = ?, mota = ?, isdelete = ?, delete_at = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    bg.getMaBanggia(),
                    bg.getTenbanggia(),
                    bg.getMota(),
                    bg.getIsdelete() != null ? bg.getIsdelete() : false,
                    bg.getDeleteAt() != null ? Timestamp.valueOf(bg.getDeleteAt()) : null,
                    bg.getId());
        }

        // Save details if they are provided
        if (bg.getDetails() != null) {
            // First delete old details for this banggia id
            banggiachitietRepository.deleteByBanggiaId(bg.getId());
            // Insert new details
            for (Banggiachitiet detail : bg.getDetails()) {
                detail.setBanggia(bg);
                banggiachitietRepository.save(detail);
            }
        }

        return bg;
    }
}
