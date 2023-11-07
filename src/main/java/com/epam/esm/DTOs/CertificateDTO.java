package com.epam.esm.DTOs;

import com.epam.esm.model.GiftCertificate;

import java.util.List;

public record CertificateDTO (GiftCertificate giftCertificate, String createDate, String lastUpdateDate, List<TagDTO> tags){
}
