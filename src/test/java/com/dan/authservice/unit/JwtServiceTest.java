package com.dan.authservice.unit;

import com.dan.authservice.entity.User;
import com.dan.authservice.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private final SecretKey KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final String SECRET_KEY = Base64.getEncoder().encodeToString(KEY.getEncoded());
    private final Long EXPIRATION_TIME = 1800000L;

    @InjectMocks
    private JwtService jwtService;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", EXPIRATION_TIME);

        userDetails = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("password")
                .username("test")
                .emailVerified(true)
                .build();
    }

    @Test
    void extractUsername_WhenValidToken_ThenReturnUsername() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);
        assertNotNull(username);
        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    void generateTokenWithExtraClaims() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("claimKey", "claimValue");

        String token = jwtService.generateToken(extraClaims, userDetails);
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertNotNull(claims);
        assertEquals("claimValue", claims.get("claimKey"));
    }

    @Test
    void isTokenValid_WhenValidToken_ThenReturnTrue() {
        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_WhenInvalidUser_ThenReturnFalse() {
        String token = jwtService.generateToken(userDetails);
        UserDetails otherUser = User.builder()
                .id(2L)
                .email("test2@test.com")
                .password("password")
                .username("test2")
                .emailVerified(true)
                .build();

        assertFalse(jwtService.isTokenValid(token, otherUser));
    }

    @Test
    void isTokenValid_WhenExpiredToken_ThenReturnFalse() {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1L);
        String token = jwtService.generateToken(userDetails);

        boolean isValid;
        try {
            isValid = jwtService.isTokenValid(token, userDetails);
        } catch (ExpiredJwtException e) {
            isValid = false;
        }

        assertFalse(isValid);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
