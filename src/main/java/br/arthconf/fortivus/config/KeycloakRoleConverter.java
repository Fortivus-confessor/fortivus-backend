package br.arthconf.fortivus.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<String> allRoles = new ArrayList<>();

        // 1. Realm Roles (Keycloak)
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            List<String> realmRoles = (List<String>) realmAccess.get("roles");
            if (realmRoles != null) allRoles.addAll(realmRoles);
        }

        // 2. Client Roles (Keycloak)
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null) {
            Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get("fortivus-web");
            if (clientAccess != null && clientAccess.containsKey("roles")) {
                List<String> clientRoles = (List<String>) clientAccess.get("roles");
                if (clientRoles != null) allRoles.addAll(clientRoles);
            }
        }

        return allRoles.stream()
                .distinct()
                .map(role -> {
                    // Evita duplicar o prefixo ROLE_
                    if (role.startsWith("ROLE_")) return new SimpleGrantedAuthority(role);
                    return new SimpleGrantedAuthority("ROLE_" + role.toUpperCase().replace(" ", "_"));
                })
                .collect(Collectors.toList());
    }
}
