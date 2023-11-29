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
import static com.epam.esm.logs.BooleanFlags.*;

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
        GiftCertificate saveGiftCertificate = giftCertificateTagRepository.saveGiftCertificate(giftCertificate, tagIdsList);

        certificateSuccesfullySaved = saveGiftCertificate != null;
        if (certificateSuccesfullySaved) {
            GiftCertificate response = giftCertificateTagRepository.getGiftCertificateByName(giftCertificate.getName());

            certificateExists = response.equals(saveGiftCertificate);
            if (certificateExists)
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            else {
                ErrorDTO errorResponse = new ErrorDTO(CERTIFICATE_COULD_NOT_BE_SAVED, 500);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
        } else {
            GiftCertificate giftCertificateByName = giftCertificateTagRepository.getGiftCertificateByName(giftCertificate.getName());
            if (giftCertificateByName != null) {
                Long idFound = giftCertificateByName.getId();

                ErrorDTO errorResponse = new ErrorDTO(CERTIFICATE_ALREADY_EXISTS.formatted(idFound), CERTIFICATE_FOUND);
                return ResponseEntity.status(HttpStatus.FOUND).body(errorResponse);
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

    }

    public ResponseEntity getGiftCertificateById(Long giftCertificateId) {

        GiftCertificate giftCertificate = giftCertificateTagRepository.getGiftCertificateById(giftCertificateId);

        certificateExists = giftCertificate != null;
        if (certificateExists) {

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
        certificateSuccessfullyDeleted = giftCertificateTagRepository.deleteGiftCertificate(giftCertificateId);

        return certificateSuccessfullyDeleted ?
            ResponseEntity.status(HttpStatus.FOUND).body(null) :
        ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                new ErrorDTO(
                        CERTIFICATE_WITH_ID_NOT_FOUND.formatted(giftCertificateId),
                        Codes.CERTIFICATE_NOT_FOUND));
    }

//    public ResponseEntity<?> updateGiftCertificate(long id, GiftCertificate giftCertificate, List<Long> tagIdsList) {
//
//        //TODO extract methods from here to make the code cleaner
//        ResponseEntity<ErrorDTO> requestValidationMessage = validateCertificateRequest(giftCertificate);
//        log.info("Check if request has valid parameters");
//        if (requestValidationMessage != null) return requestValidationMessage;
//
//        log.info("Check if request already exists or if id is associated to any certificate. " +
//                "If certificate already exists, don't execute update");
//        //eliminate duplicated
//        tagIdsList = tagIdsList.stream().distinct().collect(Collectors.toList());
//
//        if (validateUpdate(id, giftCertificate, tagIdsList)) {
//
//            log.info("Certificate requested has one or more new parameters, so we update it");
//            GiftCertificate responseDTO = giftCertificateTagRepository.updateGiftCertificate(id, giftCertificate, tagIdsList);
//
//            log.info("Check if it has been updated and response exists");
//            if (responseDTO != null) {
//                log.info("updated succesfully, return OK");
//                return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
//            } else {
//                log.info("has not been updated by some reason");
//
//                log.info("Check if the problem is a not found id");
//                if (giftCertificateTagRepository.getGiftCertificateById(id) == null) {
//                    String message = CERTIFICATE_WITH_ID_NOT_FOUND.formatted(id);
//
//                    ErrorDTO errorResponse = new ErrorDTO(message, Codes.CERTIFICATE_NOT_FOUND);
//                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
//                } else {
//                    log.info("There is a problem with parameters. There are non existing tags");
//
//                    String message = ("There are non existing tags");
//
//                    ErrorDTO errorResponse = new ErrorDTO(message, TAG_NOT_FOUND);
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
//                }
//            }
//        } else {
//            log.info("certificate requested already exists, so we don't update and instead return found");
//
//            log.info("get the matching certificate using the parameter name and get id of matched certificate");
//            GiftCertificate certificate = giftCertificateTagRepository.getGiftCertificateByName(giftCertificate.getName());
//            Long idFound = certificate.getId();
//            String message = CERTIFICATE_ALREADY_EXISTS.formatted(idFound);
//            ErrorDTO errorResponse = new ErrorDTO(message, Codes.CERTIFICATE_FOUND);
//            return ResponseEntity.status(HttpStatus.FOUND).body(errorResponse);
//
//        }
//    }

    public ResponseEntity<?> updateGiftCertificate(long id, GiftCertificate giftCertificate, List<Long> tagIdsList) {
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
        String validationMessage = CertificateValidator.validateRequest(giftCertificate);

        certificateRequestIsValid = validationMessage.equals("Valid");
        if (!certificateRequestIsValid) {
            ErrorDTO errorResponse = new ErrorDTO(validationMessage, CERTIFICATE_BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        return null;
    }

    private boolean validateUpdate(long id, GiftCertificate giftCertificate, List<Long> tagIdsList) {

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

