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
    GiftCertificate saveGiftCertificate(GiftCertificate giftCertificate, Date date, List<Long> tagList);

    //GET
    GiftCertificate getGiftCertificateById(long id);
    GiftCertificate getGiftCertificateByName(String giftCertificateName);
    List<GiftCertificate> getCertificatesByTagName(String tagName);
    List<GiftCertificate> searchCertificatesByKeyword(String keyWord);

    //DELETE
    boolean deleteGiftCertificate(long id);

    //UPDATE
    GiftCertificate updateGiftCertificate(long id, GiftCertificate giftCertificate, List<Long> tagIds);

    /**
     TAGS
     */

    //SAVE
    Tag saveTag(String tagName);
    //GET
    Tag getTagById(long id);
    Tag getTagByName(String name);
    List<Tag> getTagsListByCertificateId(long certificate_id);
    List<Long> tagIdListByCertificateId(long certificate_id);
    List<GiftCertificate> sortCertificates(List<GiftCertificate> commonList, String nameOrder, String createDateOrder);
    List<GiftCertificate> filterCertificates(String tagName, String searchWord, String nameOrder, String createDateOrder);

    //DELETE
    boolean deleteTag(long id);
    boolean filterValidTags(List<Long> tagIds);

    void joinTags(Long giftCertificateId, List<Long> tagIds);
}
