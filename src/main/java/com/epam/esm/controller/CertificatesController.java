package com.epam.esm.controller;

import com.epam.esm.Dto.GiftCertificate.GiftCertificateRequestDTO;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.service.GiftCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//TODO create two profiles (dev and prod)
@RestController
public class CertificatesController {

    @Autowired
    private GiftCertificateService giftCertificateService;

    @PostMapping(value = "/certificate", consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<?> postCertificate(@RequestBody GiftCertificateRequestDTO requestDTO) {
        GiftCertificate giftCertificate = new GiftCertificate(
                requestDTO.name(),
                requestDTO.description(),
                requestDTO.price(),
                requestDTO.duration()
        );
        return giftCertificateService.saveGiftCertificate(giftCertificate, requestDTO.tagIds());
    }

    @GetMapping(value = "/certificate/{id}", consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<?> getCertificateById(@PathVariable long id) {
        return giftCertificateService.getGiftCertificateById(id);
    }

    @GetMapping(value = "/certificate/filter", produces = {"application/json"})
    public ResponseEntity<?> getFilteredCertificates(
            @RequestParam(required = false) String tagName,
            @RequestParam(required = false) String searchWord,
            @RequestParam(required = false) String nameOrder,
            @RequestParam(required = false) String createDateOrder) {

        return giftCertificateService.getFilteredCertificates(
                tagName,
                searchWord,
                nameOrder,
                createDateOrder);
    }

    @DeleteMapping(value = "/certificate/{id}", consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<?> deleteCertificateById(@PathVariable long id) {
        return giftCertificateService.deleteGiftCertificateById(id);
    }

    @PutMapping(value = "/certificate/{id}", consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<?> updateCertificate(@PathVariable long id, @RequestBody GiftCertificateRequestDTO requestDTO) {
        GiftCertificate giftCertificate = new GiftCertificate(
                requestDTO.name(),
                requestDTO.description(),
                requestDTO.price(),
                requestDTO.duration()
        );
        return giftCertificateService.updateGiftCertificate(id, giftCertificate, requestDTO.tagIds());
    }

    /* Format of POST
            "name" : "name",
            "description" : "description",
            "price" : 1.0,
            "duration" : 1,
            "tagIds" : [1,2]
     */
}
