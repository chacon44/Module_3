package com.epam.esm.service;

import com.epam.esm.Dto.Errors.ErrorDTO;
import com.epam.esm.exceptions.Codes;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.repository.GiftCertificateTagRepository;
import com.epam.esm.validators.CertificateValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.epam.esm.exceptions.Codes.*;
import static com.epam.esm.exceptions.Messages.*;

@SuppressWarnings("rawtypes")
@Slf4j
@Service
public class GiftCertificateService {

    private final GiftCertificateTagRepository giftCertificateTagRepository;

    @Autowired
    public GiftCertificateService(GiftCertificateTagRepository giftCertificateTagRepository) {
        this.giftCertificateTagRepository = giftCertificateTagRepository;
    }

    public ResponseEntity<?> saveGiftCertificate(GiftCertificate giftCertificate, List<Long> tagIdsList) {

        ResponseEntity<ErrorDTO> requestValidationMessage = validateCertificateRequest(giftCertificate);
        if (requestValidationMessage != null) return requestValidationMessage;

        //eliminate duplicated tag ids
        tagIdsList = tagIdsList.stream().distinct().collect(Collectors.toList());
        GiftCertificate tryToFindCertificate = giftCertificateTagRepository.getGiftCertificateByName(giftCertificate.getName());

        GiftCertificate saveGiftCertificate = giftCertificateTagRepository.saveGiftCertificate(giftCertificate, tagIdsList);

        if (tryToFindCertificate == null) {
            if (saveGiftCertificate != null) {
                GiftCertificate response = giftCertificateTagRepository.getGiftCertificateByName(giftCertificate.getName());
                return new ResponseEntity<>(response, HttpStatus.CREATED);

            } else {
                ErrorDTO errorResponse = new ErrorDTO(CERTIFICATE_COULD_NOT_BE_SAVED, 500);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

        } else {
            Long idFound = tryToFindCertificate.getId();
            ErrorDTO errorResponse = new ErrorDTO(CERTIFICATE_ALREADY_EXISTS.formatted(idFound), CERTIFICATE_FOUND);
            return ResponseEntity.status(HttpStatus.FOUND).body(errorResponse);
        }
    }

    public ResponseEntity getGiftCertificateById(Long giftCertificateId) {

        GiftCertificate giftCertificate = giftCertificateTagRepository.getGiftCertificateById(giftCertificateId);

        if (giftCertificate != null) {
            return ResponseEntity.ok(giftCertificate);
        } else {
            String message = CERTIFICATE_WITH_ID_NOT_FOUND.formatted(giftCertificateId);

            ErrorDTO errorResponse = new ErrorDTO(message, Codes.CERTIFICATE_NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    public ResponseEntity<?> getFilteredCertificates(String tagName, String searchWord, String nameOrder, String createDateOrder) {
        //TODO add exceptions here

        List<GiftCertificate> list = giftCertificateTagRepository.filterCertificates(tagName, searchWord, nameOrder, createDateOrder);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteGiftCertificateById(Long giftCertificateId) {

        boolean certificateSuccessfullyDeleted = giftCertificateTagRepository.deleteGiftCertificate(giftCertificateId);

        return certificateSuccessfullyDeleted ?
                ResponseEntity.status(HttpStatus.FOUND).body(null) :
                ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                        new ErrorDTO(
                                CERTIFICATE_WITH_ID_NOT_FOUND.formatted(giftCertificateId),
                                Codes.CERTIFICATE_NOT_FOUND));
    }
    public ResponseEntity<?> updateGiftCertificate(Long id, GiftCertificate giftCertificate, List<Long> tagIdsList) {
        ResponseEntity<ErrorDTO> requestValidationMessage = validateCertificateRequest(giftCertificate);
        if (requestValidationMessage != null) {
            return requestValidationMessage;
        }

        tagIdsList = tagIdsList.stream().distinct().collect(Collectors.toList());

        if (!validateUpdate(id, giftCertificate, tagIdsList)) {
            GiftCertificate certificate = giftCertificateTagRepository.getGiftCertificateByName(giftCertificate.getName());

            String message = CERTIFICATE_ALREADY_EXISTS.formatted(certificate.getId());
            ErrorDTO errorResponse = new ErrorDTO(message, Codes.CERTIFICATE_FOUND);
            return ResponseEntity.status(HttpStatus.FOUND).body(errorResponse);
        }

        GiftCertificate responseDTO = giftCertificateTagRepository.updateGiftCertificate(id, giftCertificate, tagIdsList);
        if (responseDTO == null) {
            if (giftCertificateTagRepository.getGiftCertificateById(id) == null) {
                String message = CERTIFICATE_WITH_ID_NOT_FOUND.formatted(id);
                ErrorDTO errorResponse = new ErrorDTO(message, Codes.CERTIFICATE_NOT_FOUND);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            } else {
                String message = "There are non existing tags";
                ErrorDTO errorResponse = new ErrorDTO(message, TAG_NOT_FOUND);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    private ResponseEntity<ErrorDTO> validateCertificateRequest(GiftCertificate giftCertificate) {
        Optional<String> validationMessage = CertificateValidator.validateRequest(giftCertificate);

        if (validationMessage.isPresent()) {
            ErrorDTO errorResponse = new ErrorDTO(validationMessage.get(), CERTIFICATE_BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        return null;
    }

    private boolean validateUpdate(Long id, GiftCertificate giftCertificate, List<Long> tagIdsList) {

        GiftCertificate certificate = giftCertificateTagRepository.getGiftCertificateById(id);

        if (certificate != null) {

            List<Long> tagIdList = giftCertificateTagRepository.tagIdListByCertificateId(id);
            boolean nameExist = Objects.equals(certificate.getName(), giftCertificate.getName());
            boolean descriptionExist = Objects.equals(certificate.getDescription(), giftCertificate.getDescription());
            boolean priceExist = Objects.equals(certificate.getPrice(), giftCertificate.getPrice());
            boolean durationExist = Objects.equals(certificate.getDuration(), giftCertificate.getDuration());
            boolean tagsExist = tagIdList.equals(tagIdsList);

            return !nameExist || !descriptionExist || !priceExist || !durationExist || !tagsExist;
        }
        return false;
    }
}

