package com.example.springsecuirityclient.repository;

import com.example.springsecuirityclient.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends
        JpaRepository<PasswordResetToken,Long> {

    PasswordResetToken findByToken(String token);
}
