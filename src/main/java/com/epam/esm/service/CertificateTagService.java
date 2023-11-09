package com.epam.esm.service;

import com.epam.esm.Dto.GiftCertificate.GiftCertificateResponseDTO;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.repository.CertificateTagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertificateTagService {

    @Autowired
    CertificateTagRepository certificateTagRepository;

    public GiftCertificate saveCertificate(GiftCertificate giftCertificate) {

        return certificateTagRepository.createCertificate(giftCertificate);
    }
}
