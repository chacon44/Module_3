package com.epam.esm.Dto.GiftCertificate;

import com.epam.esm.model.GiftCertificate;
import java.util.List;

public record GiftCertificateRequestDTO(GiftCertificate giftCertificate, List<Long> tagIds){
}
