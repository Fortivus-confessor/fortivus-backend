package br.arthconf.fortivus.service;

import br.arthconf.fortivus.domain.Usuario;
import br.arthconf.fortivus.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        var lista = usuarioRepository.findAllFetched();
        return lista != null ? new ArrayList<>(lista) : new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscarPorCentro(UUID centroId) {
        var lista = usuarioRepository.findByCentroComandoId(centroId);
        return lista != null ? new ArrayList<>(lista) : new ArrayList<>();
    }

    @Transactional
    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(UUID id) {
        return usuarioRepository.findByIdFetched(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @Transactional
    public void deletar(UUID id) {
        usuarioRepository.deleteById(id);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Usuario> listarPaginado(org.springframework.data.domain.Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }
}
