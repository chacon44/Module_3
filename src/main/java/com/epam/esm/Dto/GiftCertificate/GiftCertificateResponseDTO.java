package com.epam.esm.Dto.GiftCertificate;

import com.epam.esm.model.Tag;
import java.util.List;
public class GiftCertificateResponseDTO {

    Long id;
    String name;
    String description;
    Double price;
    Long duration;
    String createUpdate;
    String LastUpdateDate;
    List<Tag> tags;

    public GiftCertificateResponseDTO(Long id, String name, String description, Double price, Long duration, String createUpdate, String LastUpdateDate
            //, List<Tag> tags
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.createUpdate = createUpdate;
        this.LastUpdateDate = LastUpdateDate;
        //this.tags = tags;
    }
}
