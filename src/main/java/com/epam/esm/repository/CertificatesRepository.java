package com.epam.esm.repository;

import com.epam.esm.Dto.GiftCertificate.GiftCertificateResponseDTO;
import com.epam.esm.model.GiftCertificate;
import java.util.Optional;

public interface CertificatesRepository {
    int save(GiftCertificate giftCertificate);

    GiftCertificateResponseDTO returnIdByName(String name);

    Optional<GiftCertificate> findById(Long id);

    int updatePrice(long Id, Long price);

    GiftCertificateResponseDTO returnCertificate(Long id);

    int deleteById(Long id);

    Optional<GiftCertificateResponseDTO> findByName(String name);
}
