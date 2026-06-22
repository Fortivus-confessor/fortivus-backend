package br.arthconf.fortivus.service;

import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class KeycloakService {

    @Value("${keycloak.server-url:http://localhost:9000}")
    private String serverUrl;

    @Value("${keycloak.realm:fortivus}")
    private String realm;

    @Value("${keycloak.admin.username:admin}")
    private String adminUsername;

    @Value("${keycloak.admin.password:admin}")
    private String adminPassword;

    @Value("${keycloak.admin.client-id:admin-cli}")
    private String adminClientId;

    private Keycloak getKeycloakInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master") // always auth to master for admin-cli
                .clientId(adminClientId)
                .username(adminUsername)
                .password(adminPassword)
                .build();
    }

    public void criarUsuario(String email, String primeiroNome, String sobrenome, String senha, String roleName) {
        try (Keycloak keycloak = getKeycloakInstance()) {
            UserRepresentation user = new UserRepresentation();
            user.setEnabled(true);
            user.setUsername(email);
            user.setEmail(email);
            user.setFirstName(primeiroNome);
            user.setLastName(sobrenome);
            user.setEmailVerified(true);

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setTemporary(false);
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(senha);

            user.setCredentials(Collections.singletonList(credential));

            Response response = keycloak.realm(realm).users().create(user);
            if (response.getStatus() != 201) {
                if (response.getStatus() == 409) {
                    throw new RuntimeException("Usuário já existe no Keycloak (E-mail duplicado).");
                }
                throw new RuntimeException("Erro ao criar usuário no Keycloak. Status: " + response.getStatus());
            }

            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

            // Assinalar role
            try {
                var role = keycloak.realm(realm).roles().get(roleName).toRepresentation();
                keycloak.realm(realm).users().get(userId).roles().realmLevel().add(Collections.singletonList(role));
            } catch (Exception e) {
                // If role assignment fails, we ignore or log. The user is created anyway.
                e.printStackTrace();
            }
        }
    }

    public void atualizarUsuario(String oldEmail, String newEmail, String primeiroNome, String sobrenome, String roleName) {
        try (Keycloak keycloak = getKeycloakInstance()) {
            var users = keycloak.realm(realm).users().searchByUsername(oldEmail, true);
            if (users == null || users.isEmpty()) {
                System.out.println("Usuário não encontrado no Keycloak para o e-mail antigo: " + oldEmail);
                return;
            }
            UserRepresentation user = users.get(0);
            String userId = user.getId();

            user.setUsername(newEmail);
            user.setEmail(newEmail);
            user.setFirstName(primeiroNome);
            user.setLastName(sobrenome);
            keycloak.realm(realm).users().get(userId).update(user);

            // Atualizar role
            try {
                // Remover roles antigas e assinalar a nova
                var realmRoles = keycloak.realm(realm).users().get(userId).roles().realmLevel().listAll();
                keycloak.realm(realm).users().get(userId).roles().realmLevel().remove(realmRoles);
                
                var newRole = keycloak.realm(realm).roles().get(roleName).toRepresentation();
                keycloak.realm(realm).users().get(userId).roles().realmLevel().add(Collections.singletonList(newRole));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
