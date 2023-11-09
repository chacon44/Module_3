package com.epam.esm.repository;

import com.epam.esm.Dto.GiftCertificate.GiftCertificateResponseDTO;
import com.epam.esm.exceptions.CustomizedExceptions;
import com.epam.esm.exceptions.ErrorCode;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Tag;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.epam.esm.database.DatabaseData.*;
import static com.epam.esm.date.DateCalculation.*;

@Repository
public class JdbcCertificatesRepository implements CertificatesRepository
{

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RestController.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int save(GiftCertificate giftCertificate) {

        String saveQuery = ("INSERT INTO %s " +
                "(%s,%s,%s,%s,%s,%s) VALUES (?, ?, ?, ?, ?, ?)").formatted(
                        TABLE_CERTIFICATE_NAME,
                CERTIFICATE_NAME,
                CERTIFICATE_DESCRIPTION,
                CERTIFICATE_PRICE,
                CERTIFICATE_DURATION,
                CERTIFICATE_CREATE_DATE,
                CERTIFICATE_LAST_UPDATE_DATE
        );

        try {
            return jdbcTemplate.update(saveQuery,
                    giftCertificate.getCertificateName(),
                    giftCertificate.getCertificateDescription(),
                    giftCertificate.getCertificatePrice(),
                    giftCertificate.getCertificateDuration(),
                    createDate,
                    lastUpdateDate);
        } catch (DataAccessException e) {
            logger.error("Database error on Save", e);
            throw new CustomizedExceptions(e.getMessage(), ErrorCode.DATABASE_ERROR);
        }
    }

    @Override
    public GiftCertificateResponseDTO returnCertificate(Long id) {
        String query = ("SELECT * from %s WHERE %s = ?").formatted(TABLE_CERTIFICATE_NAME, CERTIFICATE_ID);

        return jdbcTemplate.queryForObject(query, (resultSet, rowNum) ->
                        (new GiftCertificateResponseDTO(
                                id,
                                resultSet.getString(CERTIFICATE_NAME),
                                resultSet.getString(CERTIFICATE_DESCRIPTION),
                                resultSet.getDouble(CERTIFICATE_PRICE),
                                resultSet.getLong(  CERTIFICATE_DURATION),
                                resultSet.getString(CERTIFICATE_CREATE_DATE),
                                resultSet.getString(CERTIFICATE_LAST_UPDATE_DATE)
                    )),
        id
    );
    }
    @Override
    public GiftCertificateResponseDTO returnIdByName(String name) {

        String query = "SELECT * from " + TABLE_CERTIFICATE_NAME + " WHERE " + CERTIFICATE_NAME + " = '" + name + "'";

        List<GiftCertificateResponseDTO> responses = jdbcTemplate.query(
                query,
                (resultSet, i) -> new GiftCertificateResponseDTO(
                        resultSet.getLong(CERTIFICATE_ID),
                        name,
                        resultSet.getString(CERTIFICATE_DESCRIPTION),
                        resultSet.getDouble(CERTIFICATE_PRICE),
                        resultSet.getLong(CERTIFICATE_DURATION),
                        resultSet.getString(CERTIFICATE_CREATE_DATE),
                        resultSet.getString(CERTIFICATE_LAST_UPDATE_DATE)
                )
        );
        return responses.get(0);
    }

    @Override
    public Optional<GiftCertificate> findById(Long id) {
        String query = ("SELECT * from %s WHERE %s = ?").formatted(TABLE_CERTIFICATE_NAME, CERTIFICATE_ID);

        System.out.println(query);
        try {
            GiftCertificate response = jdbcTemplate.queryForObject(query, (resultSet, rowNum) ->
                            new GiftCertificate(),
                    id
            );
            logger.info("Finished search");
            return Optional.ofNullable(response);
        } catch (EmptyResultDataAccessException e) {
            logger.info("No rows found");
            return Optional.empty();
        }
    }

    @Override
    public Optional<GiftCertificateResponseDTO> findByName(String name) {

        String query = "SELECT * from " + TABLE_CERTIFICATE_NAME + " WHERE " + CERTIFICATE_NAME + " = '" + name + "'";

        List<GiftCertificateResponseDTO> responses = jdbcTemplate.query(
                query,
                (resultSet, i) -> new GiftCertificateResponseDTO(
                        resultSet.getLong(CERTIFICATE_ID),
                        name,
                        resultSet.getString(CERTIFICATE_DESCRIPTION),
                        resultSet.getDouble(CERTIFICATE_PRICE),
                        resultSet.getLong(CERTIFICATE_DURATION),
                        resultSet.getString(CERTIFICATE_CREATE_DATE),
                        resultSet.getString(CERTIFICATE_LAST_UPDATE_DATE)

                )
        );

        if (responses.isEmpty()) {
            return Optional.empty();
        } else {

            return Optional.of(responses.get(0));
        }
    }
    @Override
    public int deleteById(Long id) {
        String query = ("DELETE from %s WHERE %s = ?").formatted(TABLE_CERTIFICATE_NAME, CERTIFICATE_ID);
        return jdbcTemplate.update(query, id);
    }
    @Override
    public int updatePrice(long id, Long price) {

        String query = ("UPDATE %s SET %s = ? WHERE %s = ?").formatted(TABLE_CERTIFICATE_NAME, CERTIFICATE_PRICE, CERTIFICATE_ID);
        return jdbcTemplate.update(query, price, id);
    }
}
