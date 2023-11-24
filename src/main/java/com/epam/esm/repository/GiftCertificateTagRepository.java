package com.epam.esm.repository;

import com.epam.esm.Dto.GiftCertificate.GiftCertificateRequestDTO;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Tag;

import java.util.List;
import java.util.Optional;

public interface GiftCertificateTagRepository {

    /**
    CERTIFICATES
    */

    //SAVE
    Optional <Long> saveGiftCertificate(GiftCertificateRequestDTO GiftCertificateRequestDTO);

    //GET
    Optional<GiftCertificate> getGiftCertificateById(long id);
    Optional<GiftCertificate> getGiftCertificateByName(String giftCertificateName);
    List<GiftCertificate> getCertificatesByTagName(String tagName);
    List<GiftCertificate> getCertificatesBySearchWord(String searchWord);
    List<GiftCertificate> sortCertificates(List<GiftCertificate> commonList, String nameOrder, String createDateOrder);
    List<GiftCertificate> filterCertificates(String tagName, String searchWord, String nameOrder, String createDateOrder);
    //DELETE
    boolean deleteGiftCertificateById(long id);

    //UPDATE
    Optional<GiftCertificate> updateGiftCertificate(long id, GiftCertificateRequestDTO giftCertificate);

    /**
     TAGS
     */

    //SAVE
    Optional<Long> saveTag(Tag tag);

    //GET
    Optional<Tag> getTagById(long id);
    Optional<Long> getTagByName(String name);

    //DELETE
    void deleteTagFromJoinTable(long id);
    boolean deleteTag(long id);

    /**
     JOINT TABLE
     */

    //DELETE
    boolean deleteCertificateFromJoinTable(long id);
}
