package com.epam.esm.mapper;

import com.epam.esm.enums.Columns;
import com.epam.esm.model.Tag;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class TagRowMapper implements RowMapper<Tag> {
    @Override
    public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {

        return  Tag.builder()
                    .id(rs.getLong(Columns.TAG_TABLE_ID.getColumn()))
                    .name(rs.getString(Columns.TAG_TABLE_NAME.getColumn()))
                    .build();
    }
}