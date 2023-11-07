package com.epam.esm.service;

import com.epam.esm.DTOs.ResponseDTO;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.repository.JdbcCertificatesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CertificateManagementService {
    @Autowired
    JdbcCertificatesRepository jdbcCertificatesRepository;
    private static final Logger logger = LoggerFactory.getLogger(CertificateManagementService.class);

    public void saveCertificate(GiftCertificate giftCertificate) {

        jdbcCertificatesRepository.save(giftCertificate);
    }

    public ResponseDTO returnIdByQuestion(String name) {

        return jdbcCertificatesRepository.returnIdByName(name);
    }
    public ResponseDTO returnCertificate(long id) {

        return jdbcCertificatesRepository.returnCertificate(id);
    }
    public void deleteCertificate(long id) {

        jdbcCertificatesRepository.deleteById(id);
    }

    public Optional<GiftCertificate> findById(long id) {

        return jdbcCertificatesRepository.findById(id);
    }

    public void putPriceIntoCertificate(ResponseDTO responseDTO) {

        jdbcCertificatesRepository.updatePrice(responseDTO.index(), responseDTO.price());
    }
}
