package com.module3.DTOs;

import com.module3.model.Certificate;

import java.util.List;

public record CertificateDTO (Certificate certificate, long duration, String createDate, String lastUpdateDate, List<TagDTO> tags){
}
