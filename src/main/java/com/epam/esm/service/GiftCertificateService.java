package com.epam.esm.service;

import com.epam.esm.Dto.Errors.ErrorDTO;
import com.epam.esm.Dto.GiftCertificate.GiftCertificateRequestDTO;
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

    public ResponseEntity<?> saveGiftCertificate(GiftCertificateRequestDTO giftCertificateRequestDTO) {

        ResponseEntity<ErrorDTO> requestValidationMessage = validateCertificateRequest(giftCertificateRequestDTO);
        if (requestValidationMessage != null) return requestValidationMessage;

        GiftCertificate giftCertificate = new GiftCertificate(
                giftCertificateRequestDTO.name(),
                giftCertificateRequestDTO.description(),
                giftCertificateRequestDTO.price(),
                giftCertificateRequestDTO.duration()
        );

        List<Long> tagList = giftCertificateRequestDTO.tagIds();
        Long id = giftCertificateTagRepository.saveGiftCertificate(giftCertificate, new Date(), tagList);

        certificateSuccesfullySaved = id != null;
        if (!certificateSuccesfullySaved) {
            GiftCertificate giftCertificateByName = giftCertificateTagRepository.getGiftCertificateByName(giftCertificateRequestDTO.name());
            if (giftCertificateByName != null) {
                Long idFound = giftCertificateByName.getId();

                ErrorDTO errorResponse = new ErrorDTO(CERTIFICATE_ALREADY_EXISTS.formatted(idFound), CERTIFICATE_FOUND);
                return ResponseEntity.status(HttpStatus.FOUND).body(errorResponse);
            }
        }

        if (id != null) {
            GiftCertificate response = giftCertificateTagRepository.getGiftCertificateById(id);

            certificateExists = response != null;
            if (certificateExists)
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            else {
                ErrorDTO errorResponse = new ErrorDTO(CERTIFICATE_COULD_NOT_BE_SAVED, 500);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

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

        if (certificateSuccessfullyDeleted) {
            giftCertificateTagRepository.deleteCertificateFromJoinTable(giftCertificateId);
            return ResponseEntity.status(HttpStatus.FOUND).body(null);
        }

        String message = CERTIFICATE_WITH_ID_NOT_FOUND.formatted(giftCertificateId);

        ErrorDTO errorResponse = new ErrorDTO(message, Codes.CERTIFICATE_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(errorResponse);
    }

    public ResponseEntity<?> updateGiftCertificate(long id, GiftCertificateRequestDTO giftCertificateRequestDTO) {

        //TODO extract methods from here to make the code cleaner
        ResponseEntity<ErrorDTO> requestValidationMessage = validateCertificateRequest(giftCertificateRequestDTO);
        log.info("Check if request has valid parameters");
        if (requestValidationMessage != null) return requestValidationMessage;

        log.info("Check if request already exists or if id is associated to any certificate. " +
                "If certificate already exists, don't execute update");
        if (validateUpdate(id, giftCertificateRequestDTO)) {
            GiftCertificate giftCertificate = new GiftCertificate(
                    giftCertificateRequestDTO.name(),
                    giftCertificateRequestDTO.description(),
                    giftCertificateRequestDTO.price(),
                    giftCertificateRequestDTO.duration()
            );
            List<Long> tagList = giftCertificateRequestDTO.tagIds();

            log.info("Certificate requested has one or more new parameters, so we update it");
            GiftCertificate responseDTO = giftCertificateTagRepository.updateGiftCertificate(id, giftCertificate, tagList);

            log.info("Check if it has been updated and response exists");
            if (responseDTO != null) {
                log.info("updated succesfully, return OK");
                return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
            } else {
                log.info("has not been updated by some reason");

                log.info("Check if the problem is a not found id");
                if (giftCertificateTagRepository.getGiftCertificateById(id) == null) {
                    String message = CERTIFICATE_WITH_ID_NOT_FOUND.formatted(id);

                    ErrorDTO errorResponse = new ErrorDTO(message, Codes.CERTIFICATE_NOT_FOUND);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                } else {
                    log.info("There is a problem with parameters. There are non existing tags");

                    String message = ("There are non existing tags");

                    ErrorDTO errorResponse = new ErrorDTO(message, TAG_NOT_FOUND);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
                }
            }
        } else {
            log.info("certificate requested already exists, so we don't update and instead return found");

            log.info("get the matching certificate using the parameter name and get id of matched certificate");
            GiftCertificate certificate = giftCertificateTagRepository.getGiftCertificateByName(giftCertificateRequestDTO.name());
            Long idFound = certificate.getId();
            String message = CERTIFICATE_ALREADY_EXISTS.formatted(idFound);
            ErrorDTO errorResponse = new ErrorDTO(message, Codes.CERTIFICATE_FOUND);
            return ResponseEntity.status(HttpStatus.FOUND).body(errorResponse);

        }
    }

    private ResponseEntity<ErrorDTO> validateCertificateRequest(GiftCertificateRequestDTO giftCertificateRequestDTO) {
        String validationMessage = CertificateValidator.validateRequest(giftCertificateRequestDTO);

        certificateRequestIsValid = validationMessage.equals("Valid");
        if (!certificateRequestIsValid) {
            ErrorDTO errorResponse = new ErrorDTO(validationMessage, CERTIFICATE_BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        return null;
    }

    private boolean validateUpdate(long id, GiftCertificateRequestDTO giftCertificateRequestDTO) {

        GiftCertificate certificate = giftCertificateTagRepository.getGiftCertificateById(id);

        if (certificate != null) {

            List<Long> tagIdList = giftCertificateTagRepository.tagsByCertificateId(id);
            boolean nameExist = Objects.equals(certificate.getName(), giftCertificateRequestDTO.name());
            boolean descriptionExist = Objects.equals(certificate.getDescription(), giftCertificateRequestDTO.description());
            boolean priceExist = Objects.equals(certificate.getPrice(), giftCertificateRequestDTO.price());
            boolean durationExist = Objects.equals(certificate.getDuration(), giftCertificateRequestDTO.duration());
            boolean tagsExist = tagIdList.equals(giftCertificateRequestDTO.tagIds());

            return !nameExist || !descriptionExist || !priceExist || !durationExist || !tagsExist;
        }
        return false;
    }
}

