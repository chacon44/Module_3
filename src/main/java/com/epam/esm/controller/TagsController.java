package com.epam.esm.controller;

import com.epam.esm.Dto.Tag.TagRequestDTO;
import com.epam.esm.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TagsController {

    @Autowired
    private TagService tagService;

    @PostMapping(value = "/tag", consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<?> postTag(@RequestBody TagRequestDTO requestDTO) {
        return tagService.saveTag(requestDTO.name());
    }
    @GetMapping(value = "/tag/{id}", consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<?> getTagById(@PathVariable long id) {
        return tagService.getTagById(id);
    }
    @DeleteMapping(value = "/tag/{id}", consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<?> deleteTagById(@PathVariable long id) {
        return tagService.deleteTagById(id);
    }
}
