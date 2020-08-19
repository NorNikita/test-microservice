package ru.pflb.boomq.test.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;

public class GrantedAuthoritiesExtractor implements Converter<Jwt, Collection<GrantedAuthority>> {
    private static final String DEFAULT_AUTHORITY_PREFIX = "SCOPE_";

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        Collection<String> authorities = (Collection<String>) jwt.getClaims().get("authorities");
        Collection<String> scopes = (Collection<String>) jwt.getClaims().get("scope");

        scopes.forEach(scope -> grantedAuthorities.add(new SimpleGrantedAuthority(DEFAULT_AUTHORITY_PREFIX + scope)));
        if (null != authorities) {
            authorities.forEach(role -> grantedAuthorities.add(new SimpleGrantedAuthority(role)));
        }

        return grantedAuthorities;
    }
}