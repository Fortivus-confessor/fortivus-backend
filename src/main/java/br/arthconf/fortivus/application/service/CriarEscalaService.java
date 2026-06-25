package br.arthconf.fortivus.application.service;

import br.arthconf.fortivus.application.port.in.CriarEscalaUseCase;
import br.arthconf.fortivus.application.port.out.EscalaRepositoryPort;
import br.arthconf.fortivus.domain.EstadoOperacionalUsuario;
import br.arthconf.fortivus.domain.model.Escala;
import br.arthconf.fortivus.infrastructure.persistence.entity.EscalaEntity;
import br.arthconf.fortivus.infrastructure.persistence.entity.UsuarioEntity;
import br.arthconf.fortivus.infrastructure.persistence.repository.SpringDataUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CriarEscalaService implements CriarEscalaUseCase {

    private final EscalaRepositoryPort escalaRepositoryPort;
    private final SpringDataUsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public Escala executar(Command command) {
        List<UsuarioEntity> integrantes = usuarioRepository.findAllById(command.integrantesIds());

        EscalaEntity escalaAtualEntity = null;
        if (command.id() != null) {
            escalaAtualEntity = escalaRepositoryPort.buscarEntidadePorId(command.id());
        }

        List<EscalaEntity> ativas = getEscalasAtivas();
        final EscalaEntity escalaExistente = escalaAtualEntity;

        List<UsuarioEntity> aptos = filtrarIntegrantesAptos(integrantes, command, ativas, escalaExistente);

        if (aptos.size() != command.integrantesIds().size()) {
            throw new RuntimeException("Um ou mais integrantes selecionados estão em Férias/Afastados ou já estão escalados neste período.");
        }

        // Libera integrantes removidos de escala existente
        if (escalaExistente != null && escalaExistente.getIntegrantes() != null) {
            Set<UUID> novosIds = aptos.stream().map(UsuarioEntity::getId).collect(Collectors.toSet());
            List<UsuarioEntity> removidos = escalaExistente.getIntegrantes().stream()
                    .filter(u -> !novosIds.contains(u.getId()))
                    .collect(Collectors.toList());
            removidos.forEach(u -> u.setEstadoOperacional(EstadoOperacionalUsuario.DISPONIVEL));
            usuarioRepository.saveAll(removidos);
        }

        aptos.forEach(u -> u.setEstadoOperacional(EstadoOperacionalUsuario.EM_MISSAO));
        usuarioRepository.saveAll(aptos);

        List<UUID> integrantesIds = aptos.stream().map(UsuarioEntity::getId).collect(Collectors.toList());

        Escala escala = Escala.builder()
                .id(command.id())
                .equipeId(command.equipeId())
                .veiculoId(command.veiculoId())
                .comandanteId(command.comandanteId())
                .dataInicio(command.dataInicio())
                .dataFim(command.dataFim())
                .ativa(true)
                .integrantesIds(integrantesIds)
                .build();

        return escalaRepositoryPort.salvar(escala, integrantesIds);
    }

    private List<EscalaEntity> getEscalasAtivas() {
        return escalaRepositoryPort.listarAtivas().stream()
                .map(e -> escalaRepositoryPort.buscarEntidadePorId(e.getId()))
                .collect(Collectors.toList());
    }

    private List<UsuarioEntity> filtrarIntegrantesAptos(
            List<UsuarioEntity> integrantes,
            Command command,
            List<EscalaEntity> ativas,
            EscalaEntity escalaAtual) {

        return integrantes.stream()
                .filter(u -> {
                    EstadoOperacionalUsuario estado = u.getEstadoOperacional();
                    if (estado == EstadoOperacionalUsuario.FERIAS ||
                        estado == EstadoOperacionalUsuario.AFASTADO_SAUDE ||
                        estado == EstadoOperacionalUsuario.LICENCA) return false;

                    if (command.dataInicio() == null || command.dataFim() == null) return true;

                    long start = command.dataInicio().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    long end = command.dataFim().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

                    for (EscalaEntity e : ativas) {
                        if (e.getId() != null && e.getId().equals(command.id())) continue;

                        boolean inScale = (e.getComandante() != null && e.getComandante().getId().equals(u.getId()))
                                || (e.getIntegrantes() != null && e.getIntegrantes().stream().anyMatch(i -> i.getId().equals(u.getId())));

                        if (inScale && e.getDataInicio() != null && e.getDataFim() != null) {
                            long eStart = e.getDataInicio().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                            long eEnd = e.getDataFim().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                            if (start <= eEnd && end >= eStart) return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }
}
