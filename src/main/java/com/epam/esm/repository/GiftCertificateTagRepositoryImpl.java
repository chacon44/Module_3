package com.epam.esm.repository;

import com.epam.esm.Dto.GiftCertificate.GiftCertificateRequestDTO;
import com.epam.esm.mapper.GiftCertificateRowMapper;
import com.epam.esm.mapper.GiftCertificateRowMapperForIds;
import com.epam.esm.mapper.TagRowMapper;
import com.epam.esm.mapper.TagRowMapperForIds;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.epam.esm.exceptions.Messages.*;
import static com.epam.esm.logs.BooleanFlags.*;
import static com.epam.esm.logs.LogMessages.*;
import static com.epam.esm.queries.PostgreSqlQueries.*;

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

    @Override
    public void deleteTagFromJoinTable(long tagId) {
        log.info(DELETING_TAG_FROM_JOIN_TABLE);

        jdbcTemplate.update(DELETE_TAG_FROM_JOINT_TABLE, tagId);
    }

    /**
     * CERTIFICATE
     */
    @Override
    public Optional<Long> saveGiftCertificate(GiftCertificateRequestDTO requestDTO) {
        log.info(SAVING_GIFT_CERTIFICATE);

        certificateNameExists = getGiftCertificateByName(requestDTO.name()).isPresent();
        if (certificateNameExists)
            return Optional.empty();

        String defaultTime = formattingDate(new Date());
        Optional<Long> id = Optional.ofNullable(jdbcTemplate.queryForObject(SAVE_GIFT_CERTIFICATE, Long.class, requestDTO.name(), requestDTO.description(), requestDTO.price(), requestDTO.duration(), defaultTime, defaultTime));

        certificateSuccesfullySaved = id.isPresent();
        tagsListIsEmpty = requestDTO.tagIds().isEmpty();

        if (certificateSuccesfullySaved && !tagsListIsEmpty)
            joinTags(id.get(), requestDTO.tagIds());
        return id;
    }

    @Override
    public Optional<GiftCertificate> getGiftCertificateById(long giftCertificateId) {
        log.info(GETTING_GIFT_CERTIFICATES_BY_ID, giftCertificateId);

        try {
            Optional<GiftCertificate> giftCertificate = Optional.ofNullable(jdbcTemplate.queryForObject(GET_GIFT_CERTIFICATE_BY_ID, certificateRowMapper, giftCertificateId));

            certificateExists = giftCertificate.isPresent();

            if (!certificateExists) return Optional.empty();

            giftCertificate.get().setTags(
                    tagsByCertificateId(giftCertificateId).stream()
                            .map(this::getTagById)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .map(tag -> new Tag(tag.getId(), tag.getName()))
                            .collect(Collectors.toList())
            );
            return giftCertificate;
        } catch (EmptyResultDataAccessException e){
            log.warn(CERTIFICATE_WITH_ID_NOT_FOUND.formatted(giftCertificateId));
            return Optional.empty();
        }
    }

    @Override
    public Optional<GiftCertificate> getGiftCertificateByName(String giftCertificateName) {
        log.info(GETTING_GIFT_CERTIFICATE_BY_NAME, giftCertificateName);
        try {
            GiftCertificate giftCertificate = jdbcTemplate.queryForObject(GET_GIFT_CERTIFICATE_BY_NAME, certificateRowMapper, giftCertificateName);
            return Optional.ofNullable(giftCertificate);
        } catch (EmptyResultDataAccessException e) {
            log.warn(CERTIFICATE_WITH_NAME_NOT_FOUND.formatted(giftCertificateName));
            return Optional.empty();
        }
    }

    @Override
    public List<GiftCertificate> getCertificatesByTagName(String tagName) {

        Optional<Long> id = getTagByName(tagName);
        tagExists = id.isPresent();

        if (tagExists) {
            log.info(TAG_FOUND.formatted(tagName, id.get()));

            List<Long> giftCertificates = jdbcTemplate.query(GET_CERTIFICATES_BY_TAG_ID, certificateRowMapperForIds, id.get());

            certificatesListIsEmpty = giftCertificates.isEmpty();

            if (!certificatesListIsEmpty) {
                List<GiftCertificate> responseDTOList = new ArrayList<>();
                log.debug(CERTIFICATES_LIST_OF_TAG_IS_NOT_EMPTY, tagName);
                giftCertificates.stream().map(this::getGiftCertificateById).forEach(giftCertificateOptional -> giftCertificateOptional.ifPresent(responseDTOList::add));
                return responseDTOList;
            } else return List.of();
        } else return List.of();
    }

    @Override
    public List<GiftCertificate> getCertificatesBySearchWord(String searchWord) {

        List<GiftCertificate> giftCertificates = jdbcTemplate
                .query(GET_GIFT_CERTIFICATE_BY_SEARCH_WORD, certificateRowMapper, "%"+searchWord+"%", "%"+searchWord+"%");

        certificatesListIsEmpty = giftCertificates.isEmpty();
        if (!certificatesListIsEmpty) {
            log.debug(CERTIFICATES_LIST_IS_EMPTY);
            return List.of();
        }

        log.debug(CERTIFICATES_LIST_IS_NOT_EMPTY);
        List<GiftCertificate> responseDTOList = new ArrayList<>();
        for (GiftCertificate giftCertificate : giftCertificates) {
            Optional<GiftCertificate> giftCertificateOptional = getGiftCertificateById(giftCertificate.getId());
            giftCertificateOptional.ifPresent(responseDTOList::add);
        }

        log.debug(CERTIFICATES_LIST_IS_NOT_EMPTY);
        return responseDTOList;
    }

    public List<GiftCertificate> sortCertificates(List<GiftCertificate> commonList, String nameOrder, String createDateOrder) {
        log.info("name sorting order is {}", nameOrder);

        boolean hasToBeOrderedByName = nameOrder != null && (nameOrder.equals("ASC") || nameOrder.equals("DESC"));
        boolean hasToBeOrderedByCreateDate = createDateOrder != null && (createDateOrder.equals("ASC") || createDateOrder.equals("DESC"));
        Comparator<GiftCertificate> nameComparator = null;
        Comparator<GiftCertificate> dateComparator = null;

        if (hasToBeOrderedByName) {
            log.info(SORTING_CERTIFICATES_BY_NAME);
            nameComparator = nameOrder.equals("ASC") ?
                    Comparator.comparing(GiftCertificate::getName, Comparator.nullsLast(String::compareTo)) :
                    Comparator.comparing(GiftCertificate::getName, Comparator.nullsLast(String::compareTo)).reversed();

        }
        if (hasToBeOrderedByCreateDate) {
            log.info(SORTING_CERTIFICATES_BY_CREATE_DATE);

            dateComparator = createDateOrder.equals("ASC") ?
                    Comparator.comparing(GiftCertificate::getCreateDate, Comparator.nullsLast(String::compareTo)) :
                    Comparator.comparing(GiftCertificate::getCreateDate, Comparator.nullsLast(String::compareTo)).reversed();
        }

        if(hasToBeOrderedByName) {
            if (hasToBeOrderedByCreateDate)
                commonList.sort(nameComparator.thenComparing(dateComparator));
            else
                commonList.sort(nameComparator);
        }else if (hasToBeOrderedByCreateDate)
            commonList.sort(dateComparator);

        return commonList;
    }

    public List<GiftCertificate> filterCertificates(String tagName, String searchWord, String nameOrder, String createDateOrder) {

        List<GiftCertificate> tagFilter = tagName != null ? getCertificatesByTagName(tagName) : new ArrayList<>();
        List<GiftCertificate> wordFilter = searchWord != null ? getCertificatesBySearchWord(searchWord) : new ArrayList<>();

        List<GiftCertificate> commonList = new ArrayList<>(tagFilter);
        commonList.retainAll(wordFilter);

        List<GiftCertificate> result = new ArrayList<>(commonList);
        result.addAll(tagFilter);
        result.addAll(wordFilter);
        result = sortCertificates(result, nameOrder, createDateOrder);

        return result;
    }

    public List<Long> tagsByCertificateId(long certificate_id) {
        log.info(GETTING_TAG_IDS_BY_CERTIFICATE_ID, certificate_id);

        List<Long> tags = jdbcTemplate.query(GET_TAGS_BY_CERTIFICATE_ID, tagRowMapperForIds, certificate_id);

        tagsListIsEmpty = tags.isEmpty();
        return tagsListIsEmpty ? Collections.emptyList() : tags;
    }

    @Override
    public boolean deleteGiftCertificateById(long giftCertificateId) {
        log.info(DELETING_GIFT_CERTIFICATE_BY_ID, giftCertificateId);
        return jdbcTemplate.update(DELETE_GIFT_CERTIFICATE_BY_ID, giftCertificateId) > 0;
    }

    @Override
    public Optional<GiftCertificate> updateGiftCertificate(long id, GiftCertificateRequestDTO giftCertificateRequestDTO) {
        log.info(UPDATING_GIFT_CERTIFICATE, id);

        return getGiftCertificateById(id)
                .map(giftCertificate -> {
                    joinTags(id, giftCertificateRequestDTO.tagIds());

                    //TODO check if every tag passed actually exists
                    jdbcTemplate.update(UPDATE_GIFT_CERTIFICATE,
                            giftCertificateRequestDTO.name(),
                            giftCertificateRequestDTO.description(),
                            giftCertificateRequestDTO.price(),
                            giftCertificateRequestDTO.duration(),
                            formattingDate(new Date()),
                            id);
                    return getGiftCertificateById(id);
                }).orElse(Optional.empty());

    }

    private void joinTags(Long certificateId, List<Long> tagIds) throws RuntimeException {
        log.info("Joining tags to gift certificate with ID {}", certificateId);

        deleteCertificateFromJoinTable(certificateId);

        tagIds.forEach(tagId -> {
            getTagById(tagId).orElseThrow(() -> new RuntimeException("Tag not found: " + tagId));
            jdbcTemplate.update(SAVE_TAGS_TO_GIFT_CERTIFICATES, certificateId, tagId);
        });
    }

    /**
     * TAG
     */
    @Override
    public Optional<Tag> getTagById(long tagId) {
        log.info(GETTING_TAG_BY_ID, tagId);
        try {
            Tag responseTag = jdbcTemplate.queryForObject(GET_TAG_BY_ID, tagRowMapper, tagId);
            return Optional.ofNullable(responseTag);
        } catch (EmptyResultDataAccessException e) {
            log.warn(TAG_ID_NOT_FOUND.formatted(tagId));
            return Optional.empty();
        }
    }

    @Override
    public Optional<Long> getTagByName(String tagName) {
        log.info(GETTING_TAG_BY_NAME, tagName);
        try {
            Tag responseTag = jdbcTemplate.queryForObject(GET_TAG_BY_NAME, tagRowMapper, tagName);
            return Optional.ofNullable(responseTag).map(Tag::getId);
        } catch (EmptyResultDataAccessException e) {
            log.warn(TAG_NAME_NOT_FOUND.formatted(tagName));
            return Optional.empty();
        }
    }

    @Override
    public Optional<Long> saveTag(Tag tag) {
        log.info(SAVING_TAG_NAME);

        Optional<Long> id = Optional.ofNullable(jdbcTemplate.queryForObject(SAVE_TAG, Long.class, tag.getName()));

        id.ifPresent(tag::setId);
        return id;
    }

    @Override
    public boolean deleteTag(long tagId) {
        log.info(DELETING_TAG_BY_ID, tagId);

        return jdbcTemplate.update(DELETE_TAG_BY_ID, tagId) > 0;
    }

    /**
     * AUXILIARY METHODS
     */

    private String formattingDate(Date date) {

        //TODO move timeZone to current place (+1 hour)
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSS");
        df.setTimeZone(tz);
        return df.format(date);
    }
}
