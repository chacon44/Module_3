package com.epam.esm.repository;

import com.epam.esm.mapper.GiftCertificateRowMapper;
import com.epam.esm.mapper.GiftCertificateRowMapperForIds;
import com.epam.esm.mapper.TagRowMapper;
import com.epam.esm.mapper.TagRowMapperForIds;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.epam.esm.exceptions.Messages.*;
import static com.epam.esm.logs.BooleanFlags.*;
import static com.epam.esm.logs.LogMessages.*;
import static com.epam.esm.queries.PostgreSqlQueries.*;
import static java.sql.Statement.*;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;

@Repository
@Slf4j
public class GiftCertificateTagRepositoryImpl implements GiftCertificateTagRepository {

    private final JdbcTemplate jdbcTemplate;
    private final TagRowMapper tagRowMapper;
    private final TagRowMapperForIds tagRowMapperForIds = new TagRowMapperForIds();
    private final GiftCertificateRowMapper certificateRowMapper;
    private final GiftCertificateRowMapperForIds certificateRowMapperForIds = new GiftCertificateRowMapperForIds();

    public GiftCertificateTagRepositoryImpl(JdbcTemplate jdbcTemplate, TagRowMapper tagRowMapper, GiftCertificateRowMapper certificateRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.tagRowMapper = tagRowMapper;
        this.certificateRowMapper = certificateRowMapper;
    }

    @Override
    public boolean deleteCertificateFromJoinTable(long giftCertificateId) {
        log.info(DELETING_CERTIFICATE_FROM_JOINT_TABLE);

        return jdbcTemplate.update(DELETE_CERTIFICATE_FROM_JOINT_TABLE, giftCertificateId) > 0;
    }


    /**
     * CERTIFICATE
     */
    @Override
    public Long saveGiftCertificate(GiftCertificate giftCertificate, Date date, List<Long> tagList) {
        log.info(SAVING_GIFT_CERTIFICATE);

        certificateNameExists = getGiftCertificateByName(giftCertificate.getName()) != null;
        if (certificateNameExists)
            return null;


        KeyHolder keyHolder = new GeneratedKeyHolder();
        log.info("generated key holder");

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(SAVE_GIFT_CERTIFICATE, RETURN_GENERATED_KEYS);

                    log.info("ps created");

                    ps.setString(1, giftCertificate.getName());
                    ps.setString(2, giftCertificate.getDescription());
                    ps.setDouble(3, giftCertificate.getPrice());
                    ps.setLong  (4, giftCertificate.getDuration());
                    ps.setString(5, formattingDate(date));
                    ps.setString(6, formattingDate(date));
                    return ps;
                }, keyHolder);

        if (keyHolder.getKey() != null) {
            Long id = keyHolder.getKey().longValue();
            giftCertificate.setId(id);
            joinTags(id, tagList);
            return id;
        } else
            return null;
    }

    @Override
    public GiftCertificate getGiftCertificateById(long giftCertificateId) {
        log.info(GETTING_GIFT_CERTIFICATES_BY_ID, giftCertificateId);

        try {
            GiftCertificate giftCertificate = jdbcTemplate.queryForObject(GET_GIFT_CERTIFICATE_BY_ID, certificateRowMapper, giftCertificateId);

            if (giftCertificate != null) {

                List<Tag> tagsToAdd = getTagsListByCertificateId(giftCertificateId);
                giftCertificate.setTags(tagsToAdd);

                return giftCertificate;
            } else return null;
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

        Long id = getTagByName(tagName);
        tagExists = id != null;
        if (tagExists) {
            log.info(TAG_FOUND.formatted(tagName, id));

            List<Long> giftCertificates = jdbcTemplate.query(GET_CERTIFICATES_BY_TAG_ID, certificateRowMapperForIds, id);

            boolean certificatesListIsEmpty = giftCertificates.isEmpty();

            return certificatesListIsEmpty ? List.of() : giftCertificates.stream().map(this::getGiftCertificateById).collect(Collectors.toList());
        } else return List.of();
    }

    @Override
    public List<GiftCertificate> getCertificatesBySearchWord(String searchWord) {

        String searchTerm = "%" + searchWord + "%";

        List<GiftCertificate> giftCertificates = jdbcTemplate
                .query(GET_GIFT_CERTIFICATE_BY_SEARCH_WORD, certificateRowMapper, searchTerm, searchTerm);

        certificatesListIsNotEmpty = giftCertificates.isEmpty();

        return certificatesListIsNotEmpty ?
                List.of() :
                giftCertificates.stream().map(giftCertificate -> getGiftCertificateById(giftCertificate.getId())).collect(Collectors.toList());
    }

    @Override
    public List<GiftCertificate> sortCertificates(List<GiftCertificate> commonList, String nameOrder, String createDateOrder) {

        log.info(nameOrder);
        log.info(createDateOrder);

        List<String> order = new ArrayList<>(asList("ASC", "DESC"));
        boolean hasToBeOrderedByName = nameOrder != null && order.contains(nameOrder);
        boolean hasToBeOrderedByCreateDate = createDateOrder != null && order.contains(createDateOrder);
        log.info("bool " + hasToBeOrderedByName);
        log.info("bool " + hasToBeOrderedByCreateDate);

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
                commonList.retainAll(getCertificatesBySearchWord(searchWord));
        } else if (searchWord != null) {
            commonList.addAll(getCertificatesBySearchWord(searchWord));
        }

        return sortCertificates(commonList, nameOrder, createDateOrder);
    }

    @Override
    public boolean deleteGiftCertificate(long giftCertificateId) {
        log.info(DELETING_GIFT_CERTIFICATE_BY_ID, giftCertificateId);
        return jdbcTemplate.update(DELETE_GIFT_CERTIFICATE_BY_ID, giftCertificateId) > 0;
    }

    @Override
    public GiftCertificate updateGiftCertificate(long id, GiftCertificate giftCertificate, List<Long> tagIds) {
        log.info(UPDATING_GIFT_CERTIFICATE, id);

        thereAreNonExistingTags = !filterValidTags(tagIds);
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
                formattingDate(new Date()),
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
    public List<Long> tagsByCertificateId(long certificate_id) {
        log.info(GETTING_TAG_IDS_BY_CERTIFICATE_ID, certificate_id);

        List<Long> tags = jdbcTemplate.query(GET_TAGS_BY_CERTIFICATE_ID, tagRowMapperForIds, certificate_id);

        tagsListIsEmpty = tags.isEmpty();
        return tagsListIsEmpty ? List.of() : tags;
    }

    @Override
    public List<Tag> getTagsListByCertificateId(long certificate_id) {
        return tagsByCertificateId(certificate_id).stream()
                .map(this::getTagById)
                .filter(Objects::nonNull)
                .map(tag -> new Tag(tag.getId(), tag.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public Long getTagByName(String tagName) {
        log.info(GETTING_TAG_BY_NAME, tagName);
        try {
            Tag responseTag = jdbcTemplate.queryForObject(GET_TAG_BY_NAME, tagRowMapper, tagName);
            if(responseTag != null)
                return responseTag.getId();
            else return null;
        } catch (EmptyResultDataAccessException e) {
            log.warn(TAG_NAME_NOT_FOUND.formatted(tagName));
            return null;
        }
    }

    @Override
    public Long saveTag(Tag tag) {
        log.info(SAVING_TAG_NAME);

        if (!tag.getName().isEmpty() && getTagByName(tag.getName()) == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(SAVE_TAG, RETURN_GENERATED_KEYS);
                        ps.setString(1, tag.getName());
                        return ps;
                    }, keyHolder);

            if (keyHolder.getKey() != null) {
                Long id = keyHolder.getKey().longValue();
                tag.setId(id);
                return id;
            }

        }
        return null;
    }


    @Override
    public boolean deleteTag(long tagId) {
        log.info(DELETING_TAG_BY_ID, tagId);

        return jdbcTemplate.update(DELETE_TAG_BY_ID, tagId) > 0;
    }

    @Override
    public void deleteTagFromJoinTable(long tagId) throws RuntimeException {
        log.info(DELETING_TAG_FROM_JOIN_TABLE);

        Tag tag = getTagById(tagId);
        if (tag == null)
            throw new RuntimeException("This id does not exists");

        if (getCertificatesByTagName(tag.getName()).isEmpty())
            throw new RuntimeException("This id is not attached to any certificate");

        jdbcTemplate.update(DELETE_TAG_FROM_JOINT_TABLE, tagId);
    }

    @Override
    public void joinTags(Long certificateId, List<Long> tagIds) throws RuntimeException {
        log.info("Joining tags to gift certificate with id {}", certificateId);

        if (certificateId == null)
            throw new RuntimeException("Certificate id is null");

        if (getGiftCertificateById(certificateId) == null)
            throw new RuntimeException("Gift certificate with id " + certificateId + " not found");

        if (tagIds.isEmpty())
            throw new RuntimeException("Tag list is empty");

        deleteCertificateFromJoinTable(certificateId);
        Set<Long> uniqueSet = new LinkedHashSet<>(tagIds);
        List<Long> uniqueList = new ArrayList<>(uniqueSet);

        uniqueList.forEach(tagId -> {
            Tag tag = getTagById(tagId);
            if (tag == null) {
                throw new RuntimeException("Tag with id " + tagId + " not found");
            }
            jdbcTemplate.update(SAVE_TAGS_TO_GIFT_CERTIFICATES, certificateId, tagId);
        });

    }

    @Override
    public boolean filterValidTags(List<Long> tagIds) {
        log.info("Validating tags");

        for (Long tagId : tagIds) {
            boolean exists = getTagById(tagId) != null;
            if (!exists)
                return false;
        }
        return true;
    }

    /**
     * AUXILIARY METHODS
     */
    @Override
    public String formattingDate(Date date) {

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSS'Z'");
        df.setTimeZone(tz);
        return df.format(date);
    }
}

