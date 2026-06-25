package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.GerenciarUsuarioUseCase;
import br.arthconf.fortivus.application.port.in.ListarUsuariosUseCase;
import br.arthconf.fortivus.application.port.in.ObterUsuarioLogadoUseCase;
import br.arthconf.fortivus.application.port.out.UsuarioPort;
import br.arthconf.fortivus.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioApplicationService implements ListarUsuariosUseCase, GerenciarUsuarioUseCase, ObterUsuarioLogadoUseCase {

    private final UsuarioPort usuarioPort;

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        var lista = usuarioPort.findAllFetched();
        return lista != null ? new ArrayList<>(lista) : new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> buscarPorCentro(UUID centroId) {
        var lista = usuarioPort.findByCentroComandoId(centroId);
        return lista != null ? new ArrayList<>(lista) : new ArrayList<>();
    }

    @Override
    @Transactional
    public Usuario salvar(Usuario usuario) {
        return usuarioPort.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario buscarPorId(UUID id) {
        return usuarioPort.findByIdFetched(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @Override
    @Transactional
    public void deletar(UUID id) {
        usuarioPort.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Usuario> listarPaginado(Pageable pageable) {
        return usuarioPort.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario getUsuarioLogado() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
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
