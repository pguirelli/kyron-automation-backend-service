package com.kyron.automation.backend.service.service;

import com.kyron.automation.backend.service.dto.TagDto;
import com.kyron.automation.backend.service.model.Tag;
import com.kyron.automation.backend.service.repository.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional
    public TagDto createTag(TagDto tagDto) {
        Tag tag = new Tag();
        BeanUtils.copyProperties(tagDto, tag);
        Tag savedTag = tagRepository.save(tag);
        return convertToDto(savedTag);
    }

    public List<TagDto> getAllTags() {
        return tagRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TagDto getTag(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with id: " + id));
        return convertToDto(tag);
    }

    @Transactional
    public TagDto updateTag(Long id, TagDto tagDto) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with id: " + id));
        BeanUtils.copyProperties(tagDto, tag, "id");
        Tag updatedTag = tagRepository.save(tag);
        return convertToDto(updatedTag);
    }

    @Transactional
    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new EntityNotFoundException("Tag not found with id: " + id);
        }
        tagRepository.deleteById(id);
    }

    private TagDto convertToDto(Tag tag) {
        TagDto dto = new TagDto();
        BeanUtils.copyProperties(tag, dto);
        return dto;
    }
}
