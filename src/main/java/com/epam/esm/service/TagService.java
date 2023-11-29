package com.epam.esm.service;

import com.epam.esm.Dto.Errors.ErrorDTO;
import com.epam.esm.model.Tag;
import com.epam.esm.repository.GiftCertificateTagRepository;
import com.epam.esm.validators.TagValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.epam.esm.exceptions.Codes.TAG_BAD_REQUEST;
import static com.epam.esm.exceptions.Codes.TAG_NOT_FOUND;
import static com.epam.esm.exceptions.Messages.*;
import static com.epam.esm.logs.BooleanFlags.*;
import static org.springframework.http.HttpStatus.*;


@Slf4j
@Service
public class TagService {

    private final GiftCertificateTagRepository giftCertificateTagRepository;


    @Autowired
    public TagService(GiftCertificateTagRepository giftCertificateTagRepository) {
        this.giftCertificateTagRepository = giftCertificateTagRepository;
    }

    public ResponseEntity<?> saveTag(String tagName) {
        ResponseEntity<ErrorDTO> requestValidationMessage = validateTagRequest(tagName);
        if (requestValidationMessage != null) return requestValidationMessage;
        tagExists = giftCertificateTagRepository.getTagByName(tagName) != null;

        if (tagExists) {

            Tag tagFound = giftCertificateTagRepository.getTagByName(tagName);
            String message = TAG_ALREADY_EXISTS.formatted(tagFound.getId());
            return ResponseEntity.badRequest().body(new ErrorDTO(message, TAG_BAD_REQUEST));
        }

        Tag tag = giftCertificateTagRepository.saveTag(tagName);
        tagSuccesfullySaved = tag != null;
        if (!tagSuccesfullySaved) {
            return ResponseEntity.status(BAD_REQUEST).body(new ErrorDTO(TAG_COULD_NOT_BE_SAVED, TAG_BAD_REQUEST));
        }

        Tag tagResponse = giftCertificateTagRepository.getTagById(tag.getId());

        tagSuccesfullySaved = tagResponse != null;
        return tagSuccesfullySaved ?
                ResponseEntity.status(CREATED).body(tagResponse) :
                ResponseEntity.status(BAD_REQUEST).body(new ErrorDTO(TAG_COULD_NOT_BE_SAVED, TAG_BAD_REQUEST));
    }
    public ResponseEntity<?> getTagById(long tagId) {
        Tag tag = giftCertificateTagRepository.getTagById(tagId);

        tagExists = tag != null;
        if (tagExists) {
            return ResponseEntity.ok(tag);
        } else {
            String message = TAG_ID_NOT_FOUND.formatted(tagId);

            ErrorDTO errorResponse = new ErrorDTO(message, TAG_NOT_FOUND);
            return ResponseEntity.status(NOT_FOUND).body(errorResponse);
        }
    }
    public ResponseEntity<?> deleteTagById(long tagId) {

        tagSuccessfullyDeleted = giftCertificateTagRepository.deleteTag(tagId);
        if (tagSuccessfullyDeleted) {
            return ResponseEntity.status(FOUND).body(null);
        }

        String message = TAG_ID_NOT_FOUND.formatted(tagId);
        ErrorDTO errorResponse = new ErrorDTO(message, TAG_NOT_FOUND);
        return ResponseEntity.status(NOT_FOUND).body(errorResponse);

    }
    private ResponseEntity<ErrorDTO> validateTagRequest(String tagName) {
        String validationMessage = TagValidator.validateForSave(tagName);
        tagRequestIsValid = validationMessage.equals("Valid");
        return tagRequestIsValid ? null :
                ResponseEntity.badRequest().body(new ErrorDTO(validationMessage, TAG_BAD_REQUEST));
    }
}
