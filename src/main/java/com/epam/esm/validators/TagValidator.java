package com.epam.esm.validators;

import com.epam.esm.Dto.Tag.TagRequestDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TagValidator {
    public static String validateForSave (TagRequestDTO requestDTO){

        if (requestDTO.name() == null || requestDTO.name().isEmpty()) {
            return "Tag name is required";
        }
        return "Valid";
    }
}
