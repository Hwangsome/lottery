package com.clinic.lottery.gateway.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * 网关鉴权主体：用户或管理员，用于 Spring Security 上下文
 */
@Getter
public class GatewayAuthPrincipal implements UserDetails {

    public static final String TYPE_USER = "user";
    public static final String TYPE_ADMIN = "admin";

    private final Long userId;
    private final Long adminId;
    private final String type;

    private GatewayAuthPrincipal(Long userId, Long adminId, String type) {
        this.userId = userId;
        this.adminId = adminId;
        this.type = type;
    }

    public static GatewayAuthPrincipal user(Long userId) {
        return new GatewayAuthPrincipal(userId, null, TYPE_USER);
    }

    public static GatewayAuthPrincipal admin(Long adminId) {
        return new GatewayAuthPrincipal(null, adminId, TYPE_ADMIN);
    }

    public boolean isUser() {
        return TYPE_USER.equals(type);
    }

    public boolean isAdmin() {
        return TYPE_ADMIN.equals(type);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return TYPE_ADMIN.equals(type)
                ? Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
                : Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return type + ":" + (userId != null ? userId : adminId);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
