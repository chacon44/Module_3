package com.epam.esm.model;

import lombok.*;
import java.util.List;
import java.util.Objects;

@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GiftCertificate {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Long duration;
    private String createDate;
    private String lastUpdateDate;
    private List<Tag> tags;

    public GiftCertificate(String name, String description, Double price, Long duration) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
    }
}
