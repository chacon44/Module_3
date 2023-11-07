package com.epam.esm.repository;

import com.epam.esm.DTOs.ResponseDTO;
import com.epam.esm.model.GiftCertificate;
import java.util.Optional;

public interface CertificatesRepository {
    int save(GiftCertificate giftCertificate);

    ResponseDTO returnIdByName(String name);

    Optional<GiftCertificate> findById(Long id);

    int updatePrice(long Id, Long price);

    ResponseDTO returnCertificate(Long id);

    int deleteById(Long id);

    Optional<ResponseDTO> findByName(String name);
}
