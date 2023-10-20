package com.module3.repository;

import com.module3.DTOs.ResponseTagDTO;
import com.module3.model.Tag;

import java.util.Optional;

public interface TagsRepository {

    int save(Tag tag);

    ResponseTagDTO returnIdByName(String name);

    Optional<Tag> findById(Long id);

    ResponseTagDTO returnTag(Long id);

    int deleteById(Long id);
    int updateName(long Id, String name);

    Optional<ResponseTagDTO> findByName(String name);
}
