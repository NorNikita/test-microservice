package ru.pflb.boomq.test.args.provider;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class AuthorityProvider {

    public static SimpleGrantedAuthority getUserAuthority() {
        return new SimpleGrantedAuthority("ROLE_USER");
    }

    public static SimpleGrantedAuthority getAdminAuthority() {
        return new SimpleGrantedAuthority("ROLE_ADMIN");
    }
}