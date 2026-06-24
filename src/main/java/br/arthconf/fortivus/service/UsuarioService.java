package br.arthconf.fortivus.service;

import br.arthconf.fortivus.domain.model.Usuario;
import br.arthconf.fortivus.application.port.out.UsuarioPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioPort usuarioPort;

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        var lista = usuarioPort.findAllFetched();
        return lista != null ? new ArrayList<>(lista) : new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscarPorCentro(UUID centroId) {
        var lista = usuarioPort.findByCentroComandoId(centroId);
        return lista != null ? new ArrayList<>(lista) : new ArrayList<>();
    }

    @Transactional
    public Usuario salvar(Usuario usuario) {
        return usuarioPort.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(UUID id) {
        return usuarioPort.findByIdFetched(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @Transactional
    public void deletar(UUID id) {
        usuarioPort.deleteById(id);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Usuario> listarPaginado(org.springframework.data.domain.Pageable pageable) {
        return usuarioPort.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Usuario getUsuarioLogado() {
        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.oauth2.jwt.Jwt jwt) {
            String username = jwt.getClaimAsString("preferred_username");
            if (username != null) {
                var byEmail = usuarioPort.findByEmailIgnoreCase(username);
                if (byEmail.isPresent()) return byEmail.get();

                String emailClaim = jwt.getClaimAsString("email");
                if (emailClaim != null) {
                    var byEmailClaim = usuarioPort.findByEmailIgnoreCase(emailClaim);
                    if (byEmailClaim.isPresent()) return byEmailClaim.get();
                }

                var byCpf = usuarioPort.findByCpf(username);
                if (byCpf.isPresent()) return byCpf.get();
            }
        }
        return null;
    }
}
