package com.epam.esm.repository;

import com.epam.esm.model.GiftCertificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.Objects;

import static com.epam.esm.database.DatabaseData.*;
import static com.epam.esm.queries.PostgreSqlQueries.FIND_GIFT_CERTIFICATE_BY_ID;
import static com.epam.esm.queries.PostgreSqlQueries.INSERT_NEW_GIFT_CERTIFICATE;

@Repository
public class CertificateTagRepository implements CertificateTag{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //CERTIFICATES
    private final RowMapper<GiftCertificate> certificateRowMapper = (rs, rowNum) -> {
        GiftCertificate giftCertificate = new GiftCertificate();

        giftCertificate.setCertificateId(rs.getLong(CERTIFICATE_ID));
        giftCertificate.setCertificateName(rs.getString(CERTIFICATE_NAME));
        giftCertificate.setCertificateDescription(rs.getString(CERTIFICATE_DESCRIPTION));
        giftCertificate.setCertificatePrice(rs.getDouble(CERTIFICATE_PRICE));
        giftCertificate.setCertificateDuration(rs.getLong(CERTIFICATE_DURATION));
        giftCertificate.setCertificateCreateDate(rs.getString(CERTIFICATE_CREATE_DATE));
        giftCertificate.setCertificateLastUpdateDate(rs.getString(CERTIFICATE_LAST_UPDATE_DATE));

        //giftCertificate.setTags(findTagsByCertificateId(giftCertificate.getCertificateId()));
        return giftCertificate;
    };

//    private final RowMapper<Tag> tagRowMapper = (rs, rowNum) -> {
//        Tag tag = new Tag();
//        tag.setTagId(rs.getLong(TAG_ID));
//        tag.setTagName(rs.getString(TAG_NAME));
//        return tag;
//    };

    @Transactional
    @Override
    public GiftCertificate createCertificate(GiftCertificate giftCertificate) {

        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_NEW_GIFT_CERTIFICATE, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, giftCertificate.getCertificateName());
            ps.setString(2, giftCertificate.getCertificateDescription());
            ps.setDouble(3, giftCertificate.getCertificatePrice());
            ps.setLong(  4, giftCertificate.getCertificateDuration());
            ps.setString(5, giftCertificate.getCertificateCreateDate());
            ps.setString(6, giftCertificate.getCertificateLastUpdateDate());

            return ps;
        }, holder);

        long newCertificateId = Objects.requireNonNull(holder.getKey()).longValue();
        giftCertificate.setCertificateId(newCertificateId);

        return giftCertificate;
    }

//    public GiftCertificate readCertificate(Long certificateId) {
//        return jdbcTemplate.queryForObject(FIND_GIFT_CERTIFICATE_BY_ID, new Object[]{certificateId}, certificateRowMapper);
//    }
//
//    public GiftCertificateResponseDTO readCertificateByName(String name) {
//        final String sql = "SELECT * FROM CERTIFICATES WHERE name ="+name;
//
//        GiftCertificate giftCertificate = jdbcTemplate.queryForObject(sql, new Object[]{name}, certificateRowMapper);
//
//        return new GiftCertificateResponseDTO(
//                giftCertificate.getCertificateId(),
//                giftCertificate.getCertificateName(),
//                giftCertificate.getCertificateDescription(),
//                giftCertificate.getCertificatePrice(),
//                giftCertificate.getCertificateDuration(),
//                giftCertificate.getCertificateCreateDate(),
//                giftCertificate.getCertificateLastUpdateDate()
//                //,giftCertificate.getTags()
//        );
//    }
//
//    public List<GiftCertificate> readAllCertificates() {
//        final String sql = "SELECT * FROM CERTIFICATE";
//        return jdbcTemplate.query(sql, certificateRowMapper);
//    }
//
//    @Transactional
//    public void updateCertificate(GiftCertificate giftCertificate) {
//        final String sql = "UPDATE CERTIFICATE SET certificate_name = ? WHERE certificate_id = ?";
//        jdbcTemplate.update(sql, giftCertificate.getCertificateName(), giftCertificate.getCertificateId());
//
//        deleteAllCertificateTagLinks(giftCertificate.getCertificateId());
////        for (Tag tag : giftCertificate.getTags()) {
////            createCertificateTagLink(giftCertificate.getCertificateId(), tag.getTagId());
////        }
//    }
//
//    public void deleteCertificate(Long certificateId) {
//        deleteAllCertificateTagLinks(certificateId);
//        final String sql = "DELETE FROM CERTIFICATE WHERE certificate_id = ?";
//        jdbcTemplate.update(sql, certificateId);
//    }
//
//    private void createCertificateTagLink(Long certificateId, Long tagId) {
//        final String sql = "INSERT INTO CERTIFICATE_TAG (certificate_id, tag_id) VALUES (?, ?)";
//        jdbcTemplate.update(sql, certificateId, tagId);
//    }
//
//    private void deleteAllCertificateTagLinks(Long certificateId) {
//        final String sql = "DELETE FROM CERTIFICATE_TAG WHERE certificate_id = ?";
//        jdbcTemplate.update(sql, certificateId);
//    }
//
//    private List<Tag> findTagsByCertificateId(Long certificateId) {
//        final String sql = "SELECT b.* FROM TAG b INNER JOIN CERTIFICATE_TAG fb ON b.tag_id = fb.tag_id WHERE fb.certificate_id = ?";
//        return jdbcTemplate.query(sql, new Object[]{certificateId}, tagRowMapper);
//    }
//
//    private GiftCertificate findCertificateByCertificateId(Long certificateId) {
//        final String sql = "SELECT c.* FROM CERTIFICATE c INNER JOIN CERTIFICATE_TAG ct ON c.certificate_id = ct.certificate_id";
//        return (GiftCertificate) jdbcTemplate.query(sql, certificateRowMapper);
//    }
}
