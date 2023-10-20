package com.module3.controller;

import com.module3.DTOs.ErrorDTO;
import com.module3.DTOs.RequestTagDTO;
import com.module3.DTOs.ResponseTagDTO;
import com.module3.model.Tag;
import com.module3.service.TagManagementService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;


@org.springframework.web.bind.annotation.RestController
public class TagsController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CertificatesController.class);

    @Autowired
    private TagManagementService tagManagementService;

    @PostMapping(value = "/tag", consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<?> postTag(@RequestBody RequestTagDTO requestTagDTO) {
        if (requestTagDTO.name() == null || requestTagDTO.name().isEmpty()) {
            ErrorDTO errorDTO = new ErrorDTO("Your request misses data");

            logger.error("error because request is not valid");
            return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
        }

        logger.debug("tag not found");

        Tag tag = new Tag(requestTagDTO.name());

        tagManagementService.saveCertificate(tag);
        ResponseTagDTO responseTagDTO = tagManagementService.returnIdByName(requestTagDTO.name());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseTagDTO);
    }

    @GetMapping(value = "/tag/{id}", consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<Tag> getTag(@PathVariable long id) {
        Optional<Tag> result = tagManagementService.findById(id);

        return result.map(tags -> {
            logger.info("Successful");
            return ResponseEntity.ok(new Tag(tags.name()));
        }).orElseGet(() -> {
            logger.error("This id doesn't exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        });
    }

    @DeleteMapping(value = "/tag/{id}", consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<Tag> deleteTag(@PathVariable long id) {

        Optional<Tag> result = tagManagementService.findById(id);
        tagManagementService.deleteTag(id);
        return result.map(tags -> ResponseEntity.ok(
                        new Tag(tags.name())
                ))
                .orElseGet(() -> {
                    logger.error("This id doesn't exist");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                });
    }

    @PutMapping(value = "/tag/{id}", consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<ResponseTagDTO> putName(@PathVariable long id, @RequestBody RequestTagDTO requestTagDTO) {

        if (tagManagementService.findById(id).isEmpty()){
            logger.error("This id doesn't exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        else {
            ResponseTagDTO responseTagDTO = tagManagementService.returnTag(id);
            ResponseTagDTO updated = new ResponseTagDTO(
                    id,
                    Objects.isNull(requestTagDTO.name()) ? responseTagDTO.name() : requestTagDTO.name() );
            tagManagementService.putNameIntoTag(updated);
            logger.debug("name put correctly");
            return ResponseEntity.status(HttpStatus.OK).body(tagManagementService.returnTag(id));
        }
    }
}
