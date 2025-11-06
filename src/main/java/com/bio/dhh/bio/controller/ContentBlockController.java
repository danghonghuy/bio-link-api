package com.bio.dhh.bio.controller;

import com.bio.dhh.bio.model.ClickLog;
import com.bio.dhh.bio.model.ContentBlock;
import com.bio.dhh.bio.model.Profile;
import com.bio.dhh.bio.repository.ClickLogRepository;
import com.bio.dhh.bio.repository.ContentBlockRepository;
import com.bio.dhh.bio.repository.ProfileRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/blocks")
public class ContentBlockController {

    private final ContentBlockRepository blockRepository;
    private final ProfileRepository profileRepository;
    private final ClickLogRepository clickLogRepository;

    public ContentBlockController(ContentBlockRepository br, ProfileRepository pr, ClickLogRepository cr) {
        this.blockRepository = br;
        this.profileRepository = pr;
        this.clickLogRepository = cr;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<ContentBlock> addBlock(@PathVariable String userId, @RequestBody ContentBlock block) {
        Profile profile = profileRepository.findByUserId(userId).orElse(null);
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }

        block.setProfile(profile);
        int lastOrder = profile.getBlocks().size();
        block.setBlockOrder(lastOrder);

        ContentBlock savedBlock = blockRepository.save(block);
        return ResponseEntity.ok(savedBlock);
    }

    @PutMapping("/{blockId}")
    public ResponseEntity<ContentBlock> updateBlock(@PathVariable Long blockId, @RequestBody ContentBlock updatedBlockData) {
        ContentBlock block = blockRepository.findById(blockId).orElse(null);
        if (block == null) {
            return ResponseEntity.notFound().build();
        }

        block.setType(updatedBlockData.getType());
        block.setData(updatedBlockData.getData());

        return ResponseEntity.ok(blockRepository.save(block));
    }

    @DeleteMapping("/{blockId}")
    public ResponseEntity<Void> deleteBlock(@PathVariable Long blockId) {
        if (!blockRepository.existsById(blockId)) {
            return ResponseEntity.notFound().build();
        }
        blockRepository.deleteById(blockId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reorder/{userId}")
    public ResponseEntity<Void> reorderBlocks(@PathVariable String userId, @RequestBody List<Long> orderedBlockIds) {
        Profile profile = profileRepository.findByUserId(userId).orElse(null);
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }

        List<ContentBlock> blocks = profile.getBlocks();
        Map<Long, ContentBlock> blockMap = new HashMap<>();
        for (ContentBlock block : blocks) {
            blockMap.put(block.getId(), block);
        }

        for (int i = 0; i < orderedBlockIds.size(); i++) {
            ContentBlock block = blockMap.get(orderedBlockIds.get(i));
            if (block != null) {
                block.setBlockOrder(i);
                blockRepository.save(block);
            }
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{blockId}/click")
    public ResponseEntity<Void> recordClick(@PathVariable Long blockId) {
        if (!blockRepository.existsById(blockId)) {
            return ResponseEntity.notFound().build();
        }

        ClickLog log = new ClickLog();
        log.setBlockId(blockId);
        clickLogRepository.save(log);

        return ResponseEntity.ok().build();
    }
}