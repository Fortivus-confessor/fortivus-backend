package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.DeletarEscalaUseCase;
import br.arthconf.fortivus.application.port.out.EscalaRepositoryPort;
import br.arthconf.fortivus.domain.EstadoOperacionalUsuario;
import br.arthconf.fortivus.infrastructure.persistence.entity.EscalaEntity;
import br.arthconf.fortivus.infrastructure.persistence.repository.SpringDataUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeletarEscalaService implements DeletarEscalaUseCase {

    private final EscalaRepositoryPort escalaRepositoryPort;
    private final SpringDataUsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public void executar(UUID id) {
        EscalaEntity entity = escalaRepositoryPort.buscarEntidadePorId(id);

        if (entity.isAtiva() && entity.getIntegrantes() != null) {
            entity.getIntegrantes().forEach(u -> u.setEstadoOperacional(EstadoOperacionalUsuario.DISPONIVEL));
            usuarioRepository.saveAll(entity.getIntegrantes());
        }

        escalaRepositoryPort.deletar(id);
    }
}
