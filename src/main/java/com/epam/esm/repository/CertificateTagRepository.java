package com.epam.esm.repository;

import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Tag;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.List;
import java.util.Objects;

public class CertificateTagRepository {
    private final JdbcTemplate jdbcTemplate;

    public CertificateTagRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<GiftCertificate> certificateRowMapper = (rs, rowNum) -> {
        GiftCertificate giftCertificate = new GiftCertificate();
        giftCertificate.setCertificateId(rs.getLong("certificate_id"));
        giftCertificate.setCertificateName(rs.getString("certificate_name"));
        giftCertificate.setCertificateDescription(rs.getString("certificate_description"));
        giftCertificate.setCertificatePrice(rs.getString("certificate_price"));
        giftCertificate.setCertificateDuration(rs.getLong("certificate_duration"));
        giftCertificate.setCertificateCreateDate(rs.getString("certificate_createDate"));
        giftCertificate.setCertificateLastUpdateDate(rs.getString("certificate_lastUpdateDate"));

        giftCertificate.setTags(findTagsByCertificateId(giftCertificate.getCertificateId()));
        return giftCertificate;
    };

    private final RowMapper<Tag> tagRowMapper = (rs, rowNum) -> {
        Tag tag = new Tag();
        tag.setTagId(rs.getLong("tag_id"));
        tag.setTagName(rs.getString("tag_name"));
        return tag;
    };

    @Transactional
    public GiftCertificate createCertificate(GiftCertificate giftCertificate) {
        final String sql = "INSERT INTO CERTIFICATE (certificate_name) VALUES (?)";

        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, giftCertificate.getCertificateName());
            return ps;
        }, holder);

        long newCertificateId = Objects.requireNonNull(holder.getKey()).longValue();
        giftCertificate.setCertificateId(newCertificateId);

        for (Tag tag : giftCertificate.getTags()) {
            createCertificateTagLink(newCertificateId, tag.getTagId());
        }
        return giftCertificate;
    }

    public GiftCertificate readCertificate(Long certificateId) {
        final String sql = "SELECT * FROM CERTIFICATE WHERE certificate_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{certificateId}, certificateRowMapper);
    }

    public List<GiftCertificate> readAllCertificates() {
        final String sql = "SELECT * FROM CERTIFICATE";
        return jdbcTemplate.query(sql, certificateRowMapper);
    }

    @Transactional
    public void updateCertificate(GiftCertificate giftCertificate) {
        final String sql = "UPDATE CERTIFICATE SET certificate_name = ? WHERE certificate_id = ?";
        jdbcTemplate.update(sql, giftCertificate.getCertificateName(), giftCertificate.getCertificateId());

        deleteAllCertificateTagLinks(giftCertificate.getCertificateId());
        for (Tag tag : giftCertificate.getTags()) {
            createCertificateTagLink(giftCertificate.getCertificateId(), tag.getTagId());
        }
    }

    public void deleteCertificate(Long certificateId) {
        deleteAllCertificateTagLinks(certificateId);
        final String sql = "DELETE FROM CERTIFICATE WHERE certificate_id = ?";
        jdbcTemplate.update(sql, certificateId);
    }

    private void createCertificateTagLink(Long certificateId, Long tagId) {
        final String sql = "INSERT INTO CERTIFICATE_TAG (certificate_id, tag_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, certificateId, tagId);
    }

    private void deleteAllCertificateTagLinks(Long certificateId) {
        final String sql = "DELETE FROM CERTIFICATE_TAG WHERE certificate_id = ?";
        jdbcTemplate.update(sql, certificateId);
    }

    private List<Tag> findTagsByCertificateId(Long certificateId) {
        final String sql = "SELECT b.* FROM TAG b INNER JOIN CERTIFICATE_TAG fb ON b.tag_id = fb.tag_id WHERE fb.certificate_id = ?";
        return jdbcTemplate.query(sql, new Object[]{certificateId}, tagRowMapper);
    }
}
