package br.arthconf.fortivus.controller;

import br.arthconf.fortivus.application.port.in.GerenciarUsuarioUseCase;
import br.arthconf.fortivus.application.port.in.ObterUsuarioLogadoUseCase;
import br.arthconf.fortivus.application.port.out.IdentityManagementPort;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/operacional/usuarios/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final ObterUsuarioLogadoUseCase obterUsuarioLogadoUseCase;
    private final GerenciarUsuarioUseCase gerenciarUsuarioUseCase;
    private final IdentityManagementPort keycloakService;

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
        var logado = obterUsuarioLogadoUseCase.getUsuarioLogado();
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

        gerenciarUsuarioUseCase.salvar(logado);

        if (isEmailChanged || (dto.getNome() != null && !dto.getNome().isBlank())) {
            keycloakService.atualizarUsuario(emailAntigo, logado.getEmail(), logado.getNome(), role);
        }

        if (dto.getNovaSenha() != null && !dto.getNovaSenha().isBlank()) {
            keycloakService.atualizarSenha(logado.getEmail(), dto.getNovaSenha());
        }

        return ResponseEntity.ok().build();
    }
}
