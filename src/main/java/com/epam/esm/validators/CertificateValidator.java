package com.epam.esm.validators;

import com.epam.esm.Dto.GiftCertificate.GiftCertificateRequestDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CertificateValidator {
    public static String validateRequest(GiftCertificateRequestDTO requestDTO) {

        List<String> errors = new ArrayList<>();

        if (requestDTO.name() == null || requestDTO.name().isEmpty()) {
            errors.add("Name is required");
        }

        if (requestDTO.description() == null || requestDTO.description().isEmpty()) {
            errors.add("Description is required");
        }

        try {
            double price = Double.parseDouble(requestDTO.price().toString());
            if (Double.isNaN(price) || Double.isInfinite(price)) {
                errors.add("Price must be a finite number");
            } else if (price < 0) {
                errors.add("Price must be non-negative");
            }
        } catch (NumberFormatException e) {
            errors.add("Price must be a valid number");
        }

        try {
            long duration = Long.parseLong(requestDTO.duration().toString());
            if (duration < 0) {
                errors.add("Duration must be non-negative");
            }
        } catch (NumberFormatException e) {
            errors.add("Duration must be a valid number");
        }

        if (errors.isEmpty()) {
            return "Valid";
        } else {
            return String.join(", ", errors);
        }
    }
}
