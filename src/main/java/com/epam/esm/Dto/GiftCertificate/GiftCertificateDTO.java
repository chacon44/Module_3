package com.epam.esm.Dto.GiftCertificate;

import com.epam.esm.Dto.Tag.TagDTO;

import java.util.List;

public record GiftCertificateDTO(
        Long certificateId,
        String certificateName,
        String certificateDescription,
        Double certificatePrice,
        Long certificateDuration,
        String createDate,
        String lastUpdateDate
        //,List<TagDTO> tags
){ }
