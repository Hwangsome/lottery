package com.clinic.lottery.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 */
@Slf4j
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expireSeconds;

    public JwtUtil(String secret, long expireSeconds) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expireSeconds = expireSeconds;
    }

    /**
     * 生成用户 Token
     */
    public String generateToken(Long userId, String openid) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("openid", openid);
        claims.put("type", "user");
        return createToken(claims, String.valueOf(userId));
    }

    /**
     * 生成管理员 Token
     */
    public String generateAdminToken(Long adminId, String username, Integer role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", adminId);
        claims.put("username", username);
        claims.put("role", role);
        claims.put("type", "admin");
        return createToken(claims, String.valueOf(adminId));
    }

    /**
     * 创建 Token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expireSeconds * 1000);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 解析 Token
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从 Token 中获取用户 ID
     */
    public Long getUserId(String token) {
        try {
            Claims claims = parseToken(token);
            Object userId = claims.get("userId");
            if (userId instanceof Integer) {
                return ((Integer) userId).longValue();
            } else if (userId instanceof Long) {
                return (Long) userId;
            }
            return Long.parseLong(String.valueOf(userId));
        } catch (Exception e) {
            log.error("解析 Token 获取用户ID失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从 Token 中获取管理员 ID
     */
    public Long getAdminId(String token) {
        try {
            Claims claims = parseToken(token);
            Object adminId = claims.get("adminId");
            if (adminId instanceof Integer) {
                return ((Integer) adminId).longValue();
            } else if (adminId instanceof Long) {
                return (Long) adminId;
            }
            return Long.parseLong(String.valueOf(adminId));
        } catch (Exception e) {
            log.error("解析 Token 获取管理员ID失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取 Token 类型
     */
    public String getTokenType(String token) {
        try {
            Claims claims = parseToken(token);
            return (String) claims.get("type");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 验证 Token 是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token 已过期");
            return false;
        } catch (Exception e) {
            log.warn("Token 无效: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 判断 Token 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return true;
        }
    }
}
