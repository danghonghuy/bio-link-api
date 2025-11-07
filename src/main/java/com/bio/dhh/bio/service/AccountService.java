package com.bio.dhh.bio.service;

import com.bio.dhh.bio.dto.ChangeEmailDTO;
import com.bio.dhh.bio.dto.ChangePasswordDTO;
import com.bio.dhh.bio.dto.DeleteAccountDTO;
import com.bio.dhh.bio.repository.ProfileRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AccountService {

    @Autowired
    private FirebaseAuth firebaseAuth;

    @Autowired
    private ProfileRepository profileRepository;

    public void changeUserEmail(ChangeEmailDTO changeEmailDTO) {
        try {
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(changeEmailDTO.getUserId())
                    .setEmail(changeEmailDTO.getNewEmail());
            firebaseAuth.updateUser(request);
        } catch (FirebaseAuthException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi cập nhật email trên Firebase: " + e.getMessage());
        }
    }

    public void changeUserPassword(ChangePasswordDTO changePasswordDTO) {
        try {
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(changePasswordDTO.getUserId())
                    .setPassword(changePasswordDTO.getNewPassword());
            firebaseAuth.updateUser(request);
        } catch (FirebaseAuthException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi cập nhật mật khẩu trên Firebase: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteUserAccount(DeleteAccountDTO deleteAccountDTO) {
        String userId = deleteAccountDTO.getUserId();
        try {
            profileRepository.findByUserId(userId).ifPresent(profile -> {
                profileRepository.deleteById(profile.getId());
            });
            firebaseAuth.deleteUser(userId);
        } catch (FirebaseAuthException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi xóa tài khoản Firebase: " + e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi xóa dữ liệu profile: " + e.getMessage());
        }
    }
}