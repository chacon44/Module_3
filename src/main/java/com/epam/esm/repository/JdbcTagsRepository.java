package com.epam.esm.repository;

import com.epam.esm.Dto.Tag.ResponseTagDTO;
import com.epam.esm.model.Tag;
import com.epam.esm.exceptions.CustomizedExceptions;
import com.epam.esm.exceptions.ErrorCode;
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

@Repository
public class JdbcTagsRepository implements TagsRepository {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RestController.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int save(Tag tag) {

        String saveQuery = ("INSERT INTO %s " +
                "(%s) VALUES (?)").formatted(
                TABLE_TAG_NAME,
                TAG_NAME
        );

        try {
            return jdbcTemplate.update(saveQuery, tag.getTagName());
        } catch (DataAccessException e) {
            logger.error("Database error on Save", e);
            throw new CustomizedExceptions(e.getMessage(), ErrorCode.DATABASE_ERROR);
        }
    }

    @Override
    public ResponseTagDTO returnTag(Long tagId) {
        String query = ("SELECT * from %s WHERE %s = ?").formatted(TABLE_TAG_NAME, TAG_ID);

        return jdbcTemplate.queryForObject(query, (resultSet, rowNum) ->
                        (new ResponseTagDTO(
                                tagId,
                                resultSet.getString(TAG_NAME)
                        )),
                tagId
        );
    }
    @Override
    public ResponseTagDTO returnIdByName(String tagName) {

        String query = "SELECT * from " + TABLE_TAG_NAME + " WHERE " + TAG_NAME + " = '" + tagName + "'";

        List<ResponseTagDTO> responses = jdbcTemplate.query(
                query,
                (resultSet, i) -> new ResponseTagDTO(
                        resultSet.getLong(TAG_ID),
                        tagName
                )
        );
        return responses.get(0);
    }

    @Override
    public Optional<Tag> findById(Long tagId) {
        String query = ("SELECT * from %s WHERE %s = ?").formatted(TABLE_TAG_NAME, TAG_ID);

        System.out.println(query);
        try {
            Tag response = jdbcTemplate.queryForObject(query, (resultSet, rowNum) ->
                            new Tag(),
                    tagId
            );
            logger.info("Finished search");
            return Optional.ofNullable(response);
        } catch (EmptyResultDataAccessException e) {
            logger.info("No rows found");
            return Optional.empty();
        }
    }

    @Override
    public Optional<ResponseTagDTO> findByName(String tagName) {

        String query = "SELECT * from " + TABLE_TAG_NAME + " WHERE " + TAG_NAME + " = '" + tagName + "'";

        List<ResponseTagDTO> responses = jdbcTemplate.query(
                query,
                (resultSet, i) -> new ResponseTagDTO(
                        resultSet.getLong(TAG_ID),
                        tagName
                )
        );

        if (responses.isEmpty()) {
            return Optional.empty();
        } else {

            return Optional.of(responses.get(0));
        }
    }
    @Override
    public int deleteById(Long tagId) {

        String query = ("DELETE from %s WHERE %s = ?").formatted(TABLE_TAG_NAME, TAG_ID);
    //UPDATE CERTIFICATE_TAG TABLE
        return jdbcTemplate.update(query, tagId);
    }

    @Override
    public int updateName(long tagId, String tagName){

        String query = ("UPDATE %s SET %s = ? WHERE %s = ?").formatted(TABLE_TAG_NAME, TAG_NAME, TAG_ID);
        return jdbcTemplate.update(query, tagName, tagId);
    }
}
