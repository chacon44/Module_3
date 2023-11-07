package com.epam.esm.service;

import com.epam.esm.DTOs.ResponseTagDTO;
import com.epam.esm.model.Tag;
import com.epam.esm.repository.JdbcTagsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class TagManagementService {
    @Autowired
    JdbcTagsRepository jdbcTagsRepository;
    private static final Logger logger = LoggerFactory.getLogger(CertificateManagementService.class);

    public void saveCertificate(Tag tag) {

        jdbcTagsRepository.save(tag);
    }

    public ResponseTagDTO returnIdByName(String name) {

        return jdbcTagsRepository.returnIdByName(name);
    }
    public ResponseTagDTO returnTag(long id) {

        return jdbcTagsRepository.returnTag(id);
    }
    public void deleteTag(long id) {

        jdbcTagsRepository.deleteById(id);
    }

    public Optional<Tag> findById(long id) {

        return jdbcTagsRepository.findById(id);
    }

    public void putNameIntoTag(ResponseTagDTO responseTagDTO) {

        jdbcTagsRepository.updateName(responseTagDTO.id(), responseTagDTO.name());
    }
}
