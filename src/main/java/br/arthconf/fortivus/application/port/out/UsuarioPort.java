package br.arthconf.fortivus.application.port.out;

import br.arthconf.fortivus.domain.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioPort {
    Optional<Usuario> findByEmailIgnoreCase(String email);
    Optional<Usuario> findByCpf(String cpf);
    List<Usuario> findAllFetched();
    List<Usuario> findByCentroComandoId(UUID centroId);
    Optional<Usuario> findByIdFetched(UUID id);
    Page<Usuario> findAll(Pageable pageable);
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(UUID id);
    void deleteById(UUID id);
}
