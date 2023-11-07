package com.epam.esm.repository;

import com.epam.esm.DTOs.ResponseTagDTO;
import com.epam.esm.model.Tag;
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
