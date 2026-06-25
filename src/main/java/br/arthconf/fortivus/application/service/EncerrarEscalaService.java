package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.EncerrarEscalaUseCase;
import br.arthconf.fortivus.application.port.out.EscalaRepositoryPort;
import br.arthconf.fortivus.domain.EstadoOperacionalUsuario;
import br.arthconf.fortivus.domain.model.Escala;
import br.arthconf.fortivus.infrastructure.persistence.entity.EscalaEntity;
import br.arthconf.fortivus.infrastructure.persistence.repository.SpringDataUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EncerrarEscalaService implements EncerrarEscalaUseCase {

    private final EscalaRepositoryPort escalaRepositoryPort;
    private final SpringDataUsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public void executar(UUID id) {
        EscalaEntity entity = escalaRepositoryPort.buscarEntidadePorId(id);
        entity.setAtiva(false);

        if (entity.getIntegrantes() != null) {
            entity.getIntegrantes().forEach(u -> u.setEstadoOperacional(EstadoOperacionalUsuario.DISPONIVEL));
            usuarioRepository.saveAll(entity.getIntegrantes());
        }

        Escala escala = Escala.builder()
                .id(entity.getId())
                .equipeId(entity.getEquipe() != null ? entity.getEquipe().getId() : null)
                .veiculoId(entity.getVeiculo() != null ? entity.getVeiculo().getId() : null)
                .comandanteId(entity.getComandante() != null ? entity.getComandante().getId() : null)
                .dataInicio(entity.getDataInicio())
                .dataFim(entity.getDataFim())
                .ativa(false)
                .build();

        escalaRepositoryPort.salvar(escala, null);
    }
}
