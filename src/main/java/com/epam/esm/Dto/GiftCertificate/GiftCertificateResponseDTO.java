package com.epam.esm.Dto.GiftCertificate;

import com.epam.esm.model.Tag;
import java.util.List;

public record GiftCertificateResponseDTO(
        Long id,
        String name,
        String description,
        Double price,
        Long duration,
        String createDate,
        String lastUpdateDate,
        List<Tag> tags) { }
