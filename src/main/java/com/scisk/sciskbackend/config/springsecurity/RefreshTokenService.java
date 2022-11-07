package com.scisk.sciskbackend.config.springsecurity;

import com.scisk.sciskbackend.datasourceentity.RefreshToken;
import com.scisk.sciskbackend.datasourceentity.UserDS;
import com.scisk.sciskbackend.repository.RefreshTokenRepository;
import com.scisk.sciskbackend.repository.UserRepository;
import com.scisk.sciskbackend.service.CounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${scisk.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private final CounterService counterService;

    public RefreshTokenService(CounterService counterService) {
        this.counterService = counterService;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(counterService.getNextSequence("refreshtoken"));
        refreshToken.setUser(UserDS.map(userRepository.findById(userId).get()));
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "refreshtoken.expired");
        }
        return token;
    }
}
