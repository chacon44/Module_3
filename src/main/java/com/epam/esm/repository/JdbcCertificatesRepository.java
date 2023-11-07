package com.epam.esm.repository;

import com.epam.esm.DTOs.ResponseDTO;
import com.epam.esm.exceptions.CustomizedExceptions;
import com.epam.esm.exceptions.ErrorCode;
import com.epam.esm.model.GiftCertificate;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static com.epam.esm.database.DatabaseData.*;
import static com.epam.esm.date.DateCalculation.*;

@Repository
public class JdbcCertificatesRepository implements CertificatesRepository {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RestController.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int save(GiftCertificate giftCertificate) {

        String saveQuery = ("INSERT INTO %s " +
                "(%s,%s,%s,%s,%s,%s) VALUES (?, ?, ?, ?, ?, ?)").formatted(
                        TABLE_NAME,
                COLUMN_NAME,
                COLUMN_DESCRIPTION,
                COLUMN_PRICE,
                COLUMN_DURATION,
                COLUMN_CREATE_DATE,
                COLUMN_LASTUPDATE_DATE
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
    public ResponseDTO returnCertificate(Long id) {
        String query = ("SELECT * from %s WHERE %s = ?").formatted(TABLE_NAME, COLUMN_ID);

        return jdbcTemplate.queryForObject(query, (resultSet, rowNum) ->
                        (new ResponseDTO(
                                id,
                                resultSet.getString(COLUMN_NAME),
                                resultSet.getString(COLUMN_DESCRIPTION),
                                resultSet.getLong(COLUMN_PRICE),
                                resultSet.getLong(COLUMN_DURATION),
                                resultSet.getString(COLUMN_CREATE_DATE),
                                resultSet.getString(COLUMN_LASTUPDATE_DATE)
                        )),
                id
        );
    }
    @Override
    public ResponseDTO returnIdByName(String name) {

        String query = "SELECT * from " + TABLE_NAME + " WHERE " + COLUMN_NAME + " = '" + name + "'";

        List<ResponseDTO> responses = jdbcTemplate.query(
                query,
                (resultSet, i) -> new ResponseDTO(
                        resultSet.getLong(COLUMN_ID),
                        name,
                        resultSet.getString(COLUMN_DESCRIPTION),
                        resultSet.getLong(COLUMN_PRICE),
                        resultSet.getLong(COLUMN_DURATION),
                        resultSet.getString(COLUMN_CREATE_DATE),
                        resultSet.getString(COLUMN_LASTUPDATE_DATE)
                )
        );
        return responses.get(0);
    }

    @Override
    public Optional<GiftCertificate> findById(Long id) {
        String query = ("SELECT * from %s WHERE %s = ?").formatted(TABLE_NAME, COLUMN_ID);

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
    public Optional<ResponseDTO> findByName(String name) {

        String query = "SELECT * from " + TABLE_NAME + " WHERE " + COLUMN_NAME + " = '" + name + "'";

        List<ResponseDTO> responses = jdbcTemplate.query(
                query,
                (resultSet, i) -> new ResponseDTO(
                        resultSet.getLong(COLUMN_ID),
                        name,
                        resultSet.getString(COLUMN_DESCRIPTION),
                        resultSet.getLong(COLUMN_PRICE),
                        resultSet.getLong(COLUMN_DURATION),
                        resultSet.getString(COLUMN_CREATE_DATE),
                        resultSet.getString(COLUMN_LASTUPDATE_DATE)
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
        String query = ("DELETE from %s WHERE %s = ?").formatted(TABLE_NAME, COLUMN_ID);
        return jdbcTemplate.update(query, id);
    }
    @Override
    public int updatePrice(long id, Long price) {

        String query = ("UPDATE %s SET %s = ? WHERE %s = ?").formatted(TABLE_NAME, COLUMN_PRICE, COLUMN_ID);
        return jdbcTemplate.update(query, price, id);
    }
}
