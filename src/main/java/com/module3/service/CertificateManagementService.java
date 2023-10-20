package com.module3.service;

import com.module3.DTOs.ResponseDTO;
import com.module3.model.Certificate;
import com.module3.repository.JdbcCertificatesRepository;
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

    public void saveCertificate(Certificate certificate) {

        jdbcCertificatesRepository.save(certificate);
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

    public Optional<Certificate> findById(long id) {

        return jdbcCertificatesRepository.findById(id);
    }

    public void putPriceIntoCertificate(ResponseDTO responseDTO) {

        jdbcCertificatesRepository.updatePrice(responseDTO.index(), responseDTO.price());
    }
}
