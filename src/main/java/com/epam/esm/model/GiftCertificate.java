package com.epam.esm.model;

import lombok.Getter;

import java.util.List;

public class GiftCertificate {

    private Long certificateId;
    @Getter
    private String certificateName;
    @Getter
    private List<Tag> tags;
    @Getter
    private String certificateDescription;
    @Getter
    private String certificateLastUpdateDate;
    @Getter
    private String certificateCreateDate;
    @Getter
    private String certificatePrice;
    @Getter
    private long certificateDuration;

    public void setCertificateId(long certificateId) {
        this.certificateId = certificateId;
    }

    public long getCertificateId() {
        return certificateId;
    }

    public void setCertificateName(String certificateName) {
        this.certificateName = certificateName;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void setCertificateDescription(String certificateDescription) {
        this.certificateDescription = certificateDescription;
    }

    public void setCertificatePrice(String certificatePrice) {
        this.certificatePrice = certificatePrice;
    }

    public void setCertificateDuration(long certificateDuration) {
        this.certificateDuration = certificateDuration;
    }

    public void setCertificateCreateDate(String certificateCreateDate) {
        this.certificateCreateDate = certificateCreateDate;
    }

    public void setCertificateLastUpdateDate(String certificateLastUpdateDate) {
        this.certificateLastUpdateDate = certificateLastUpdateDate;
    }

}

