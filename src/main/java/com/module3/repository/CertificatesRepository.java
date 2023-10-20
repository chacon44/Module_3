package com.module3.repository;

import com.module3.DTOs.ResponseDTO;
import com.module3.model.Certificate;

import java.util.Optional;

public interface CertificatesRepository {
    int save(Certificate certificate);

    ResponseDTO returnIdByName(String name);

    Optional<Certificate> findById(Long id);

    int updatePrice(long Id, Long price);

    ResponseDTO returnCertificate(Long id);

    int deleteById(Long id);

    Optional<ResponseDTO> findByName(String name);
}
