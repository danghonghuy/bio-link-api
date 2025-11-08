package com.bio.dhh.bio.service;

import com.bio.dhh.bio.exception.ResourceNotFoundException;
import com.bio.dhh.bio.model.ContentBlock;
import com.bio.dhh.bio.repository.ContentBlockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Quan trọng!

@Service
public class ContentBlockService {

    private final ContentBlockRepository blockRepository;

    @Autowired
    public ContentBlockService(ContentBlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }

    // Đánh dấu phương thức này là một giao dịch
    @Transactional
    public ContentBlock updateBlockStatus(Long blockId, boolean isEnabled) {
        // Tìm block, nếu không thấy sẽ văng ra lỗi
        ContentBlock block = blockRepository.findById(blockId)
                .orElseThrow(() -> new ResourceNotFoundException("ContentBlock not found with id: " + blockId));

        // Cập nhật trạng thái
        block.setEnabled(isEnabled);

        // Lưu lại block. Vì có @Transactional, thay đổi này sẽ được commit vào DB
        return blockRepository.save(block);
    }
}