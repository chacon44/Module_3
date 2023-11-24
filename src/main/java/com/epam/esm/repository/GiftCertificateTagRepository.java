package com.epam.esm.repository;

import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Tag;

import java.util.Date;
import java.util.List;

public interface GiftCertificateTagRepository {

    /**
    CERTIFICATES
    */

    //SAVE
    Long saveGiftCertificate(GiftCertificate giftCertificate, Date date, List<Long> tagList);

    //GET
    GiftCertificate getGiftCertificateById(long id);
    GiftCertificate getGiftCertificateByName(String giftCertificateName);
    List<GiftCertificate> getCertificatesByTagName(String tagName);
    List<GiftCertificate> getCertificatesBySearchWord(String searchWord);

    //DELETE
    boolean deleteGiftCertificate(long id);

    //UPDATE
    GiftCertificate updateGiftCertificate(long id, GiftCertificate giftCertificate, List<Long> tagIds);

    /**
     TAGS
     */

    //SAVE
    Long saveTag(Tag tag);
    //GET
    Tag getTagById(long id);
    Long getTagByName(String name);
    List<Tag> getTagsListByCertificateId(long certificate_id);
    List<Long> tagsByCertificateId(long certificate_id);
    List<GiftCertificate> sortCertificates(List<GiftCertificate> commonList, String nameOrder, String createDateOrder);
    List<GiftCertificate> filterCertificates(String tagName, String searchWord, String nameOrder, String createDateOrder);

    //DELETE
    void deleteTagFromJoinTable(long id);
    boolean deleteTag(long id);
    boolean filterValidTags(List<Long> tagIds);
    /**
     JOINT TABLE
     */
    void joinTags(Long certificateId, List<Long> tagIds);
    //DELETE
    boolean deleteCertificateFromJoinTable(long id) throws RuntimeException;
    String formattingDate(Date date);
}
