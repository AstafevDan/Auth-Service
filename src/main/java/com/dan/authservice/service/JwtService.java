package com.dan.authservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Сервис для работы с JWT.
 * Позволяет генерировать, валидировать и извлекать данные из токена.
 *
 * @author Даниил Астафьев
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    /**
     * Секретный ключ.
     */
    @Value("${app.security.jwt.secret-key}")
    private String secretKey;

    /**
     * Время жизни токена в ms.
     */
    @Value("${app.security.jwt.expiration}")
    private Long jwtExpiration;

    /**
     * Извлекает имя пользователя (email) из токена.
     *
     * @param jwt JWT токен
     * @return имя пользователя (email)
     */
    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    /**
     * Генерирует JWT токен для пользователя.
     *
     * @param userDetails данные пользователя
     * @return сгенерированный JWT токен
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Генерирует JWT токен с дополнительными claims для пользователя.
     *
     * @param extraClaims дополнительные данные (claims) для включения в токен
     * @param userDetails данные пользователя
     * @return сгенерированный JWT токен
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Проверяет валидность JWT токена для указанного пользователя.
     *
     * @param token       JWT токен
     * @param userDetails данные пользователя для проверки
     * @return true если токен валиден для данного пользователя, иначе false
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Извлекает конкретный claim из JWT токена.
     *
     * @param <T>            тип возвращаемого claim
     * @param jwt            JWT токен
     * @param claimsResolver функция для извлечения конкретного claim
     * @return значение запрошенного claim
     */
    private <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwt);
        return claimsResolver.apply(claims);
    }

    /**
     * Извлекает все claims из JWT токена.
     *
     * @param jwt JWT токен
     * @return объект Claims со всеми claims токена
     */
    private Claims extractAllClaims(String jwt) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    /**
     * Проверяет, истек ли срок действия JWT токена.
     *
     * @param token JWT токен
     * @return true если токен просрочен, иначе false
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Извлекает дату истечения срока действия токена.
     *
     * @param token JWT токен
     * @return дата истечения срока действия
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Строит JWT токен на основе переданных параметров.
     *
     * @param extraClaims   дополнительные claims
     * @param userDetails   данные пользователя
     * @param jwtExpiration время жизни токена в миллисекундах
     * @return сгенерированный JWT токен
     */
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, Long jwtExpiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Создает ключ для подписи на основе секретного ключа.
     *
     * @return ключ для подписи JWT
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
