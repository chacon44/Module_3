package com.epam.esm.controller;

import com.epam.esm.DTOs.ErrorDTO;
import com.epam.esm.DTOs.RequestDTO;
import com.epam.esm.DTOs.ResponseDTO;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.service.CertificateManagementService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;


@org.springframework.web.bind.annotation.RestController
public class CertificatesController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CertificatesController.class);

    @Autowired
    private CertificateManagementService certificateManagementService;

    @PostMapping(value = "/certificate", consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<?> postCertificate(@RequestBody RequestDTO requestDTO) {
        if (requestDTO.name() == null || requestDTO.name().isEmpty() ||
                requestDTO.description() == null || requestDTO.description().isEmpty() ||
                requestDTO.price() < 0) {
            ErrorDTO errorDTO = new ErrorDTO("Your request misses data");

            logger.error("error because request is not valid");
            return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
        }

        logger.debug("certificate not found");

        GiftCertificate giftCertificate = new GiftCertificate(requestDTO.getCertificateName(), requestDTO.description(), requestDTO.price());

        certificateManagementService.saveCertificate(giftCertificate);
        ResponseDTO responseDTO = certificateManagementService.returnIdByQuestion(requestDTO.name());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping(value = "/certificate/{id}", consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<GiftCertificate> getCertificate(@PathVariable long id) {
        Optional<GiftCertificate> result = certificateManagementService.findById(id);

        return result.map(certificates -> {
            logger.info("Successful");
            return ResponseEntity.ok(
                    new GiftCertificate(
                    certificates.getCertificateName(),
                    certificates.getCertificateDescription(),
                    certificates.getCertificatePrice())
            );
        }).orElseGet(() -> {
            logger.error("This id doesn't exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        });
    }

    @DeleteMapping(value = "/certificate/{id}", consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<GiftCertificate> deleteCertificate(@PathVariable long id) {

        Optional<GiftCertificate> result = certificateManagementService.findById(id);
        certificateManagementService.deleteCertificate(id);
        return result.map(certificates -> ResponseEntity.ok(
                        new GiftCertificate(certificates.getCertificateName(), certificates.getCertificateDescription(), certificates.getCertificatePrice())
                ))
                .orElseGet(() -> {
                    logger.error("This id doesn't exist");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                });
    }

    @PutMapping(value = "/certificate/{id}", consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<ResponseDTO> putPrice(@PathVariable long id, @RequestBody RequestDTO requestDTO) {

        if (certificateManagementService.findById(id).isEmpty()){
            logger.error("This id doesn't exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        else {
            ResponseDTO responseDTO = certificateManagementService.returnCertificate(id);
            ResponseDTO updated = new ResponseDTO(
                    id,
                    Objects.isNull(requestDTO.name()) ? responseDTO.name() : requestDTO.name(),
                    Objects.isNull(requestDTO.description()) ? responseDTO.description() : requestDTO.description(),
                    Objects.isNull(requestDTO.price()) ? responseDTO.price() : requestDTO.price(),
                    responseDTO.duration(),
                    responseDTO.createUpdate(),
                    responseDTO.LastUpdateDate());
            certificateManagementService.putPriceIntoCertificate(updated);
            logger.debug("price put correctly");
            return ResponseEntity.status(HttpStatus.OK).body(certificateManagementService.returnCertificate(id));
        }
    }
}
