package com.hmd.learnredis.services;

import com.hmd.learnredis.exceptions.RefreshTokenNotFoundException;
import com.hmd.learnredis.exceptions.TokenExpiredException;
import com.hmd.learnredis.models.RefreshToken;
import com.hmd.learnredis.models.User;
import com.hmd.learnredis.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${jwt.refreshExpiration}")
    private Long refreshExpiration;

    @Transactional
    public String createRefreshToken(User user) {
        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);
        RefreshToken savedRefreshToken = refreshTokenRepository.save(RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusSeconds(refreshExpiration))
                .user(user)
                .build());
        return savedRefreshToken.getToken();
    }

    @Transactional
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token).orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token not found"));
    }

    @Transactional
    public boolean verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenExpiredException("Refresh token has expired! Please make a new sign in request");
        }
        return true;
    }

    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
