package com.epam.esm.service;

import com.epam.esm.Dto.Errors.ErrorDTO;
import com.epam.esm.Dto.Tag.TagRequestDTO;
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

    public ResponseEntity<?> saveTag(TagRequestDTO tagRequestDTO) {
        ResponseEntity<ErrorDTO> requestValidationMessage = validateTagRequest(tagRequestDTO);
        if (requestValidationMessage != null) return requestValidationMessage;

        tagExists = giftCertificateTagRepository.getTagByName(tagRequestDTO.name()) != null;

        if (tagExists) {
            Long idFound = giftCertificateTagRepository.getTagByName(tagRequestDTO.name());
            String message = TAG_ALREADY_EXISTS.formatted(idFound);
            return ResponseEntity.badRequest().body(new ErrorDTO(message, TAG_BAD_REQUEST));
        }

        Tag tag = new Tag();
        tag.setName(tagRequestDTO.name());

        Long id = giftCertificateTagRepository.saveTag(tag);

        tagSuccesfullySaved = id != null;
        if (!tagSuccesfullySaved) {
            return ResponseEntity.status(BAD_REQUEST).body(new ErrorDTO(TAG_COULD_NOT_BE_SAVED, TAG_BAD_REQUEST));
        }

        Tag tagResponse = giftCertificateTagRepository.getTagById(id);
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
            giftCertificateTagRepository.deleteTagFromJoinTable(tagId);
            return ResponseEntity.status(FOUND).body(null);
        }

        String message = TAG_ID_NOT_FOUND.formatted(tagId);
        ErrorDTO errorResponse = new ErrorDTO(message, TAG_NOT_FOUND);
        return ResponseEntity.status(NOT_FOUND).body(errorResponse);

    }
    private ResponseEntity<ErrorDTO> validateTagRequest(TagRequestDTO tagRequestDTO) {
        String validationMessage = TagValidator.validateForSave(tagRequestDTO);
        tagRequestIsValid = validationMessage.equals("Valid");
        if (!tagRequestIsValid) {
            return ResponseEntity.badRequest().body(new ErrorDTO(validationMessage, TAG_BAD_REQUEST));
        }
        return null;
    }
}
