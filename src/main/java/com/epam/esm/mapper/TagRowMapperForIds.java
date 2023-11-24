package com.epam.esm.mapper;

import com.epam.esm.enums.Columns;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@Slf4j
public class TagRowMapperForIds implements RowMapper<Long> {
    @Override
    public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong(Columns.TAG_TABLE_ID.getColumn());
    }
}
