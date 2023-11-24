package com.epam.esm.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.epam.esm.enums.Columns.*;

@Component
public class GiftCertificateRowMapperForIds implements RowMapper<Long> {

    @Override
    public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong(GIFT_CERTIFICATE_ID.getColumn());
    }
}
