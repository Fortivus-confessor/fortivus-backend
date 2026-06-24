package br.arthconf.fortivus.service;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class KeycloakService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm:fortivus}")
    private String realm;

    public KeycloakService(
            @Value("${keycloak.server-url:http://localhost:9000}") String serverUrl,
            @Value("${keycloak.realm:fortivus}") String realm,
            @Value("${keycloak.client-id:admin-cli}") String clientId,
            @Value("${keycloak.username:admin}") String username,
            @Value("${keycloak.password:admin}") String password) {
        
        this.realm = realm;
        this.keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master") // Master realm holds admin credentials
                .clientId(clientId)
                .username(username)
                .password(password)
                .build();
    }

    public void criarUsuario(String email, String senha, String nome, String role) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(email);
        user.setEmail(email);
        
        if (nome != null && !nome.isBlank()) {
            String[] parts = nome.trim().split(" ", 2);
            user.setFirstName(parts[0]);
            user.setLastName(parts.length > 1 ? parts[1] : "");
        }
        
        user.setEnabled(true);

        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(senha);
        cred.setTemporary(false);

        user.setCredentials(Collections.singletonList(cred));

        Response response = keycloak.realm(realm).users().create(user);
        
        if (response.getStatus() == 201) {
            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            log.info("Usuário criado no Keycloak: {}", userId);
            atribuirRole(userId, role);
        } else {
            log.error("Erro ao criar usuário no Keycloak. Status: {}", response.getStatus());
            throw new RuntimeException("Falha ao criar usuário no Keycloak");
        }
    }

    public void atualizarUsuario(String emailAntigo, String novoEmail, String nome, String role) {
        List<UserRepresentation> users = keycloak.realm(realm).users().searchByEmail(emailAntigo, true);
        if (users != null && !users.isEmpty()) {
            UserRepresentation user = users.get(0);
            user.setEmail(novoEmail);
            
            if (nome != null && !nome.isBlank()) {
                String[] parts = nome.trim().split(" ", 2);
                user.setFirstName(parts[0]);
                user.setLastName(parts.length > 1 ? parts[1] : "");
            }

            try {
                keycloak.realm(realm).users().get(user.getId()).update(user);
                atualizarRole(user.getId(), role);
                log.info("Usuário atualizado no Keycloak: {}", user.getId());
            } catch (jakarta.ws.rs.WebApplicationException e) {
                log.error("Erro do Keycloak ao atualizar usuario: {}", e.getResponse().readEntity(String.class), e);
                throw new RuntimeException("Falha ao atualizar usuário no Keycloak", e);
            }
        } else {
            log.warn("Usuário {} não encontrado no Keycloak para atualização", emailAntigo);
        }
    }

    public void deletarUsuario(String email) {
        List<UserRepresentation> users = keycloak.realm(realm).users().searchByEmail(email, true);
        if (users != null && !users.isEmpty()) {
            keycloak.realm(realm).users().get(users.get(0).getId()).remove();
            log.info("Usuário deletado do Keycloak: {}", email);
        } else {
            log.warn("Usuário {} não encontrado no Keycloak para exclusão", email);
        }
    }

    public void atualizarSenha(String email, String novaSenha) {
        List<UserRepresentation> users = keycloak.realm(realm).users().searchByEmail(email, true);
        if (users != null && !users.isEmpty()) {
            String userId = users.get(0).getId();
            CredentialRepresentation cred = new CredentialRepresentation();
            cred.setType(CredentialRepresentation.PASSWORD);
            cred.setValue(novaSenha);
            cred.setTemporary(false);
            keycloak.realm(realm).users().get(userId).resetPassword(cred);
            log.info("Senha atualizada no Keycloak para o email: {}", email);
        } else {
            log.warn("Usuário com email {} não encontrado no Keycloak para atualizar a senha", email);
        }
    }

    private void atribuirRole(String userId, String roleName) {
        try {
            RoleRepresentation realmRole = keycloak.realm(realm).roles().get(roleName).toRepresentation();
            keycloak.realm(realm).users().get(userId).roles().realmLevel().add(Collections.singletonList(realmRole));
        } catch (Exception e) {
            log.error("Erro ao atribuir role {} ao usuario {}", roleName, userId, e);
        }
    }
    
    private void atualizarRole(String userId, String newRoleName) {
        try {
            List<RoleRepresentation> currentRoles = keycloak.realm(realm).users().get(userId).roles().realmLevel().listAll();
            keycloak.realm(realm).users().get(userId).roles().realmLevel().remove(currentRoles);
            atribuirRole(userId, newRoleName);
        } catch (Exception e) {
            log.error("Erro ao atualizar role {} do usuario {}", newRoleName, userId, e);
        }
    }
}
