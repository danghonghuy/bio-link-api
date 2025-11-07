package com.bio.dhh.bio.controller;

import com.bio.dhh.bio.dto.ChangeEmailDTO;
import com.bio.dhh.bio.dto.ChangePasswordDTO;
import com.bio.dhh.bio.dto.DeleteAccountDTO;
import com.bio.dhh.bio.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/change-email")
    public ResponseEntity<Map<String, String>> changeEmail(@Valid @RequestBody ChangeEmailDTO changeEmailDTO) {
        accountService.changeUserEmail(changeEmailDTO);
        return ResponseEntity.ok(Map.of("message", "Yêu cầu thay đổi email đã được gửi. Vui lòng kiểm tra email để xác thực."));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        accountService.changeUserPassword(changePasswordDTO);
        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công."));
    }

    @PostMapping("/delete")
    public ResponseEntity<Map<String, String>> deleteAccount(@Valid @RequestBody DeleteAccountDTO deleteAccountDTO) {
        accountService.deleteUserAccount(deleteAccountDTO);
        return ResponseEntity.ok(Map.of("message", "Tài khoản đã được xóa thành công."));
    }
}