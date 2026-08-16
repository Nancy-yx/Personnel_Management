package com.personnel.util;

import com.personnel.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Component
@RequiredArgsConstructor
public class JwtUtil {
    
    private final JwtConfig jwtConfig;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * 生成Token
     */
    public String generateToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtConfig.getExpiration());
        
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * 解析Token
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * 验证Token
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 从Token中获取用户ID
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }
    
    /**
     * 从Token中获取用户名
     */
    public String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }
    
    /**
     * 从Token中获取角色
     */
    public String getRole(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", String.class);
    }
    
    /**
     * 从Token中获取过期时间
     */
    public Date getExpiration(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration();
    }
    
    /**
     * 判断Token是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpiration(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
