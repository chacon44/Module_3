package com.epam.esm.service;

import com.epam.esm.Dto.Errors.ErrorDTO;
import com.epam.esm.Dto.GiftCertificate.GiftCertificateRequestDTO;
import com.epam.esm.Dto.GiftCertificate.GiftCertificateResponseDTO;
import com.epam.esm.exceptions.Codes;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.repository.GiftCertificateTagRepository;
import com.epam.esm.validators.CertificateValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.epam.esm.exceptions.Codes.*;
import static com.epam.esm.exceptions.Messages.*;
import static com.epam.esm.logs.BooleanFlags.*;

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

        Optional<Long> id = giftCertificateTagRepository.saveGiftCertificate(giftCertificateRequestDTO);

        certificateSuccesfullySaved = id.isPresent();
        if (!certificateSuccesfullySaved) {
            ErrorDTO errorResponse = new ErrorDTO(CERTIFICATE_ALREADY_EXISTS, CERTIFICATE_FOUND);
            return ResponseEntity.status(HttpStatus.FOUND).body(errorResponse);
        }

        Optional<GiftCertificate> response = giftCertificateTagRepository.getGiftCertificateById(id.get());

        certificateExists = response.isPresent();
        if (certificateExists)
            return new ResponseEntity<>(response.get(), HttpStatus.CREATED);
        else {
            ErrorDTO errorResponse = new ErrorDTO(CERTIFICATE_COULD_NOT_BE_SAVED, 500);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    public ResponseEntity<?> getGiftCertificateById(Long giftCertificateId) {

        Optional<GiftCertificate> optionalCertificate = giftCertificateTagRepository.getGiftCertificateById(giftCertificateId);

        certificateExists = optionalCertificate.isPresent();
        if (certificateExists) {
            GiftCertificate giftCertificate = optionalCertificate.get();

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
        certificateSuccessfullyDeleted = giftCertificateTagRepository.deleteGiftCertificateById(giftCertificateId);
        
        if (certificateSuccessfullyDeleted) {
            giftCertificateTagRepository.deleteCertificateFromJoinTable(giftCertificateId);
            return ResponseEntity.status(HttpStatus.FOUND).body(null);
        }

        String message = CERTIFICATE_WITH_ID_NOT_FOUND.formatted(giftCertificateId);

        ErrorDTO errorResponse = new ErrorDTO(message, Codes.CERTIFICATE_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

    }

    public ResponseEntity<?> updateGiftCertificate(long id, GiftCertificateRequestDTO giftCertificateRequestDTO) {

        //TODO allow updating with non existing tags. Update only existing ones or don't update nothing
        ResponseEntity<ErrorDTO> requestValidationMessage = validateCertificateRequest(giftCertificateRequestDTO);
        if (requestValidationMessage != null) return requestValidationMessage;

        boolean nameExist = giftCertificateTagRepository.getGiftCertificateByName(giftCertificateRequestDTO.name()).isPresent();
        if (!nameExist) {

            Optional<GiftCertificate> responseDTO = giftCertificateTagRepository.updateGiftCertificate(id, giftCertificateRequestDTO);
            if (responseDTO.isPresent())
                return ResponseEntity.status(HttpStatus.FOUND).body(convertToDTO(responseDTO.get()));
            else {
                String message = CERTIFICATE_WITH_ID_NOT_FOUND.formatted(id);

                ErrorDTO errorResponse = new ErrorDTO(message, Codes.CERTIFICATE_NOT_FOUND);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        }else {

            Long idFound = giftCertificateTagRepository.getGiftCertificateByName(giftCertificateRequestDTO.name()).get().getId();
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

    public GiftCertificateResponseDTO convertToDTO(GiftCertificate giftCertificate) {

        return new GiftCertificateResponseDTO(
                giftCertificate.getId(),
                giftCertificate.getName(),
                giftCertificate.getDescription(),
                giftCertificate.getPrice(),
                giftCertificate.getDuration(),
                giftCertificate.getCreateDate(),
                giftCertificate.getLastUpdateDate(),
                giftCertificate.getTags()
        );
    }
}

