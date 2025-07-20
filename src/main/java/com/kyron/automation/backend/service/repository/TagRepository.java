package com.kyron.automation.backend.service.repository;

import com.kyron.automation.backend.service.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByNameContainingIgnoreCase(String name);
    Set<Tag> findByIdIn(Set<Long> ids);
}
