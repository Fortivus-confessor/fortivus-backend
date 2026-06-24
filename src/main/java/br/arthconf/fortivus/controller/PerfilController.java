package br.arthconf.fortivus.controller;

import br.arthconf.fortivus.domain.model.Usuario;
import br.arthconf.fortivus.service.UsuarioService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/operacional/usuarios/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final UsuarioService usuarioService;
    private final br.arthconf.fortivus.service.KeycloakService keycloakService;

    @Data
    public static class PerfilUpdateDTO {
        private String nome;
        private String email;
        private String senhaAtual;
        private String novaSenha;
    }

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> atualizarPerfil(@RequestBody PerfilUpdateDTO dto) {
        Usuario logado = usuarioService.getUsuarioLogado();
        if (logado == null) return ResponseEntity.status(401).build();

        String emailAntigo = logado.getEmail();
        String role = logado.getPerfil() != null ? logado.getPerfil().name() : "OPERADOR";

        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            logado.setNome(dto.getNome());
        }
        
        boolean isEmailChanged = false;
        if (dto.getEmail() != null && !dto.getEmail().isBlank() && !dto.getEmail().equalsIgnoreCase(emailAntigo)) {
            logado.setEmail(dto.getEmail());
            isEmailChanged = true;
        }

        usuarioService.salvar(logado);

        if (isEmailChanged || (dto.getNome() != null && !dto.getNome().isBlank())) {
            keycloakService.atualizarUsuario(emailAntigo, logado.getEmail(), logado.getNome(), role);
        }

        if (dto.getNovaSenha() != null && !dto.getNovaSenha().isBlank()) {
            // Need to update using the NEW email in case it changed
            keycloakService.atualizarSenha(logado.getEmail(), dto.getNovaSenha());
        }

        return ResponseEntity.ok().build();
    }
}
