package br.arthconf.fortivus.infrastructure.scheduling;

import br.arthconf.fortivus.application.port.out.EscalaRepositoryPort;
import br.arthconf.fortivus.domain.EstadoOperacionalUsuario;
import br.arthconf.fortivus.domain.model.Escala;
import br.arthconf.fortivus.infrastructure.persistence.entity.EscalaEntity;
import br.arthconf.fortivus.infrastructure.persistence.repository.SpringDataUsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EscalaCronJob {

    private final EscalaRepositoryPort escalaRepositoryPort;
    private final SpringDataUsuarioRepository usuarioRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void verificarEscalasExpiradas() {
        LocalDateTime agora = LocalDateTime.now();
        List<Escala> ativas = escalaRepositoryPort.listarAtivas();

        for (Escala escala : ativas) {
            if (escala.getDataFim() != null && escala.getDataFim().isBefore(agora)) {
                EscalaEntity entity = escalaRepositoryPort.buscarEntidadePorId(escala.getId());
                entity.setAtiva(false);

                if (entity.getIntegrantes() != null) {
                    entity.getIntegrantes().forEach(u -> u.setEstadoOperacional(EstadoOperacionalUsuario.DISPONIVEL));
                    usuarioRepository.saveAll(entity.getIntegrantes());
                }

                Escala escalaEncerrada = Escala.builder()
                        .id(entity.getId())
                        .equipeId(entity.getEquipe() != null ? entity.getEquipe().getId() : null)
                        .veiculoId(entity.getVeiculo() != null ? entity.getVeiculo().getId() : null)
                        .comandanteId(entity.getComandante() != null ? entity.getComandante().getId() : null)
                        .dataInicio(entity.getDataInicio())
                        .dataFim(entity.getDataFim())
                        .ativa(false)
                        .build();

                escalaRepositoryPort.salvar(escalaEncerrada, null);
                log.info("Escala {} encerrada automaticamente por expiração", escala.getId());
            }
        }
    }
}
