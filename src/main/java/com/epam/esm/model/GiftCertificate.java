package com.epam.esm.model;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class GiftCertificate {
    private Long certificateId;
    private String certificateName;
    private String certificateDescription;
    private Double certificatePrice;
    private Long certificateDuration;
    private String certificateCreateDate;
    private String certificateLastUpdateDate;
    //private List<Tag> tags;
}

