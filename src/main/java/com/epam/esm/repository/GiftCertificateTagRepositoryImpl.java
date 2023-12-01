package com.epam.esm.repository;

import com.epam.esm.enums.Columns;
import com.epam.esm.mapper.GiftCertificateRowMapper;
import com.epam.esm.mapper.TagRowMapper;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.*;

import static com.epam.esm.enums.Columns.*;
import static com.epam.esm.exceptions.Messages.*;
import static com.epam.esm.logs.LogMessages.*;
import static com.epam.esm.queries.PostgreSqlQueries.*;
import static java.sql.Statement.*;
import static java.util.Collections.*;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

@Repository
@Slf4j
public class GiftCertificateTagRepositoryImpl implements GiftCertificateTagRepository {

    private final JdbcTemplate jdbcTemplate;
    private final TagRowMapper tagRowMapper;
    private final GiftCertificateRowMapper certificateRowMapper;

    public GiftCertificateTagRepositoryImpl(JdbcTemplate jdbcTemplate, TagRowMapper tagRowMapper, GiftCertificateRowMapper certificateRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.tagRowMapper = tagRowMapper;
        this.certificateRowMapper = certificateRowMapper;
    }

    /**
     * CERTIFICATE
     */
    @Override
    public GiftCertificate saveGiftCertificate(GiftCertificate giftCertificate, List<Long> tagList) {
        log.info(SAVING_GIFT_CERTIFICATE);

        boolean certificateNameExists = getGiftCertificateByName(giftCertificate.getName()) != null;
        if (certificateNameExists)
            return null;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(SAVE_GIFT_CERTIFICATE, RETURN_GENERATED_KEYS);

                    ps.setString(1, giftCertificate.getName());
                    ps.setString(2, giftCertificate.getDescription());
                    ps.setDouble(3, giftCertificate.getPrice());
                    ps.setLong(  4, giftCertificate.getDuration());

                    return ps;
                }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null) {

            // Get the generated certificate ID and return a new Tag object
            Long id = ((Number) keys.get(GIFT_CERTIFICATE_ID.getColumn())).longValue();
            giftCertificate.setId(id);
            joinTags(id, tagList);
            return giftCertificate;
        }
        return null;
    }

    @Override
    public GiftCertificate getGiftCertificateById(long giftCertificateId) {
        log.info(GETTING_GIFT_CERTIFICATES_BY_ID, giftCertificateId);

        try {
            GiftCertificate giftCertificate = jdbcTemplate.queryForObject(GET_GIFT_CERTIFICATE_BY_ID, certificateRowMapper, giftCertificateId);

            if (giftCertificate == null)
                return null;
            else {
                List<Tag> tagsToAdd = getTagsListByCertificateId(giftCertificateId);
                giftCertificate.setTags(tagsToAdd);
                return giftCertificate;
            }
        } catch (EmptyResultDataAccessException e) {
            log.warn(CERTIFICATE_WITH_ID_NOT_FOUND.formatted(giftCertificateId));
            return null;
        }
    }

    @Override
    public GiftCertificate getGiftCertificateByName(String giftCertificateName) {
        log.info(GETTING_GIFT_CERTIFICATE_BY_NAME, giftCertificateName);
        try {
            GiftCertificate giftCertificate = jdbcTemplate.queryForObject(GET_GIFT_CERTIFICATE_BY_NAME, certificateRowMapper, giftCertificateName);
            if (giftCertificate == null)
                return null;
            else {
                List<Tag> tagsToAdd = getTagsListByCertificateId(giftCertificate.getId());
                giftCertificate.setTags(tagsToAdd);
                return giftCertificate;
            }
        } catch (EmptyResultDataAccessException e) {
            log.warn(CERTIFICATE_WITH_NAME_NOT_FOUND.formatted(giftCertificateName));
            return null;
        }
    }

    @Override
    public List<GiftCertificate> getCertificatesByTagName(String tagName) {

        Tag tag = getTagByName(tagName);
        if (tag != null) {
            log.info(TAG_FOUND.formatted(tagName, tag.getId()));

            List<Long> giftCertificates = jdbcTemplate.query(
                    GET_CERTIFICATES_BY_TAG_ID,
                    (rs, rowNum) -> rs.getLong(GIFT_CERTIFICATE_ID.getColumn()),
                    tag.getId());

            boolean certificatesListIsEmpty = giftCertificates.isEmpty();

            return certificatesListIsEmpty ? List.of() : giftCertificates.stream().map(this::getGiftCertificateById).collect(toList());
        } else return List.of();
    }

    @Override
    public List<GiftCertificate> searchCertificatesByKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            throw new IllegalArgumentException("Keyword cannot be null or empty");
        }

        String searchTerm = "%" + keyword + "%";

        List<GiftCertificate> giftCertificates = jdbcTemplate.query(
                GET_GIFT_CERTIFICATE_BY_SEARCH_WORD,
                certificateRowMapper, searchTerm, searchTerm);

        return giftCertificates.isEmpty() ? emptyList() :
                giftCertificates.stream().map(giftCertificate -> getGiftCertificateById(giftCertificate.getId())).collect(toList());
    }

    @Override
    public List<GiftCertificate> sortCertificates(List<GiftCertificate> commonList, String nameOrder, String createDateOrder) {

        List<String> order = List.of("ASC", "DESC");
        boolean hasToBeOrderedByName = nameOrder != null && order.contains(nameOrder);
        boolean hasToBeOrderedByCreateDate = createDateOrder != null && order.contains(createDateOrder);

        Comparator<GiftCertificate> nameComparator = null;
        Comparator<GiftCertificate> dateComparator = null;

        if (hasToBeOrderedByName) {
            log.info(SORTING_CERTIFICATES_BY_NAME);
            nameComparator = nameOrder.equals("ASC") ?
                    comparing(GiftCertificate::getName) :
                    comparing(GiftCertificate::getName).reversed();

        }
        if (hasToBeOrderedByCreateDate) {
            log.info(SORTING_CERTIFICATES_BY_CREATE_DATE);
            dateComparator = createDateOrder.equals("ASC") ?
                    comparing(GiftCertificate::getCreateDate) :
                    comparing(GiftCertificate::getCreateDate).reversed();
        }

        if (hasToBeOrderedByName) {
            if (hasToBeOrderedByCreateDate)
                commonList.sort(nameComparator.thenComparing(dateComparator));
            else
                commonList.sort(nameComparator);
        } else if (hasToBeOrderedByCreateDate)
            commonList.sort(dateComparator);

        return commonList;
    }

    @Override
    public List<GiftCertificate> filterCertificates(String tagName, String searchWord, String nameOrder, String createDateOrder) {

        List<GiftCertificate> commonList = new ArrayList<>();
        if (tagName != null) {
            commonList.addAll(getCertificatesByTagName(tagName));
            if (searchWord != null)
                commonList.retainAll(searchCertificatesByKeyword(searchWord));
        } else if (searchWord != null) {
            commonList.addAll(searchCertificatesByKeyword(searchWord));
        }

        return sortCertificates(commonList, nameOrder, createDateOrder);
    }

    @Override
    public boolean deleteGiftCertificate(long giftCertificateId) {
        log.info(DELETING_GIFT_CERTIFICATE_BY_ID, giftCertificateId);
        jdbcTemplate.update(DELETE_CERTIFICATE_FROM_JOINT_TABLE, giftCertificateId);

        return jdbcTemplate.update(DELETE_GIFT_CERTIFICATE_BY_ID, giftCertificateId) > 0;
    }

    @Override
    public GiftCertificate updateGiftCertificate(long id, GiftCertificate giftCertificate, List<Long> tagIds) {
        log.info(UPDATING_GIFT_CERTIFICATE, id);

        boolean thereAreNonExistingTags = !filterValidTags(tagIds);
        boolean certificateDoesNotExist = getGiftCertificateById(id) == null;
        if (thereAreNonExistingTags || certificateDoesNotExist) {
            return null;
        }

        joinTags(id, tagIds);
        jdbcTemplate.update(UPDATE_GIFT_CERTIFICATE,
                giftCertificate.getName(),
                giftCertificate.getDescription(),
                giftCertificate.getPrice(),
                giftCertificate.getDuration(),
                id);

        return getGiftCertificateById(id);
    }


    /**
     * TAG
     */

    @Override
    public Tag getTagById(long tagId) {
        log.info(GETTING_TAG_BY_ID, tagId);
        try {
            return jdbcTemplate.queryForObject(GET_TAG_BY_ID, tagRowMapper, tagId);
        } catch (EmptyResultDataAccessException e) {
            log.warn(TAG_ID_NOT_FOUND.formatted(tagId));
            return null;
        }
    }

    @Override
    public List<Long> tagIdListByCertificateId(long certificate_id) {
        log.info(GETTING_TAG_IDS_BY_CERTIFICATE_ID, certificate_id);
        return jdbcTemplate.query(
                GET_TAGS_BY_CERTIFICATE_ID,
                (rs, rowNum) -> rs.getLong(Columns.TAG_TABLE_ID.getColumn()),
                certificate_id);
    }

    @Override
    public List<Tag> getTagsListByCertificateId(long certificate_id) {
        return tagIdListByCertificateId(certificate_id).stream()
                .map(this::getTagById)
                .filter(Objects::nonNull)
                .map(tag -> new Tag(tag.getId(), tag.getName()))
                .collect(toList());
    }

    @Override
    public Tag getTagByName(String tagName) {
        log.info(GETTING_TAG_BY_NAME, tagName);
        try {
            return jdbcTemplate.queryForObject(GET_TAG_BY_NAME, tagRowMapper, tagName);
        } catch (EmptyResultDataAccessException e) {
            log.warn(TAG_NAME_NOT_FOUND.formatted(tagName));
            return null;
        }
    }

    @Override
    public Tag saveTag(String tagName) {
        if (!tagName.isEmpty() && getTagByName(tagName) == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(SAVE_TAG, RETURN_GENERATED_KEYS);
                ps.setString(1, tagName);
                return ps;
            }, keyHolder);

            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null) {
                Long id = ((Number) keys.get(TAG_TABLE_ID.getColumn())).longValue();
                return new Tag(id, tagName);
            }
        }

        return null;
    }

    @Override
    public boolean deleteTag(long tagId) {
        log.info(DELETING_TAG_BY_ID, tagId);
        jdbcTemplate.update(DELETE_TAG_FROM_JOINT_TABLE, tagId);
        return jdbcTemplate.update(DELETE_TAG_BY_ID, tagId) > 0;
    }

    @Override
    public void joinTags(Long giftCertificateId, List<Long> tagIds) throws RuntimeException {

        if (giftCertificateId == null)
            throw new RuntimeException("Certificate is null");

        log.info("Joining tags to gift certificate with id {}", giftCertificateId);

        jdbcTemplate.update(DELETE_CERTIFICATE_FROM_JOINT_TABLE, giftCertificateId);
        tagIds.forEach(tagId -> {
            Tag tag = getTagById(tagId);
            if (tag == null) {
                throw new RuntimeException("Tag with id " + tagId + " not found");
            }
            //TODO create method for this
            jdbcTemplate.update(SAVE_TAGS_TO_GIFT_CERTIFICATES, giftCertificateId, tagId);
        });
    }

    @Override
    public boolean filterValidTags(List<Long> tagIds) {
        log.info("Validating tags");

        return tagIds.stream().allMatch(tagId -> getTagById(tagId) != null);
    }
}

