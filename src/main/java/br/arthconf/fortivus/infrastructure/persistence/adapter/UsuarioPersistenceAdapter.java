package br.arthconf.fortivus.infrastructure.persistence.adapter;

import br.arthconf.fortivus.application.port.out.UsuarioPort;
import br.arthconf.fortivus.domain.model.Usuario;
import br.arthconf.fortivus.infrastructure.persistence.entity.UsuarioEntity;
import br.arthconf.fortivus.infrastructure.persistence.mapper.UsuarioMapper;
import br.arthconf.fortivus.infrastructure.persistence.repository.SpringDataUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UsuarioPersistenceAdapter implements UsuarioPort {

    private final SpringDataUsuarioRepository repository;

    @Override
    public Optional<Usuario> findByEmailIgnoreCase(String email) {
        return repository.findByEmailIgnoreCase(email).map(UsuarioMapper::toDomain);
    }

    @Override
    public Optional<Usuario> findByCpf(String cpf) {
        return repository.findByCpf(cpf).map(UsuarioMapper::toDomain);
    }

    @Override
    public List<Usuario> findAllFetched() {
        return repository.findAllFetched().stream()
                .map(UsuarioMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Usuario> findByCentroComandoId(UUID centroId) {
        return repository.findByCentroComandoId(centroId).stream()
                .map(UsuarioMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Usuario> findByIdFetched(UUID id) {
        return repository.findByIdFetched(id).map(UsuarioMapper::toDomain);
    }

    @Override
    public Page<Usuario> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(UsuarioMapper::toDomain);
    }

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioEntity entity = UsuarioMapper.toEntity(usuario);
        UsuarioEntity saved = repository.save(entity);
        return UsuarioMapper.toDomain(saved);
    }

    @Override
    public Optional<Usuario> findById(UUID id) {
        return repository.findById(id).map(UsuarioMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
