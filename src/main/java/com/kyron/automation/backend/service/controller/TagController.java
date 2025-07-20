package com.kyron.automation.backend.service.controller;

import com.kyron.automation.backend.service.dto.TagDto;
import com.kyron.automation.backend.service.service.TagService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    public ResponseEntity<TagDto> createTag(@Valid @RequestBody TagDto tagDto) {
        return new ResponseEntity<>(tagService.createTag(tagDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TagDto>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDto> getTag(@PathVariable Long id) {
        return ResponseEntity.ok(tagService.getTag(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagDto> updateTag(
            @PathVariable Long id,
            @Valid @RequestBody TagDto tagDto) {
        return ResponseEntity.ok(tagService.updateTag(id, tagDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
